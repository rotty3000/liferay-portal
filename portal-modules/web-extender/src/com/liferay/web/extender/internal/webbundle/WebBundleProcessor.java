/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.web.extender.internal.webbundle;

import com.liferay.portal.events.GlobalStartupAction;
import com.liferay.portal.kernel.deploy.auto.AutoDeployException;
import com.liferay.portal.kernel.deploy.auto.AutoDeployListener;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.io.FileFilter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.servlet.PortalClassLoaderFilter;
import com.liferay.portal.kernel.servlet.PortalClassLoaderServlet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UniqueList;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.xml.XPath;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.tools.deploy.BaseDeployer;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.dynamicdatamapping.util.DDMXMLUtil;
import com.liferay.util.ant.ExpandTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Constants;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.depend.DependencyVisitor;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WebBundleProcessor implements ModuleFrameworkConstants {

	public WebBundleProcessor(
		ClassLoader systemBundleClassLoader, File file,
		Map<String, String[]> parameterMap) {

		_systemBundleClassLoader = systemBundleClassLoader;
		_file = file;
		_parameterMap = parameterMap;

		_baseDeployer = new BaseDeployer();

		_baseDeployer.setAppServerType(ServerDetector.getServerId());
	}

	public void process() throws IOException {
		String webContextpath = MapUtil.getString(
			_parameterMap, WEB_CONTEXTPATH);

		if (!webContextpath.startsWith(StringPool.SLASH)) {
			webContextpath = StringPool.SLASH.concat(webContextpath);
		}

		AutoDeploymentContext autoDeploymentContext =
			buildAutoDeploymentContext(webContextpath);

		doAutoDeploy(autoDeploymentContext);

		_deployedAppFolder = autoDeploymentContext.getDeployDir();

		if (!PropsValues.AUTO_DEPLOY_UNPACK_WAR) {
			File[] listFiles = _deployedAppFolder.getParentFile().listFiles();

			if ((listFiles == null) || (listFiles.length == 0)) {
				// TODO Something happened here, but not sure what yet.

				return;
			}

			File file = listFiles[0];

			_deployedAppFolder.mkdirs();

			ExpandTask.expand(file, _deployedAppFolder);
		}

		if ((_deployedAppFolder == null) || !_deployedAppFolder.exists() ||
			!_deployedAppFolder.isDirectory()) {

			return;
		}

		Manifest manifest = _getManifest();

		Attributes attributes = manifest.getMainAttributes();

		// If it's not a bundle, then we need to manipulate it into one. The
		// spec states that this is only true when the Manifest does not contain
		// a Bundle_SymbolicName header.

		String bundleSymbolicName = GetterUtil.getString(
			attributes.getValue(Constants.BUNDLE_SYMBOLICNAME));
		boolean force = GetterUtil.getBoolean(
			attributes.getValue(
				ModuleFrameworkConstants.LIFERAY_FORCE_WAB_PROCESSING), false);

		if (Validator.isNull(bundleSymbolicName) || force) {
			_processPaths(
				_resourcePaths, _deployedAppFolder, _deployedAppFolder.toURI(),
				webContextpath);

			// The order of these operations is important

			processBundleSymbolicName(attributes, webContextpath);
			processBundleVersion(attributes);
			processBundleManifestVersion(attributes);
			processPortletXML(webContextpath);
			processWebXML("WEB-INF/web.xml");
			processWebXML("WEB-INF/liferay-web.xml");
			processLiferayPortletXML(webContextpath);
			processBundleClassPath(attributes);
			processDeclarativeReferences(attributes);
			processExportImportPackage(attributes);
			processPluginDependencies(attributes);
		}

		// We still have to add the specified Web-ContextPath even if it already
		// exists, because this allows changing to an alternate context.

		attributes.putValue(WEB_CONTEXTPATH, webContextpath);

		Object manifestVersion = attributes.get(
			Attributes.Name.MANIFEST_VERSION.toString());

		if (manifestVersion == null) {
			attributes.putValue(
				Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
		}

		_saveManifest(manifest);
	}

	public java.io.InputStream getInputStream() throws IOException {
		if (!_deployedAppFolder.exists() || !_deployedAppFolder.isDirectory()) {
			return null;
		}

		ZipWriter zipWriter = ZipWriterFactoryUtil.getZipWriter();

		Set<String> processedPaths = new HashSet<String>();

		File manifestFile = _getManifestFile();

		writePath(
			manifestFile, zipWriter, processedPaths,
			_MANIFEST_PATH);

		writeJarPaths(
			_deployedAppFolder, _deployedAppFolder.toURI(), zipWriter,
			processedPaths);

		return new FileInputStream(zipWriter.getFile());
	}

	protected AutoDeploymentContext buildAutoDeploymentContext(String context) {
		File destDir = new File(_file.getParentFile(), "deploy");

		destDir.mkdirs();

		AutoDeploymentContext autoDeploymentContext =
			new AutoDeploymentContext();

		autoDeploymentContext.setContext(context);
		autoDeploymentContext.setDestDir(destDir.getAbsolutePath());
		autoDeploymentContext.setFile(_file);

		return autoDeploymentContext;
	}

	protected void doAutoDeploy(AutoDeploymentContext autoDeploymentContext) {
		List<AutoDeployListener> autoDeployListeners =
			GlobalStartupAction.getAutoDeployListeners();

		for (AutoDeployListener autoDeployListener : autoDeployListeners) {
			try {
				autoDeployListener.deploy(autoDeploymentContext);
			}
			catch (AutoDeployException ade) {
				_log.error(ade);
			}
		}
	}

	protected ClassLoader getSystemBundleClassLoader() {
		return _systemBundleClassLoader;
	}

	protected void processBundleClassPath(Attributes attributes) {
		StringBundler sb = new StringBundler(1 + (_resourcePaths.size() * 2));

		sb.append("WEB-INF/classes/");

		if (_resourcePaths.contains("WEB-INF/lib/")) {
			for (String path : _resourcePaths) {
				if (!path.startsWith("WEB-INF/lib/") ||
					!path.endsWith(".jar") ||
					ArrayUtil.contains(_EXCLUDED_CLASS_PATHS, path)) {

					continue;
				}

				sb.append(", ");
				sb.append(path);
			}
		}

		attributes.putValue(Constants.BUNDLE_CLASSPATH, sb.toString());
	}

	protected void processBundleManifestVersion(Attributes attributes) {
		String bundleManifestVersion = MapUtil.getString(
			_parameterMap, Constants.BUNDLE_MANIFESTVERSION);

		if (Validator.isNull(bundleManifestVersion)) {
			bundleManifestVersion = "2";
		}

		attributes.putValue(
			Constants.BUNDLE_MANIFESTVERSION, bundleManifestVersion);
	}

	protected void processBundleSymbolicName(
		Attributes attributes, String webContextpath) {

		String bundleSymbolicName = MapUtil.getString(
			_parameterMap, Constants.BUNDLE_SYMBOLICNAME);

		if (Validator.isNull(bundleSymbolicName)) {
			bundleSymbolicName = webContextpath.substring(1);
		}

		attributes.putValue(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
	}

	protected void processBundleVersion(Attributes attributes) {
		_version = MapUtil.getString(_parameterMap, Constants.BUNDLE_VERSION);

		if (Validator.isNull(_version)) {
			PluginPackage readPluginPackage = _baseDeployer.readPluginPackage(
				_deployedAppFolder);

			_version = readPluginPackage.getVersion();
		}

		attributes.putValue(Constants.BUNDLE_VERSION, _version);
	}

	protected void processClass(
			DependencyVisitor dependencyVisitor, String className,
			InputStream inputStream, List<String> packageList)
		throws IOException {

		if (inputStream == null) {
			return;
		}

		try {
			ClassReader classReader = new ClassReader(inputStream);

			classReader.accept(dependencyVisitor, 0);

			Set<String> packages = dependencyVisitor.getPackages();

			for (String packageName : packages) {
				packageName = packageName.replaceAll(
					StringPool.SLASH, StringPool.PERIOD);

				if (packageName.startsWith("com.sun.") ||
					packageName.startsWith("sun.")) {

					continue;
				}

				packageList.add(packageName);
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	protected void processClassDependencies(File classFile) throws IOException {
		FileInputStream fis = new FileInputStream(classFile);

		processClassDependencies(classFile.getName(), fis);
	}

	protected void processClassDependencies(
			String className, InputStream inputStream)
		throws IOException {

		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		processClass(
			dependencyVisitor, className, inputStream, _referencedPackages);

		Set<String> jarPackages = dependencyVisitor.getGlobals().keySet();

		for (String jarPackage : jarPackages) {
			_classProvidedPackages.add(
				jarPackage.replaceAll(StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void processDeclarativeReferences(Attributes attributes)
		throws IOException {

		// References from web.xml

		File xml = new File(_deployedAppFolder, "WEB-INF/web.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _WEBXML_CLASSREFERENCE_ELEMENTS, "x",
				"http://java.sun.com/xml/ns/j2ee");

			for (String value :
					PropsValues.
						MODULE_FRAMEWORK_WEB_EXTENDER_DEFAULT_SERVLET_PACKAGES) {

				int pos = value.indexOf(StringPool.SEMICOLON);

				if (pos != -1) {
					value = value.substring(0, pos);
				}

				_referencedPackages.add(value.trim());
			}
		}

		// References from *.tld

		File tldFolder = new File(_deployedAppFolder, "WEB-INF/tld");

		if (tldFolder.exists() && tldFolder.isDirectory()) {
			File[] listFiles = tldFolder.listFiles(new FileFilter(".*\\.tld"));

			for (File tldFile : listFiles) {
				processTldDependencies(tldFile);
			}
		}

		// References from portlet.xml

		xml = new File(_deployedAppFolder, "WEB-INF/portlet.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _PORTLETXML_CLASSREFERENCE_ELEMENTS, "x",
				"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd");

			for (String value :
					PropsValues.
						MODULE_FRAMEWORK_WEB_EXTENDER_DEFAULT_PORTLET_PACKAGES) {

				int pos = value.indexOf(StringPool.SEMICOLON);

				if (pos != -1) {
					value = value.substring(0, pos);
				}

				_referencedPackages.add(value.trim());
			}
		}

		// References from liferay-web.xml

		// TODO do we really need this?

		// References from liferay-portlet.xml

		xml = new File(_deployedAppFolder, "WEB-INF/liferay-portlet.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _LIFERAYPORTLETXML_CLASSREFERENCE_ELEMENTS, null,
				null);
		}

		// References from liferay-hook.xml

		xml = new File(_deployedAppFolder, "WEB-INF/liferay-hook.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _LIFERAYHOOKXML_CLASSREFERENCE_ELEMENTS, null,
				null);
		}
	}

	protected void processExportImportPackage(Attributes attributes)
		throws IOException {

		_referencedPackages.addAll(_jarReferencedPackages);

		for (String packageName : _referencedPackages) {
			if (packageName.startsWith("java.") ||
				_jarProvidedPackages.contains(packageName)) {

				continue;
			}

			if (_classProvidedPackages.contains(packageName)) {
				_exportPackages.add(packageName);
			}
			else {
				_importPackages.add(packageName);
			}
		}

		for (String packageName : _classProvidedPackages) {
			_exportPackages.add(packageName);
		}

		String[] privatePackages = StringUtil.split(
			GetterUtil.getString(attributes.getValue("Private-Package")));

		if (!_exportPackages.isEmpty()) {
			Collections.sort(_exportPackages);

			for (Iterator<String> itr = _exportPackages.iterator();
					itr.hasNext();) {

				if (!ArrayUtil.contains(privatePackages, itr.next())) {
					itr.remove();
				}
			}

			if (!_exportPackages.isEmpty()) {
				String exportPackages = StringUtil.merge(
					_exportPackages,
					";version=\"".concat(_version).concat("\","));

				attributes.putValue(
					Constants.EXPORT_PACKAGE,
					exportPackages.concat(";version=\"").concat(
						_version).concat("\""));
			}
		}

		String importPackage = MapUtil.getString(
			_parameterMap, Constants.IMPORT_PACKAGE);

		if (Validator.isNotNull(importPackage)) {
			attributes.putValue(Constants.IMPORT_PACKAGE, importPackage);
		}
		else if (!_importPackages.isEmpty()) {
			Collections.sort(_importPackages);

			StringBundler sb = new StringBundler(_importPackages.size() * 3);

			for (Iterator<String> itr = _importPackages.iterator();
					itr.hasNext();) {

				String packageName = itr.next();

				sb.append(packageName);

				if (_jarReferencedPackages.contains(packageName)) {
					sb.append(";resolution:=\"optional\"");
				}

				if (itr.hasNext()) {
					sb.append(StringPool.COMMA);
				}
			}

			attributes.putValue(Constants.IMPORT_PACKAGE, sb.toString());
		}
	}

	protected void processJarDependencies(
			File jarFile, List<String> jarProvidedPackages)
		throws IOException {

		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		ZipFile zipFile = new ZipFile(jarFile);

		Enumeration<? extends ZipEntry> en = zipFile.entries();

		while (en.hasMoreElements()) {
			ZipEntry zipEntry = en.nextElement();

			String name = zipEntry.getName();

			if (name.endsWith(".class")) {
				InputStream inputStream = zipFile.getInputStream(zipEntry);

				processClass(
					dependencyVisitor, name, inputStream,
					_jarReferencedPackages);
			}
		}

		Set<String> jarPackages = dependencyVisitor.getGlobals().keySet();

		for (String jarPackage : jarPackages) {
			jarProvidedPackages.add(
				jarPackage.replaceAll(StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void processJspDependencies(File jspFile) throws IOException {
		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		ClassLoader classLoader = getSystemBundleClassLoader();

		String content = FileUtil.read(jspFile);

		Matcher matcher = _jspImportedPackagesPattern.matcher(content);

		while (matcher.find()) {
			String value = matcher.group(1);

			InputStream inputStream = classLoader.getResourceAsStream(
				value.replace('.', '/') + ".class");

			processClass(
				dependencyVisitor, value, inputStream, _referencedPackages);
		}

		Set<String> jarPackages = dependencyVisitor.getGlobals().keySet();

		for (String jarPackage : jarPackages) {
			_referencedPackages.add(
				jarPackage.replaceAll(StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void processLiferayPortletXML(String webContextpath)
		throws IOException {

		File liferayPortletXMLFile = new File(
			_deployedAppFolder, "WEB-INF/liferay-portlet.xml");

		if (!liferayPortletXMLFile.exists()) {
			return;
		}

		String content = FileUtil.read(liferayPortletXMLFile);

		Document liferayPortletXMLDoc = null;

		try {
			liferayPortletXMLDoc = SAXReaderUtil.read(content);
		}
		catch (DocumentException de) {
			throw new IOException(de);
		}

		Element rootEl = liferayPortletXMLDoc.getRootElement();

		List<Element> portletElements = rootEl.elements("portlet");

		for (Element portletElement : portletElements) {
			Element previousChild = portletElement.element("virtual-path");

			if (previousChild == null) {
				previousChild = portletElement.element("icon");
			}

			if (previousChild == null) {
				previousChild = portletElement.element("portlet-name");
			}

			Element strutsPathElement = portletElement.element("struts-path");

			if (strutsPathElement == null) {
				List<Node> children = portletElement.content();

				int pos = children.indexOf(previousChild);

				strutsPathElement = SAXReaderUtil.createElement(
					"struts-path");

				strutsPathElement.setText(MODULE.concat(webContextpath));

				children.add(pos + 1, strutsPathElement);
			}
			else {
				String strutsPath = strutsPathElement.getTextTrim();

				if (!strutsPath.startsWith(StringPool.SLASH)) {
					strutsPath = StringPool.SLASH.concat(strutsPath);
				}

				strutsPath = MODULE.concat(webContextpath).concat(strutsPath);

				strutsPathElement.setText(strutsPath);
			}
		}

		content = DDMXMLUtil.formatXML(liferayPortletXMLDoc);

		FileUtil.write(liferayPortletXMLFile, content);
	}

	protected void processPluginDependencies(Attributes attributes) {
		PluginPackage readPluginPackage = _baseDeployer.readPluginPackage(
			_deployedAppFolder);

		List<String> requiredDeploymentContexts =
			readPluginPackage.getRequiredDeploymentContexts();

		if (requiredDeploymentContexts == null ||
				requiredDeploymentContexts.isEmpty()) {
			return;
		}

		int requiredDeploymentContextsSize = requiredDeploymentContexts.size();

		StringBundler sb = new StringBundler(
			6 * requiredDeploymentContextsSize);

		int i = 0;

		for (; i < requiredDeploymentContextsSize - 1; i++) {
			sb.append(requiredDeploymentContexts.get(i));
			sb.append(StringPool.SEMICOLON);
			sb.append(Constants.BUNDLE_VERSION_ATTRIBUTE);
			sb.append(StringPool.EQUAL);
			sb.append(_version);
			sb.append(StringPool.COMMA);
		}

		if (i < requiredDeploymentContextsSize) {
			sb.append(requiredDeploymentContexts.get(i));
			sb.append(StringPool.SEMICOLON);
			sb.append(Constants.BUNDLE_VERSION_ATTRIBUTE);
			sb.append(StringPool.EQUAL);
			sb.append(_version);
		}

		String requiredBundles = sb.toString();

		if (Validator.isNotNull(requiredBundles)) {
			attributes.putValue(Constants.REQUIRE_BUNDLE, sb.toString());
		}

	}

	protected void processPortletXML(String webContextpath)
		throws IOException {

		File portletXMLFile = new File(
			_deployedAppFolder, "WEB-INF/" +
				Portal.PORTLET_XML_FILE_NAME_STANDARD);

		if (!portletXMLFile.exists()) {
			return;
		}

		String content = FileUtil.read(portletXMLFile);

		Document document = null;

		try {
			document = SAXReaderUtil.read(content);
		}
		catch (DocumentException de) {
			throw new IOException(de);
		}

		Element rootElement = document.getRootElement();

		List<Element> portletElements = rootElement.elements("portlet");

		for (Element portletElement : portletElements) {
			String portletName = portletElement.elementText("portlet-name");

			String invokerPortletName = MODULE.concat(webContextpath).concat(
				StringPool.SLASH).concat(portletName);

			XPath xPath = SAXReaderUtil.createXPath(
				_INVOKER_PORTLET_NAME_XPATH, "x",
				"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd" );

			Element invokerPortletNameEl = (Element)xPath.selectSingleNode(
				portletElement);

			if (invokerPortletNameEl == null) {
				Element portletClassElement = portletElement.element(
					"portlet-class");

				List<Node> children = portletElement.content();

				int pos = children.indexOf(portletClassElement);

				QName qName = rootElement.getQName();

				Element initParamElement = SAXReaderUtil.createElement(
					SAXReaderUtil.createQName(
						"init-param", qName.getNamespace()));

				initParamElement.addElement("name").setText(
					"com.liferay.portal.invokerPortletName");
				initParamElement.addElement("value").setText(
					invokerPortletName);

				children.add(pos + 1, initParamElement);
			}
			else {
				Element valueElement = invokerPortletNameEl.element("value");

				invokerPortletName = valueElement.getTextTrim();

				if (!invokerPortletName.startsWith(StringPool.SLASH)) {
					invokerPortletName = StringPool.SLASH.concat(
						invokerPortletName);
				}

				invokerPortletName = MODULE.concat(webContextpath).concat(
					invokerPortletName);

				valueElement.setText(invokerPortletName);
			}
		}

		content = DDMXMLUtil.formatXML(document);

		FileUtil.write(portletXMLFile, content);
	}

	protected void processWebXML(String descriptor) throws IOException {
		File webXMLFile = new File(_deployedAppFolder, descriptor);

		if (!webXMLFile.exists()) {
			return;
		}

		String content = FileUtil.read(webXMLFile);

		Document document = null;

		try {
			document = SAXReaderUtil.read(content);
		}
		catch (DocumentException de) {
			throw new IOException(de);
		}

		Element rootElement = document.getRootElement();

		List<Element> elements = rootElement.elements("filter");

		for (Element element : elements) {
			Element classElement = element.element("filter-class");

			if (classElement.getTextTrim().equals(
					PortalClassLoaderFilter.class.getName())) {

				for (Element curParam : element.elements("init-param")) {
					String paramName = curParam.element(
						"param-name").getTextTrim();

					if (paramName.equals("filter-class")) {
						String className = curParam.element(
							"param-value").getTextTrim();

						classElement.setText(className);

						curParam.detach();

						break;
					}
				}
			}
		}

		elements = rootElement.elements("servlet");

		for (Element element : elements) {
			Element classElement = element.element("servlet-class");

			if (classElement.getTextTrim().equals(
					PortalClassLoaderServlet.class.getName())) {

				for (Element curParam : element.elements("init-param")) {
					String paramName = curParam.element(
						"param-name").getTextTrim();

					if (paramName.equals("servlet-class")) {
						String className = curParam.element(
							"param-value").getTextTrim();

						classElement.setText(className);

						curParam.detach();

						break;
					}
				}
			}
		}

		content = DDMXMLUtil.formatXML(document);

		FileUtil.write(webXMLFile, content);
	}

	protected void processTldDependencies(File tldFile) throws IOException {
		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		ClassLoader classLoader = getSystemBundleClassLoader();

		String content = FileUtil.read(tldFile);

		Matcher matcher = _tldPackagesPattern.matcher(content);

		while (matcher.find()) {
			String value = matcher.group(1).trim();

			InputStream inputStream = classLoader.getResourceAsStream(
				value.replace('.', '/') + ".class");

			processClass(
				dependencyVisitor, value, inputStream, _referencedPackages);
		}

		Set<String> jarPackages = dependencyVisitor.getGlobals().keySet();

		for (String jarPackage : jarPackages) {
			_referencedPackages.add(
				jarPackage.replaceAll(StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void processXmlDependencies(
			File xmlFile, String[] xpaths, String prefix, String namespace)
		throws IOException {

		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		ClassLoader classLoader = getSystemBundleClassLoader();

		String content = FileUtil.read(xmlFile);

		Document document = null;

		try {
			document = SAXReaderUtil.read(content, false);
		}
		catch (DocumentException de) {
			throw new IOException(de);
		}

		Element rootElement = document.getRootElement();

		for (String classReference : xpaths) {
			XPath xPath = SAXReaderUtil.createXPath(
				classReference, prefix, namespace);

			List<Node> selectNodes = xPath.selectNodes(rootElement);

			for (Node node : selectNodes) {
				String value = node.getText().trim();

				InputStream inputStream = classLoader.getResourceAsStream(
					value.replace('.', '/') + ".class");

				processClass(
					dependencyVisitor, value, inputStream, _referencedPackages);
			}
		}

		Set<String> jarPackages = dependencyVisitor.getGlobals().keySet();

		for (String jarPackage : jarPackages) {
			_referencedPackages.add(
				jarPackage.replaceAll(StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void writeJarPaths(
		File directory, URI baseURI, ZipWriter zipWriter,
		Set<String> processedPaths) {

		File[] files = directory.listFiles();

		for (File file : files) {
			String path = baseURI.relativize(file.toURI()).getPath();

			if (file.isDirectory()) {
				writeJarPaths(file, baseURI, zipWriter, processedPaths);

				continue;
			}

			if (ArrayUtil.contains(_EXCLUDED_CLASS_PATHS, path)) {
				continue;
			}

			writePath(file, zipWriter, processedPaths, path);
		}
	}

	protected void writePath(
		File file, ZipWriter zipWriter, Set<String> processedPaths,
		String path) {

		if (processedPaths.contains(path)) {
			return;
		}

		processedPaths.add(path);

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);

			zipWriter.addEntry(path, fis);
		}
		catch (IOException ioe) {
			_log.error(ioe);
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

			fis = null;
		}
	}

	protected Manifest _getManifest() throws IOException {
		File manifestFile = _getManifestFile();

		Manifest manifest = new Manifest();

		FileInputStream fis = new FileInputStream(manifestFile);

		try {
			manifest.read(fis);
		}
		finally {
			fis.close();
		}

		return manifest;
	}

	protected File _getManifestFile() throws IOException {
		if (_manifestFile == null) {
			_manifestFile = new File(_deployedAppFolder, _MANIFEST_PATH);

			if (!_manifestFile.exists()) {
				FileUtil.mkdirs(_manifestFile.getParent());

				_manifestFile.createNewFile();
			}
		}

		return _manifestFile;
	}

	protected void _processPaths(
			List<String> resourcePaths, File directory, URI baseURI, String webContextpath)
		throws IOException {

		File[] files = directory.listFiles();

		for (File file : files) {
			String relativePath = baseURI.relativize(file.toURI()).getPath();

			resourcePaths.add(relativePath);

			if (relativePath.startsWith("WEB-INF/classes/") &&
				relativePath.endsWith(".class")) {

				processClassDependencies(file);
			}
			else if (relativePath.equals(
						"WEB-INF/lib" + webContextpath + "-service.jar")) {

				// TODO
				processJarDependencies(file, _exportPackages);
			}
			else if (relativePath.startsWith("WEB-INF/lib/") &&
					 relativePath.endsWith(".jar") &&
					 !ArrayUtil.contains(_EXCLUDED_CLASS_PATHS, relativePath)) {

				processJarDependencies(file, _jarProvidedPackages);
			}
			else if (relativePath.endsWith(".jsp") ||
					 relativePath.endsWith(".jspf")) {

				processJspDependencies(file);
			}

			if (file.isDirectory()) {
				_processPaths(resourcePaths, file, baseURI, webContextpath);
			}
		}
	}

	protected void _saveManifest(Manifest manifest)
		throws IOException {

		File manifestFile = _getManifestFile();

		FileOutputStream fos = new FileOutputStream(manifestFile);

		try {
			manifest.write(fos);
		}
		finally {
			fos.close();
		}
	}

	private static Log _log = LogFactoryUtil.getLog(WebBundleProcessor.class);

	private static final String[] _EXCLUDED_CLASS_PATHS = new String[] {
		"WEB-INF/lib/commons-codec.jar",
		"WEB-INF/lib/commons-fileupload.jar",
		"WEB-INF/lib/commons-io.jar",
		"WEB-INF/lib/commons-lang.jar",
		"WEB-INF/lib/commons-logging.jar",
		"WEB-INF/lib/log4j.jar",
		"WEB-INF/lib/util-bridges.jar",
		"WEB-INF/lib/util-java.jar",
		"WEB-INF/lib/util-taglib.jar"
	};

	private static final String _INVOKER_PORTLET_NAME_XPATH =
		"x:init-param[x:name/text()='com.liferay.portal.invokerPortletName']";

	private static final String[] _LIFERAYHOOKXML_CLASSREFERENCE_ELEMENTS =
		new String[] {
			"//indexer-post-processor-impl", "//service-impl",
			"//servlet-filter-impl", "//struts-action-impl"
		};

	private static final String[] _LIFERAYPORTLETXML_CLASSREFERENCE_ELEMENTS =
		new String[] {
			"//configuration-action-class", "//indexer-class",
			"//open-search-class", "//portlet-url-class",
			"//friendly-url-mapper-class", "//url-encoder-class",
			"//portlet-data-handler-class", "//portlet-layout-listener-class",
			"//poller-processor-class", "//pop-message-listener-class",
			"//social-activity-interpreter-class",
			"//social-request-interpreter-class", "//webdav-storage-class",
			"//xml-rpc-method-class", "//control-panel-entry-class",
			"//asset-renderer-factory", "//atom-collection-adapter",
			"//custom-attributes-display", "//permission-propagator",
			"//workflow-handler"
		};

	private static final String _MANIFEST_PATH = "META-INF/MANIFEST.MF";

	private static final String[] _PORTLETXML_CLASSREFERENCE_ELEMENTS =
		new String[] {
			"//x:filter-class", "//x:listener-class", "//x:portlet-class",
			"//x:resource-bundle"
		};

	private static final String[] _WEBXML_CLASSREFERENCE_ELEMENTS =
		new String[] {
			"//x:filter-class", "//x:listener-class", "//x:servlet-class"
		};

	private BaseDeployer _baseDeployer;
	private List<String> _classProvidedPackages = new UniqueList<String>();
	private File _deployedAppFolder;
	private List<String> _exportPackages = new UniqueList<String>();
	private File _file;
	private List<String> _importPackages = new UniqueList<String>();
	private List<String> _jarProvidedPackages = new UniqueList<String>();
	private Pattern _jspImportedPackagesPattern = Pattern.compile(
		"<%@\\p{Space}+?page\\p{Space}+?import=\"([^\"]+?)\"\\p{Space}+?%>");
	private List<String> _jarReferencedPackages = new UniqueList<String>();
	private File _manifestFile;
	private Map<String, String[]> _parameterMap;
	private List<String> _referencedPackages = new UniqueList<String>();
	private List<String> _resourcePaths = new UniqueList<String>();
	private ClassLoader _systemBundleClassLoader;
	private Pattern _tldPackagesPattern = Pattern.compile(
		"<[^>]+?-class>\\p{Space}*?(.*?)\\p{Space}*?</[^>]+?-class>");
	private String _version;

}
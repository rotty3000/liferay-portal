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

import aQute.lib.osgi.Analyzer;

import com.liferay.portal.events.GlobalStartupAction;
import com.liferay.portal.kernel.deploy.auto.AutoDeployException;
import com.liferay.portal.kernel.deploy.auto.AutoDeployListener;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.deploy.hot.DependencyManagementThreadLocal;
import com.liferay.portal.kernel.io.FileFilter;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceConfigurator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.servlet.PortalClassLoaderFilter;
import com.liferay.portal.kernel.servlet.PortalClassLoaderServlet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StreamUtil;
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
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.tools.deploy.BaseDeployer;
import com.liferay.portal.util.Portal;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.dynamicdatamapping.util.DDMXMLUtil;
import com.liferay.web.extender.internal.introspection.ClassLoaderSource;
import com.liferay.web.extender.internal.introspection.Source;
import com.liferay.web.extender.internal.util.Util;
import com.liferay.web.extender.jsonwebservice.JSONWebServiceConfiguratorImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.jsp.JspFactory;

import org.apache.jasper.runtime.JspFactoryImpl;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.depend.DependencyVisitor;

import org.osgi.framework.Constants;

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

		_baseDeployer.setAppServerType(ServerDetector.MODULE_FRAMEWORK_ID);
	}

	public java.io.InputStream getInputStream() throws IOException {
		if (!_deployedAppFolder.exists() || !_deployedAppFolder.isDirectory()) {
			return null;
		}

		File tempFolder = _file.getParentFile();

		File outputFile = new File(tempFolder, "output.zip");

		JarOutputStream jarOutputStream = new JarOutputStream(
			new FileOutputStream(outputFile));

		try {
			Set<String> processedPaths = new HashSet<String>();

			File manifestFile = _getManifestFile();

			InputStream inputStream = null;

			try {
				inputStream = new FileInputStream(manifestFile);

				writePath(
					inputStream, jarOutputStream, processedPaths,
					MANIFEST_PATH);
			}
			finally {
				StreamUtil.cleanUp(inputStream);
			}

			try {
				byte[] bytes = JspFactoryImpl.class.getName().getBytes(
					StringPool.UTF8);

				inputStream = new ByteArrayInputStream(bytes);

				String path =
					"WEB-INF/classes/META-INF/services/".concat(
						JspFactory.class.getName());

				writePath(
					inputStream, jarOutputStream, processedPaths, path);
			}
			finally {
				StreamUtil.cleanUp(inputStream);
			}

			writeJarPaths(
				_deployedAppFolder, _deployedAppFolder.toURI(), jarOutputStream,
				processedPaths);
		}
		finally {
			jarOutputStream.close();
		}

		return new FileInputStream(outputFile);
	}

	public void process() throws IOException {
		String webContextpath = MapUtil.getString(
			_parameterMap, WEB_CONTEXTPATH);

		if (!webContextpath.startsWith(StringPool.SLASH)) {
			webContextpath = StringPool.SLASH.concat(webContextpath);
		}

		AutoDeploymentContext autoDeploymentContext =
			buildAutoDeploymentContext(webContextpath);

		boolean enabled = DependencyManagementThreadLocal.isEnabled();

		try {
			DependencyManagementThreadLocal.setEnabled(false);

			doAutoDeploy(autoDeploymentContext);
		}
		finally {
			DependencyManagementThreadLocal.setEnabled(enabled);
		}

		_deployedAppFolder = autoDeploymentContext.getDeployDir();

		if (!_deployedAppFolder.exists()) {
			File[] listFiles = _deployedAppFolder.getParentFile().listFiles(
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".war");
					}
				}
			);

			if ((listFiles == null) || (listFiles.length == 0)) {
				// TODO Something happened here, but not sure what yet.

				return;
			}

			File file = listFiles[0];

			_deployedAppFolder.mkdirs();

			Util.expandFile(file, _deployedAppFolder);
		}

		if ((_deployedAppFolder == null) || !_deployedAppFolder.exists() ||
			!_deployedAppFolder.isDirectory()) {

			return;
		}

		PluginPackage pluginPackage = _baseDeployer.readPluginPackage(
			_deployedAppFolder);

		String recommendedDeploymentContext =
			pluginPackage.getRecommendedDeploymentContext();

		if (Validator.isNotNull(recommendedDeploymentContext)) {
			webContextpath = recommendedDeploymentContext;

			if (!webContextpath.startsWith(StringPool.SLASH)) {
				webContextpath = StringPool.SLASH.concat(webContextpath);
			}
		}

		Manifest manifest = _getManifest();

		Attributes attributes = manifest.getMainAttributes();

		// If it's not a bundle, then we need to manipulate it into one. The
		// spec states that this is only true when the Manifest does not contain
		// a Bundle_SymbolicName header.

		String bundleSymbolicName = GetterUtil.getString(
			attributes.getValue(Constants.BUNDLE_SYMBOLICNAME));

		if (Validator.isNull(bundleSymbolicName)) {
			Analyzer analyzer = new Analyzer();

			Map<String, File> classPath = new TreeMap<String, File>();

			File classesFolder = new File(
				_deployedAppFolder, "WEB-INF/classes");

			analyzer.setBase(_deployedAppFolder);
			analyzer.setJar(_deployedAppFolder);

			if (classesFolder.exists() && classesFolder.isDirectory() &&
				classesFolder.canRead()) {

				classPath.put("WEB-INF/classes", classesFolder);
			}

			_processPaths(
				_resourcePaths, _deployedAppFolder, _deployedAppFolder.toURI(),
				classPath);

			analyzer.setProperty(
				Constants.BUNDLE_CLASSPATH,
				StringUtil.merge(classPath.keySet()));
			analyzer.setClasspath(
				classPath.values().toArray(new File[classPath.size()]));

			processBundleSymbolicName(analyzer, webContextpath);

			bundleSymbolicName = analyzer.getProperty(
				Constants.BUNDLE_SYMBOLICNAME);

			String importPackage = StringPool.BLANK;

			Properties properties = PropsUtil.getProperties(
				PropsKeys.MODULE_FRAMEWORK_WEB_EXTENDER_HEADERS, true);

			Enumeration<Object> keys = properties.keys();

			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String originalKey = key;

				if (key.endsWith(StringPool.CLOSE_BRACKET)) {
					String bundleFilterSuffix = StringPool.OPEN_BRACKET.concat(
						bundleSymbolicName).concat(StringPool.CLOSE_BRACKET);

					if (!key.endsWith(bundleFilterSuffix)) {
						continue;
					}

					key = key.substring(
						0, key.indexOf(StringPool.OPEN_BRACKET));
				}

				String value = properties.getProperty(originalKey);

				if (key.equals(Constants.IMPORT_PACKAGE)) {
					importPackage += StringPool.COMMA.concat(value);
				}

				analyzer.setProperty(key, value);
			}

			// The order of these operations is important

			processBundleVersion(analyzer, pluginPackage);
			processBundleManifestVersion(analyzer);

			processPortletXML(webContextpath);
			processWebXML("WEB-INF/web.xml");
			processWebXML("WEB-INF/liferay-web.xml");
			processLiferayPortletXML(webContextpath);
			processDeclarativeReferences();
			processExportImportPackage(analyzer, importPackage);
			processPluginDependencies(analyzer, pluginPackage);

			try {
				manifest = analyzer.calcManifest();
			}
			catch (Exception e) {
				throw new IOException(e);
			}
		}

		// We still have to add the specified Web-ContextPath even if it already
		// exists, because this allows changing to an alternate context.

		attributes = manifest.getMainAttributes();

		attributes.putValue(WEB_CONTEXTPATH, webContextpath);

		Object manifestVersion = attributes.get(
			Attributes.Name.MANIFEST_VERSION.toString());

		if (manifestVersion == null) {
			attributes.putValue(
				Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
		}

		_saveManifest(manifest);
	}

	protected AutoDeploymentContext buildAutoDeploymentContext(String context) {
		File destDir = new File(_file.getParentFile(), "deploy");

		destDir.mkdirs();

		AutoDeploymentContext autoDeploymentContext =
			new AutoDeploymentContext();

		autoDeploymentContext.setAppServerType(
			ServerDetector.MODULE_FRAMEWORK_ID);
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

	protected void processBundleManifestVersion(Analyzer analyzer) {
		String bundleManifestVersion = MapUtil.getString(
			_parameterMap, Constants.BUNDLE_MANIFESTVERSION);

		if (Validator.isNull(bundleManifestVersion)) {
			bundleManifestVersion = "2";
		}

		analyzer.setProperty(
			Constants.BUNDLE_MANIFESTVERSION, bundleManifestVersion);
	}

	protected void processBundleSymbolicName(
		Analyzer analyzer, String webContextpath) {

		String bundleSymbolicName = MapUtil.getString(
			_parameterMap, Constants.BUNDLE_SYMBOLICNAME);

		if (Validator.isNull(bundleSymbolicName)) {
			bundleSymbolicName = webContextpath.substring(1);
		}

		analyzer.setProperty(Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName);
	}

	protected void processBundleVersion(
		Analyzer analyzer, PluginPackage pluginPackage) {

		_version = MapUtil.getString(_parameterMap, Constants.BUNDLE_VERSION);

		if (Validator.isNull(_version)) {
			_version = pluginPackage.getVersion();
		}

		analyzer.setProperty(Constants.BUNDLE_VERSION, _version);
	}

	protected void processClass(
			DependencyVisitor dependencyVisitor, String className,
			Source source, List<String> packageList)
		throws IOException {

		if (className.startsWith("java/")) {
			return;
		}

		InputStream inputStream = source.getResourceAsStream(className);

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

			String superName = classReader.getSuperName();

			if (superName != null) {
				processReferencedDependencies(
					superName.replace('.', '/') + ".class", source);
			}

			String[] interfaces = classReader.getInterfaces();

			if ((interfaces != null) && (interfaces.length > 0)) {
				processInterfaces(interfaces, source);
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	protected void processInterfaces(String[] interfaces, Source source)
		throws IOException {

		for (String interfaceName : interfaces) {
			processReferencedDependencies(
				interfaceName.replace('.', '/') + ".class", source);
		}
	}

	protected void processReferencedDependencies(
			String className, Source source)
		throws IOException {

		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		processClass(dependencyVisitor, className, source, _importPackages);

		Set<String> packages = dependencyVisitor.getGlobals().keySet();

		for (String referencedPackage : packages) {
			_importPackages.add(
				referencedPackage.replaceAll(
					StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void processDeclarativeReferences() throws IOException {
		for (String value :
				PropsValues.
					MODULE_FRAMEWORK_WEB_EXTENDER_DEFAULT_SERVLET_PACKAGES) {

			int pos = value.indexOf(StringPool.SEMICOLON);

			if (pos != -1) {
				value = value.substring(0, pos);
			}

			_importPackages.add(value.trim());
		}

		// References from web.xml

		File xml = new File(_deployedAppFolder, "WEB-INF/web.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _WEBXML_CLASSREFERENCE_ELEMENTS, "x",
				"http://java.sun.com/xml/ns/j2ee");
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
		}

		// References from liferay-web.xml
		// TODO: remove this?

		xml = new File(_deployedAppFolder, "WEB-INF/liferay-web.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _WEBXML_CLASSREFERENCE_ELEMENTS, "x", 
				"http://java.sun.com/xml/ns/j2ee");
		}

		// References from liferay-portlet.xml

		xml = new File(_deployedAppFolder, "WEB-INF/liferay-portlet.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _LIFERAYPORTLETXML_CLASSREFERENCE_ELEMENTS, null, null);
		}

		// References from liferay-hook.xml

		xml = new File(_deployedAppFolder, "WEB-INF/liferay-hook.xml");

		if (xml.exists()) {
			processXmlDependencies(
				xml, _LIFERAYHOOKXML_CLASSREFERENCE_ELEMENTS, null, null);
		}
	}

	protected void processExportImportPackage(
			Analyzer analyzer, String importPackage)
		throws IOException {

		if (Validator.isNotNull(importPackage)) {
			String[] packageImports = StringUtil.split(importPackage);

			for (String pachageImport : packageImports) {
				_importPackages.add(pachageImport);
			}
		}

		importPackage = MapUtil.getString(
			_parameterMap, Constants.IMPORT_PACKAGE);

		if (Validator.isNotNull(importPackage)) {
			analyzer.setProperty(Constants.IMPORT_PACKAGE, importPackage);
		}
		else if (!_importPackages.isEmpty()) {
			StringBundler sb = new StringBundler(
				(_importPackages.size() * 3) + 1);

			for (String packageName : _importPackages) {
				if (Validator.isNull(packageName)) {
					continue;
				}

				sb.append(packageName);
				sb.append(";resolution:=\"optional\"");
				sb.append(StringPool.COMMA);
			}

			sb.append("*;resolution:=\"optional\"");

			analyzer.setProperty(Constants.IMPORT_PACKAGE, sb.toString());
		}
	}

	protected void processJspDepedencies(File jspFile) throws IOException {
		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		Source source = new ClassLoaderSource(getSystemBundleClassLoader());

		String content = FileUtil.read(jspFile);

		int startPos = content.length();
		int pos = -1;

		while (true) {
			pos = content.lastIndexOf("<%@", startPos);

			if (pos == -1) {
				break;
			}

			startPos = pos;

			int x = content.indexOf("import=\"", startPos);
			int y = -1;

			if (x != -1) {
				x = x + "import=\"".length();
				y = content.indexOf("\"", x);
			}

			if ((x != -1) && (y != -1)) {
				String value = content.substring(x, y);

				processClass(
					dependencyVisitor, value.replace('.', '/') + ".class",
					source, _importPackages);
			}

			startPos -= 3;
		}

		Set<String> packages = dependencyVisitor.getGlobals().keySet();

		for (String referencedPackage : packages) {
			_importPackages.add(
				referencedPackage.replaceAll(
					StringPool.SLASH, StringPool.PERIOD));
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

				strutsPathElement = SAXReaderUtil.createElement("struts-path");

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

	protected void processPluginDependencies(
		Analyzer analyzer, PluginPackage pluginPackage) {

		List<String> requiredDeploymentContexts =
			pluginPackage.getRequiredDeploymentContexts();

		if ((requiredDeploymentContexts == null) ||
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
			analyzer.setProperty(Constants.REQUIRE_BUNDLE, requiredBundles);
		}
	}

	protected void processPortletXML(String webContextpath) throws IOException {
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
			String portletName = PortalUtil.getJsSafePortletId(
				portletElement.elementText("portlet-name"));

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

		Source source = new ClassLoaderSource(getSystemBundleClassLoader());

		String content = FileUtil.read(tldFile);

		Matcher matcher = _tldPackagesPattern.matcher(content);

		while (matcher.find()) {
			String value = matcher.group(1).trim();

			processClass(
				dependencyVisitor, value.replace('.', '/') + ".class", source,
				_importPackages);
		}

		Set<String> packages = dependencyVisitor.getGlobals().keySet();

		for (String referencedPackage : packages) {
			_importPackages.add(
				referencedPackage.replaceAll(
					StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void processXmlDependencies(
			File xmlFile, String[] xpaths, String prefix, String namespace)
		throws IOException {

		DependencyVisitor dependencyVisitor = new DependencyVisitor();

		Source source = new ClassLoaderSource(getSystemBundleClassLoader());

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

				processClass(
					dependencyVisitor, value.replace('.', '/') + ".class",
					source, _importPackages);
			}
		}

		Set<String> packages = dependencyVisitor.getGlobals().keySet();

		for (String referencedPackage : packages) {
			_importPackages.add(
				referencedPackage.replaceAll(
					StringPool.SLASH, StringPool.PERIOD));
		}
	}

	protected void writeJarPaths(
		File directory, URI baseURI, ZipOutputStream zipOutputStream,
		Set<String> processedPaths) throws IOException {

		File[] files = directory.listFiles();

		for (File file : files) {
			String relativePath = baseURI.relativize(file.toURI()).getPath();

			if (file.isDirectory()) {
				zipOutputStream.putNextEntry(new ZipEntry(relativePath));

				zipOutputStream.closeEntry();

				writeJarPaths(file, baseURI, zipOutputStream, processedPaths);

				continue;
			}

			if (ArrayUtil.contains(_EXCLUDED_CLASS_PATHS, relativePath)) {
				continue;
			}

			InputStream inputStream = null;

			if (relativePath.equals("WEB-INF/service.xml")) {
				try {
					byte[] bytes =
						JSONWebServiceConfiguratorImpl.class.getName().
							getBytes(StringPool.UTF8);

					inputStream = new ByteArrayInputStream(bytes);

					String path =
						"WEB-INF/classes/META-INF/services/".concat(
							JSONWebServiceConfigurator.class.getName());

					writePath(
						inputStream, zipOutputStream, processedPaths, path);
				}
				finally {
					StreamUtil.cleanUp(inputStream);
				}
			}

			try {
				inputStream = new FileInputStream(file);

				writePath(
					inputStream, zipOutputStream, processedPaths, relativePath);
			}
			finally {
				StreamUtil.cleanUp(inputStream);
			}
		}
	}

	protected void writePath(
		InputStream inputStream, ZipOutputStream zipOutputStream,
		Set<String> processedPaths, String path) {

		if (processedPaths.contains(path)) {
			return;
		}

		processedPaths.add(path);

		try {
			zipOutputStream.putNextEntry(new JarEntry(path));

			StreamUtil.transfer(inputStream, zipOutputStream, false);

			zipOutputStream.closeEntry();
		}
		catch (IOException ioe) {
			_log.error(ioe);
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
			_manifestFile = new File(_deployedAppFolder, MANIFEST_PATH);

			if (!_manifestFile.exists()) {
				FileUtil.mkdirs(_manifestFile.getParent());

				_manifestFile.createNewFile();
			}
		}

		return _manifestFile;
	}

	protected void _processPaths(
			List<String> resourcePaths, File directory, URI baseURI,
			Map<String, File> classPath)
		throws IOException {

		File[] files = directory.listFiles();

		for (File file : files) {
			String relativePath = baseURI.relativize(file.toURI()).getPath();

			if (relativePath.endsWith(StringPool.SLASH)) {
				relativePath = relativePath.substring(
					0, relativePath.length() - 1);
			}

			resourcePaths.add(relativePath);

			if (relativePath.startsWith("WEB-INF/lib/") &&
				relativePath.endsWith(".jar") &&
				!ArrayUtil.contains(_EXCLUDED_CLASS_PATHS, relativePath)) {

				classPath.put(relativePath, file);
			}
			else if (relativePath.endsWith(".jsp") ||
					 relativePath.endsWith(".jspf") ||
					 relativePath.endsWith(".jspx")) {

				processJspDepedencies(file);
			}

			if (file.isDirectory()) {
				_processPaths(resourcePaths, file, baseURI, classPath);
			}
		}
	}

	protected void _saveManifest(Manifest manifest) throws IOException {
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
		"WEB-INF/lib/commons-codec.jar", "WEB-INF/lib/commons-fileupload.jar",
		"WEB-INF/lib/commons-io.jar", "WEB-INF/lib/commons-lang.jar"
//		, //		"WEB-INF/lib/commons-logging.jar",
//		"WEB-INF/lib/log4j.jar", //		"WEB-INF/lib/slf4j-api.jar",
//		"WEB-INF/lib/util-bridges.jar", //		"WEB-INF/lib/util-java.jar",
//		"WEB-INF/lib/util-taglib.jar"
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
	private File _deployedAppFolder;
	private File _file;
	private List<String> _importPackages = new UniqueList<String>();
	private File _manifestFile;
	private Map<String, String[]> _parameterMap;
	private List<String> _resourcePaths = new UniqueList<String>();
	private ClassLoader _systemBundleClassLoader;
	private Pattern _tldPackagesPattern = Pattern.compile(
		"<[^>]+?-class>\\p{Space}*?(.*?)\\p{Space}*?</[^>]+?-class>");
	private String _version;

}
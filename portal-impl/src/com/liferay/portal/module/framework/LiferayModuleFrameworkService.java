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

package com.liferay.portal.module.framework;

import aQute.libg.header.OSGiHeader;
import aQute.libg.version.Version;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.ServiceLoader;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UniqueList;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.pacl.PACLClassLoaderUtil;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.util.PropsValues;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;

import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.context.ApplicationContext;

/**
 * @author Raymond Augé
 */
public class LiferayModuleFrameworkService implements ModuleFrameworkService {

	public Object addBundle(String location) throws PortalException {
		return addBundle(location, null);
	}

	public Object addBundle(String location, InputStream inputStream)
		throws PortalException {

		return addBundle(location, inputStream, true);
	}

	public Object addBundle(
			String location, InputStream inputStream, boolean checkPermissions) 
		throws PortalException {

		return _addBundle(location, inputStream, checkPermissions);
	}
	public Framework getFramework() {
		return _framework;
	}

	public String getState(long bundleId) throws PortalException {
		return _getState(bundleId);
	}

	public void registerContext(Object context) {
		if (context == null) {
			return;
		}

		if ((context instanceof ApplicationContext) &&
			PropsValues.MODULE_FRAMEWORK_REGISTER_LIFERAY_SERVICES) {

			ApplicationContext applicationContext = (ApplicationContext)context;

			_registerApplicationContext(applicationContext);
		}
		else if (context instanceof ServletContext) {
			ServletContext servletContext = (ServletContext)context;

			_registerServletContext(servletContext);
		}
	}

	public void setBundleStartLevel(long bundleId, int startLevel)
		throws PortalException {

		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		BundleStartLevel bundleStartLevel = bundle.adapt(
			BundleStartLevel.class);

		bundleStartLevel.setStartLevel(startLevel);

	}

	public void startBundle(long bundleId) throws PortalException {
		_startBundle(bundleId);
	}

	public void startBundle(long bundleId, int options)
		throws PortalException {

		_startBundle(bundleId, options);
	}

	public void startFramework() throws Exception {
		List<FrameworkFactory> frameworkFactories = ServiceLoader.load(
			FrameworkFactory.class);

		if (frameworkFactories.isEmpty()) {
			return;
		}

		FrameworkFactory frameworkFactory = frameworkFactories.get(0);

		Map<String, String> properties = buildProperties();

		_framework = frameworkFactory.newFramework(properties);

		_framework.init();

		_setupLogBridge();

		_framework.start();

		_setupFileInstall();
	}

	public void startRuntime() throws Exception {
		if (_framework == null) {
			return;
		}

		FrameworkStartLevel frameworkStartLevel = _framework.adapt(
			FrameworkStartLevel.class);

		frameworkStartLevel.setStartLevel(
			PropsValues.MODULE_FRAMEWORK_RUNTIME_START_LEVEL,
			(FrameworkListener)null);
	}

	public void stopBundle(long bundleId) throws PortalException {
		_stopBundle(bundleId);
	}

	public void stopBundle(long bundleId, int options)
		throws PortalException {

		_stopBundle(bundleId, options);
	}

	public void stopFramework() throws Exception {
		if (_framework == null) {
			return;
		}

		BundleContext bundleContext = _framework.getBundleContext();

		_logBridge.stop(bundleContext);

		_framework.stop();

	}

	public void stopRuntime() throws Exception {
		if (_framework == null) {
			return;
		}

		FrameworkStartLevel frameworkStartLevel = _framework.adapt(
			FrameworkStartLevel.class);

		frameworkStartLevel.setStartLevel(
			PropsValues.MODULE_FRAMEWORK_BEGINNING_START_LEVEL,
			(FrameworkListener)null);
	}

	public void uninstallBundle(long bundleId) throws PortalException {
		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.uninstall();
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}		
	}

	public void updateBundle(long bundleId) throws PortalException {
		_updateBundle(bundleId);
	}

	public void updateBundle(long bundleId, InputStream inputStream)
		throws PortalException {

		_updateBundle(bundleId, inputStream);
	}

	protected Map<String, String> buildProperties() {
		Map<String, String> properties = new HashMap<String, String>();

		properties.put(
			Constants.BUNDLE_DESCRIPTION, ReleaseInfo.getReleaseInfo());
		properties.put(Constants.BUNDLE_NAME, ReleaseInfo.getName());
		properties.put(Constants.BUNDLE_VENDOR, ReleaseInfo.getVendor());
		properties.put(Constants.BUNDLE_VERSION, ReleaseInfo.getVersion());
		properties.put(
			Constants.FRAMEWORK_BEGINNING_STARTLEVEL,
			String.valueOf(PropsValues.MODULE_FRAMEWORK_BEGINNING_START_LEVEL));
		properties.put(
			Constants.FRAMEWORK_BUNDLE_PARENT,
			Constants.FRAMEWORK_BUNDLE_PARENT_APP);
		properties.put(
			Constants.FRAMEWORK_STORAGE,
			PropsValues.MODULE_FRAMEWORK_STATE_DIR);

		// Felix fileinstall

		StringBundler sb = new StringBundler(3);

		sb.append(PropsValues.MODULE_FRAMEWORK_LIB_DIR);
		sb.append(StringPool.COMMA);
		sb.append(PropsValues.MODULE_FRAMEWORK_AUTO_DEPLOY_DIR);

		properties.put(FELIX_FILEINSTALL_DIR, sb.toString());
		properties.put(FELIX_FILEINSTALL_LOG_LEVEL, _getFileInstallLogLevel());
		properties.put(
			FELIX_FILEINSTALL_POLL,
			String.valueOf(PropsValues.MODULE_FRAMEWORK_AUTO_DEPLOY_INTERVAL));
		properties.put(FELIX_FILEINSTALL_TMPDIR,
			System.getProperty("java.io.tmpdir"));

		UniqueList<String> packages = new UniqueList<String>();

		try {
			_getBundleExportPackages(
				PropsValues.MODULE_FRAMEWORK_SYSTEM_BUNDLE_EXPORT_PACKAGES,
				packages);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		packages.addAll(
			Arrays.asList(PropsValues.MODULE_FRAMEWORK_SYSTEM_PACKAGES_EXTRA));
		packages.addAll(
			Arrays.asList(
				PropsValues.
					MODULE_FRAMEWORK_WEB_EXTENDER_DEFAULT_PORTLET_PACKAGES));
		packages.addAll(
			Arrays.asList(
				PropsValues.
					MODULE_FRAMEWORK_WEB_EXTENDER_DEFAULT_SERVLET_PACKAGES));

		Collections.sort(packages);

		if (_log.isDebugEnabled()) {
			_log.debug("The portal's system bundle is exporting the following packages: \n" + StringUtil.merge(packages).replace(",", "\n"));
		}

		properties.put(
			Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
			StringUtil.merge(packages));

		return properties;
	}

	private Object _addBundle(
			String location, InputStream inputStream, boolean checkPermissions)
		throws PortalException {

		if (checkPermissions) {
			_checkPermission();
		}

		if (_framework == null) {
			return null;
		}

		BundleContext bundleContext = _framework.getBundleContext();

		if (inputStream != null) {
			Bundle bundle = _getBundle(bundleContext, inputStream);

			if (bundle != null) {
				return bundle;
			}
		}

		try {
			return bundleContext.installBundle(location, inputStream);
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private void _checkPermission() throws PrincipalException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) || !permissionChecker.isOmniadmin()) {
			throw new PrincipalException();
		}
	}

	private Bundle _getBundle(long bundleId) {
		if (_framework == null) {
			return null;
		}

		BundleContext bundleContext = _framework.getBundleContext();

		return bundleContext.getBundle(bundleId);
	}

	public static Bundle _getBundle(
			BundleContext bundleContext, InputStream inputStream)
		throws PortalException {

		try {
			inputStream.mark(0);

			JarInputStream jarInputStream = new JarInputStream(inputStream);

			Manifest manifest = jarInputStream.getManifest();

			inputStream.reset();

			Attributes attributes = manifest.getMainAttributes();

			String bundleSymbolicNameAttribute = attributes.getValue(
				Constants.BUNDLE_SYMBOLICNAME);

			Map<String, Map<String, String>> bundleSymbolicNamesMap =
				OSGiHeader.parseHeader(bundleSymbolicNameAttribute);

			Set<String> bundleSymbolicNamesSet =
				bundleSymbolicNamesMap.keySet();

			Iterator<String> bundleSymbolicNamesIterator =
				bundleSymbolicNamesSet.iterator();

			String bundleSymbolicName = bundleSymbolicNamesIterator.next();

			String bundleVersionAttribute = attributes.getValue(
				Constants.BUNDLE_VERSION);

			Version bundleVersion = Version.parseVersion(
				bundleVersionAttribute);

			for (Bundle bundle : bundleContext.getBundles()) {
				Version curBundleVersion = Version.parseVersion(
					bundle.getVersion().toString());

				if (bundleSymbolicName.equals(bundle.getSymbolicName()) &&
					bundleVersion.equals(curBundleVersion)) {

					return bundle;
				}
			}

			return null;
		}
		catch (IOException ioe) {
			throw new PortalException(ioe);
		}
	}

	private void _getBundleExportPackages(
			String[] bundleSymbolicNames, List<String> packages)
		throws Exception {

		Set<URL> set = new HashSet<URL>();

		ClassLoader classLoader = PACLClassLoaderUtil.getPortalClassLoader();

		URL url = null;

		Enumeration<URL> enu = classLoader.getResources("META-INF/MANIFEST.MF");

		while (enu.hasMoreElements()) {
			url = enu.nextElement();

			set.add(url);
		}

		enu = Validator.class.getClassLoader().getResources("/META-INF/MANIFEST.MF");

		while (enu.hasMoreElements()) {
			url = enu.nextElement();

			set.add(url);
		}

		enu = Collections.enumeration(set);

		while (enu.hasMoreElements()) {
			url = enu.nextElement();

			Manifest manifest = new Manifest(url.openStream());

			Attributes attributes = manifest.getMainAttributes();

			String bundleSymbolicName = attributes.getValue(
				Constants.BUNDLE_SYMBOLICNAME);

			if (Validator.isNull(bundleSymbolicName)) {
				continue;
			}

			for (String curBundleSymbolicName : bundleSymbolicNames) {
				if (!bundleSymbolicName.startsWith(curBundleSymbolicName)) {
					continue;
				}

				String exportPackage = attributes.getValue(
					Constants.EXPORT_PACKAGE);

				Map<String, Map<String, String>> exportPackageMap =
					OSGiHeader.parseHeader(exportPackage);

				for (Map.Entry<String, Map<String, String>> entry :
						exportPackageMap.entrySet()) {

					String javaPackage = entry.getKey();
					Map<String, String> javaPackageMap = entry.getValue();

					StringBundler sb = new StringBundler(4);

					sb.append(javaPackage);
					sb.append(";version=\"");

					if (javaPackageMap.containsKey("version")) {
						String version = javaPackageMap.get("version");

						sb.append(version);
					}
					else {
						String bundleVersionString = attributes.getValue(
							Constants.BUNDLE_VERSION);

						sb.append(bundleVersionString);
					}

					sb.append("\"");

					javaPackage = sb.toString();

					packages.add(javaPackage);
				}

				break;
			}
		}
	}

	private Set<Class<?>> _getInterfaces(Object bean) {
		Set<Class<?>> interfaces = new HashSet<Class<?>>();

		Class<?> beanClass = bean.getClass();

		for (Class<?> interfaceClass : beanClass.getInterfaces()) {
			interfaces.add(interfaceClass);
		}

		while ((beanClass = beanClass.getSuperclass()) != null) {
			for (Class<?> interfaceClass : beanClass.getInterfaces()) {
				if (!interfaces.contains(interfaceClass)) {
					interfaces.add(interfaceClass);
				}
			}
		}

		return interfaces;
	}

	private String _getFileInstallLogLevel() {

		// Felix file install uses a logging level scheme as follows:
		// NONE=0, ERROR=1, WARNING=2, INFO=3, DEBUG=4

		int fileInstallLogLevel = 0;

		if (_log.isDebugEnabled()) {
			fileInstallLogLevel = 4;
		}
		else if (_log.isErrorEnabled()) {
			fileInstallLogLevel = 1;
		}
		else if (_log.isInfoEnabled()) {
			fileInstallLogLevel = 3;
		}
		else if (_log.isWarnEnabled()) {
			fileInstallLogLevel = 2;
		}

		return String.valueOf(fileInstallLogLevel);
	}

	private String _getState(long bundleId) throws PortalException {
		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		int state = bundle.getState();

		if (state == Bundle.ACTIVE) {
			return "active";
		}
		else if (state == Bundle.INSTALLED) {
			return "installed";
		}
		else if (state == Bundle.RESOLVED) {
			return "resolved";
		}
		else if (state == Bundle.STARTING) {
			return "starting";
		}
		else if (state == Bundle.STOPPING) {
			return "stopping";
		}
		else if (state == Bundle.UNINSTALLED) {
			return "uninstalled";
		}
		else {
			return StringPool.BLANK;
		}
	}

	private void _registerApplicationContext(
		ApplicationContext applicationContext) {

		BundleContext bundleContext = _framework.getBundleContext();

		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = null;

			try {
				bean = applicationContext.getBean(beanName);
			}
			catch (BeanIsAbstractException biae) {
			}
			catch (Exception e) {
				_log.error(e, e);
			}

			if (bean != null) {
				_registerService(bundleContext, beanName, bean);
			}
		}
	}

	private void _registerService(
		BundleContext bundleContext, String beanName, Object bean) {

		Set<Class<?>> interfaces = _getInterfaces(bean);

		List<String> names = new ArrayList<String>();

		for (Class<?> interfaceClass : interfaces) {
			if (ArrayUtil.contains(
					PropsValues.MODULE_FRAMEWORK_SERVICES_IGNORED_INTERFACES,
					interfaceClass.getName())) {

				continue;
			}

			names.add(interfaceClass.getName());
		}

		if (names.isEmpty()) {
			return;
		}

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put(BEAN_ID, beanName);
		properties.put(ORIGINAL_BEAN, Boolean.TRUE);
		properties.put(SERVICE_VENDOR, ReleaseInfo.getVendor());

		bundleContext.registerService(
			names.toArray(new String[names.size()]), bean, properties);
	}

	private void _registerServletContext(ServletContext servletContext) {
		BundleContext bundleContext = _framework.getBundleContext();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put(BEAN_ID, ServletContext.class.getName());
		properties.put(ORIGINAL_BEAN, Boolean.TRUE);
		properties.put(SERVICE_VENDOR, ReleaseInfo.getVendor());

		bundleContext.registerService(
			new String[] {ServletContext.class.getName()}, servletContext,
			properties);
	}

	private void _setupFileInstall() throws Exception {
		//String fileIntallPath = PropsValues.LIFERAY_LIB_PORTAL_DIR.concat(
		//	"org.apache.felix.fileinstall.jar");
		String fileIntallPath = "/home/migue/development/sourcecode/liferay/liferay-portal/portal/lib/portal/org.apache.felix.fileinstall.jar";

		InputStream inputStream = new BufferedInputStream(
			new FileInputStream(fileIntallPath));

		try {
			Bundle bundle = (Bundle)addBundle(
				fileIntallPath, inputStream, false);

			if ((bundle != null) && (bundle.getState() == Bundle.INSTALLED)) {
				bundle.start();
			}
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private void _setupLogBridge() throws Exception {
		BundleContext bundleContext = _framework.getBundleContext();

		_logBridge = new LogBridge();

		_logBridge.start(bundleContext);
	}

	private void _startBundle(long bundleId) throws PortalException {
		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.start();
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private void _startBundle(long bundleId, int options)
		throws PortalException {

		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.start(options);
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private void _stopBundle(long bundleId) throws PortalException {
		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.stop();
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private void _stopBundle(long bundleId, int options)
		throws PortalException {

		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.stop(options);
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private void _updateBundle(long bundleId) throws PortalException {
		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.update();
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private void _updateBundle(long bundleId, InputStream inputStream)
		throws PortalException {

		_checkPermission();

		Bundle bundle = _getBundle(bundleId);

		if (bundle == null) {
			throw new ModuleFrameworkException("No bundle with ID " + bundleId);
		}

		try {
			bundle.update(inputStream);
		}
		catch (BundleException be) {
			_log.error(be, be);

			throw new ModuleFrameworkException(be);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		LiferayModuleFrameworkService.class);

	private Framework _framework;
	private LogBridge _logBridge;

}
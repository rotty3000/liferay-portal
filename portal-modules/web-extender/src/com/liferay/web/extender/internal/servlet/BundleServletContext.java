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

package com.liferay.web.extender.internal.servlet;

import com.liferay.portal.apache.bridges.struts.LiferayServletContext;
import com.liferay.portal.kernel.deploy.hot.DependencyManagementThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.PortletSessionListenerManager;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.struts.AuthPublicPathRegistry;
import com.liferay.portal.util.PortalUtil;
import com.liferay.web.extender.internal.http.HttpServiceTracker;
import com.liferay.web.extender.servlet.BundleServletConfig;

import java.io.File;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.NamespaceException;

/**
 * @author Raymond Augé
 */
public class BundleServletContext extends LiferayServletContext
	implements ModuleFrameworkConstants {

	public BundleServletContext(
		Bundle bundle, WebExtenderServlet webExtenderServlet) {

		super(webExtenderServlet.getServletContext());

		_bundle = bundle;
		_webExtenderServlet = webExtenderServlet;
	}

	public void close() {
		_httpServiceTracker.close();

		FileUtil.deltree(_tempDir);
	}

	public Bundle getBundle() {
		return _bundle;
	}

	public static String getServletContextName(Bundle bundle) {
		Dictionary<String,String> headers = bundle.getHeaders();

		String webContextPath = headers.get(WEB_CONTEXTPATH);

		if (Validator.isNull(webContextPath)) {
			return null;
		}

		return webContextPath.substring(1);
	}

	@Override
	public Object getAttribute(String name) {
		if (name.equals(OSGI_BUNDLE)) {
			return _bundle;
		}
		else if (name.equals(OSGI_BUNDLECONTEXT)) {
			// This is required to meet OSGi Comp 4.3, WAS 128.6.1.

			return _bundle.getBundleContext();
		}
		else if (name.equals(JavaConstants.JAVAX_SERVLET_CONTEXT_TEMPDIR)) {
			return getTempDir();
		}

		Object value = _contextAttributes.get(name);

		if ((value == null)) {
			if (name.equals(PluginContextListener.PLUGIN_CLASS_LOADER)) {
				return getClassLoader();
			}
			else if (name.equals("org.apache.tomcat.InstanceManager")) {
				return super.getAttribute(name);
			}
		}

		return value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		Set<String> attributeNames = new HashSet<String>(
			_contextAttributes.keySet());

		for (Enumeration<String> names = super.getAttributeNames();
				names.hasMoreElements();) {

			attributeNames.add(names.nextElement());
		}

		return Collections.enumeration(attributeNames);
	}

	public ClassLoader getClassLoader() {
		Object value = _contextAttributes.get(
			PluginContextListener.PLUGIN_CLASS_LOADER);

		if (value == null) {
			BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

			value = bundleWiring.getClassLoader();

			_contextAttributes.put(
				PluginContextListener.PLUGIN_CLASS_LOADER, value);
		}

		return (ClassLoader)value;
	}

	@Override
	public String getContextPath() {
		if (_contextPath == null) {
			StringBundler sb = new StringBundler(4);

			String contextPath = super.getContextPath();

			if (!contextPath.equals(StringPool.SLASH)) {
				sb.append(contextPath);
			}

			sb.append(PortalUtil.getPathMain());
			sb.append(MODULE_MAPPING);
			sb.append(getServletContextName());

			_contextPath = sb.toString();
		}

		return _contextPath;
	}

	@Override
	public String getInitParameter(String name) {
		return _initParams.get(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_initParams.keySet());
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		String alias = path;

		BundleFilterChain bundleFilterChain = getFilterChain(alias);

		String pathMain = PortalUtil.getPathMain();

		String contextPath = getContextPath();

		if (contextPath.startsWith(pathMain)) {
			contextPath = contextPath.substring(pathMain.length());
		}

		if (path.startsWith(MODULE_MAPPING) && path.endsWith(INVOKER_PATH)) {
			bundleFilterChain.setServlet(_webExtenderServlet);

			if (path.startsWith(contextPath)) {
				path = path.substring(contextPath.length());
			}

			return new BundleRequestDispatcher(
				path, false, path, this, bundleFilterChain);
		}

		if (!isValidPath(path)) {
			return null;
		}

		if (Validator.isNull(alias)) {
			alias = StringPool.SLASH;
		}

		if (_servletsMap.containsKey(alias)) {
			bundleFilterChain.setServlet(_servletsMap.get(alias));

			return new BundleRequestDispatcher(
				alias, false, path, this, bundleFilterChain);
		}

		String extensionMapping = FileUtil.getExtension(alias).toLowerCase();

		if (Validator.isNotNull(extensionMapping)) {
			extensionMapping = _EXTENSION_PREFIX.concat(extensionMapping);
		}

		alias = alias.substring(0, alias.lastIndexOf(StringPool.SLASH));

		while (alias.length() != 0) {
			if (_servletsMap.containsKey(alias)) {
				bundleFilterChain.setServlet(_servletsMap.get(alias));

				return new BundleRequestDispatcher(
					alias, false, path, this, bundleFilterChain);
			}
			else if (_servletsMap.containsKey(alias.concat(extensionMapping))) {
				bundleFilterChain.setServlet(
					_servletsMap.get(alias.concat(extensionMapping)));

				return new BundleRequestDispatcher(
					alias.concat(extensionMapping), true, path, this,
					bundleFilterChain);
			}

			alias = alias.substring(0, alias.lastIndexOf(StringPool.SLASH));
		}

		if (_servletsMap.containsKey(
				StringPool.SLASH.concat(extensionMapping))) {

			bundleFilterChain.setServlet(
				_servletsMap.get(StringPool.SLASH.concat(extensionMapping)));

			return new BundleRequestDispatcher(
				StringPool.SLASH.concat(extensionMapping), true, path, this,
				bundleFilterChain);
		}

		if (_servletsMap.containsKey(StringPool.SLASH)) {
			bundleFilterChain.setServlet(_servletsMap.get(StringPool.SLASH));

			return new BundleRequestDispatcher(
				StringPool.SLASH, false, path, this, bundleFilterChain);
		}

		return null;
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		String filePattern = path;

		int pos = path.lastIndexOf(StringPool.SLASH);

		if (pos != -1) {
			filePattern = path.substring(pos + 1);
			path = path.substring(0, pos);
		}

		Enumeration<URL> findEntries = _bundle.findEntries(
			path, filePattern, false);

		if ((findEntries != null) && findEntries.hasMoreElements()) {
			return findEntries.nextElement();
		}

		return null;
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		String filePattern = path;

		int pos = path.lastIndexOf(StringPool.SLASH);

		if (pos != -1) {
			filePattern = path.substring(pos + 1);
			path = path.substring(0, pos);
		}

		Enumeration<URL> findEntries = _bundle.findEntries(
			path, filePattern, false);

		if ((findEntries != null) && findEntries.hasMoreElements()) {
			try {
				return findEntries.nextElement().openStream();
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		return null;
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		Set<String> resourcePaths = new HashSet<String>();

		Enumeration<String> resources = _bundle.getEntryPaths(path);

		if ((resources != null) && resources.hasMoreElements()) {
			while (resources.hasMoreElements()) {
				String resourcePath = resources.nextElement();

				resourcePaths.add(StringPool.SLASH.concat(resourcePath));
			}
		}

		return resourcePaths;
	}

	@Override
	public String getServletContextName() {
		if (_servletContextName == null) {
			_servletContextName = getServletContextName(_bundle);
		}

		return _servletContextName;
	}

	public List<ServletRequestAttributeListener>
		getServletRequestAttributeListeners() {

		return _servletRequestAttributeListeners;
	}

	public List<ServletRequestListener> getServletRequestListeners() {
		return _servletRequestListeners;
	}

	public void open() {
		_httpServiceTracker = new HttpServiceTracker(
				_bundle.getBundleContext());

		_httpServiceTracker.open();
	}

	public void registerFilter(
			String filterMapping, Filter filter,
			Dictionary<String, String> initParams, HttpContext httpContext)
		throws ServletException, NamespaceException {

		validate(filterMapping, filter, httpContext);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			currentThread.setContextClassLoader(getClassLoader());

			FilterConfig filterConfig = new BundleFilterConfig(
				this, filterMapping, initParams, httpContext);

			filter.init(filterConfig);

			_filtersMap.put(filterMapping, filter);
			_filterList.add(new Object[] {filterMapping, filter});
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public void registerListener(
			Object listener, Dictionary<String, String> initParams,
			HttpContext httpContext)
		throws ServletException {

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		boolean enabled = DependencyManagementThreadLocal.isEnabled();

		try {
			currentThread.setContextClassLoader(getClassLoader());

			DependencyManagementThreadLocal.setEnabled(false);

			for (Enumeration<String> keys = initParams.keys();
					keys.hasMoreElements();) {

				String key = keys.nextElement();
				String value = initParams.get(key);

				if (_initParams.containsKey(key)) {
					continue;
				}

				_initParams.put(key, value);
			}

			if (listener instanceof HttpSessionActivationListener) {
				PortletSessionListenerManager.
					addHttpSessionActivationListener(
						(HttpSessionActivationListener)listener);
			}
			if (listener instanceof HttpSessionAttributeListener) {
				PortletSessionListenerManager.
					addHttpSessionAttributeListener(
						(HttpSessionAttributeListener)listener);
			}
			if (listener instanceof HttpSessionBindingListener) {
				PortletSessionListenerManager.
					addHttpSessionBindingListener(
						(HttpSessionBindingListener)listener);
			}
			if (listener instanceof HttpSessionListener) {
				PortletSessionListenerManager.addHttpSessionListener(
					(HttpSessionListener)listener);
			}
			if (listener instanceof ServletContextAttributeListener) {
				// done
				_servletContextAttributeListeners.add(
					(ServletContextAttributeListener)listener);
			}
			if (listener instanceof ServletContextListener) {
				// done
				ServletContextListener servletContextListener =
					(ServletContextListener)listener;

				ServletContextEvent servletContextEvent =
					new ServletContextEvent(this);

				servletContextListener.contextInitialized(servletContextEvent);

				_servletContextListeners.add(servletContextListener);
			}
			if (listener instanceof ServletRequestAttributeListener) {
				// done
				_servletRequestAttributeListeners.add(
					(ServletRequestAttributeListener)listener);
			}
			if (listener instanceof ServletRequestListener) {
				// done
				_servletRequestListeners.add(
					(ServletRequestListener)listener);
			}
		}
		finally {
			DependencyManagementThreadLocal.setEnabled(enabled);

			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public void registerResources(
			String alias, String name, HttpContext httpContext)
		throws NamespaceException {

		validate(name);

		Servlet servlet = new ResourceServlet(alias, name, httpContext);

		try {
			registerServlet(alias, servlet, null, httpContext);

			StringBundler sb = new StringBundler(3);

			sb.append(MODULE_MAPPING);
			sb.append(getServletContextName());
			sb.append(alias);

			AuthPublicPathRegistry.register(sb.toString());
		}
		catch (ServletException se) {
			throw new IllegalArgumentException(se);
		}
	}

	public void registerServlet(
			String alias, Servlet servlet,
			Dictionary<String, String> initParams, HttpContext httpContext)
		throws ServletException, NamespaceException {

		validate(alias, servlet, httpContext);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			currentThread.setContextClassLoader(getClassLoader());

			ServletConfig servletConfig = new BundleServletConfig(
				this, alias, initParams, httpContext);

			servlet.init(servletConfig);

			_servletsMap.put(alias, servlet);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Registered servlet at " + getContextPath().concat(alias));
			}
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	@Override
	public void removeAttribute(String name) {
		Object value = _contextAttributes.remove(name);

		for(ServletContextAttributeListener listener :
				_servletContextAttributeListeners) {

			listener.attributeRemoved(
				new ServletContextAttributeEvent(this, name, value));
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (name.equals(JavaConstants.JAVAX_SERVLET_CONTEXT_TEMPDIR) ||
			name.equals(PluginContextListener.PLUGIN_CLASS_LOADER)) {

			return;
		}

		Object originalValue = _contextAttributes.get(name);

		_contextAttributes.put(name, value);

		for(ServletContextAttributeListener listener :
				_servletContextAttributeListeners) {

			// TODO add try/catch

			if (originalValue != null) {
				listener.attributeReplaced(
					new ServletContextAttributeEvent(
						this, name, originalValue));
			}
			else {
				listener.attributeAdded(
					new ServletContextAttributeEvent(this, name, value));
			}
		}
	}

	public void setServletContextName(String servletContextName) {
		_servletContextName = servletContextName;
	}

	public void unregister(String alias) {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			currentThread.setContextClassLoader(getClassLoader());

			Servlet servlet = _servletsMap.get(alias);

			if (servlet == null) {
				return;
			}

			StringBundler sb = new StringBundler(3);

			sb.append(MODULE_MAPPING);
			sb.append(getServletContextName());
			sb.append(alias);

			AuthPublicPathRegistry.unregister(sb.toString());

			servlet.destroy();

			_servletsMap.remove(servlet);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public void unregisterFilter(String filterMapping) {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			currentThread.setContextClassLoader(getClassLoader());

			Filter filter = _filtersMap.get(filterMapping);

			if (filter == null) {
				return;
			}

			filter.destroy();

			_filtersMap.remove(filter);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public void unregisterListener(Object listener) {
		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		boolean enabled = DependencyManagementThreadLocal.isEnabled();

		try {
			currentThread.setContextClassLoader(getClassLoader());

			DependencyManagementThreadLocal.setEnabled(false);

			if (listener instanceof HttpSessionActivationListener) {
				PortletSessionListenerManager.
					removeHttpSessionActivationListener(
						(HttpSessionActivationListener)listener);
			}
			if (listener instanceof HttpSessionAttributeListener) {
				PortletSessionListenerManager.
					removeHttpSessionAttributeListener(
						(HttpSessionAttributeListener)listener);
			}
			if (listener instanceof HttpSessionBindingListener) {
				PortletSessionListenerManager.removeHttpSessionBindingListener(
					(HttpSessionBindingListener)listener);
			}
			if (listener instanceof HttpSessionListener) {
				PortletSessionListenerManager.removeHttpSessionListener(
					(HttpSessionListener)listener);
			}
			if (listener instanceof ServletContextAttributeListener) {
				_servletContextAttributeListeners.remove(listener);
			}
			if (listener instanceof ServletContextListener) {
				if (_servletContextListeners.contains(listener)) {
					_servletContextListeners.remove(listener);

					ServletContextListener servletContextListener =
						(ServletContextListener)listener;

					ServletContextEvent servletContextEvent =
						new ServletContextEvent(this);

					servletContextListener.contextDestroyed(
						servletContextEvent);
				}
			}
			if (listener instanceof ServletRequestAttributeListener) {
				_servletRequestAttributeListeners.remove(listener);
			}
			if (listener instanceof ServletRequestListener) {
				_servletRequestListeners.remove(listener);
			}
		}
		finally {
			DependencyManagementThreadLocal.setEnabled(enabled);

			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	protected BundleFilterChain getFilterChain(String alias) {
		BundleFilterChain bundleFilterChain = new BundleFilterChain();

		for (Object[] filterDefinition : _filterList) {
			String filterMapping = (String)filterDefinition[0];
			Filter filter = (Filter)filterDefinition[1];

			if (filterMapping.equals(alias)) {
				bundleFilterChain.addFilter(filter);
			}

			if (filterMapping.contains(StringPool.STAR)) {
				filterMapping = filterMapping.replaceAll(
					"\\".concat(StringPool.STAR), ".*");
			}

			if (alias.matches(filterMapping)) {
				bundleFilterChain.addFilter(filter);
			}
		}

		return bundleFilterChain;
	}

	protected File getTempDir() {
		if (_tempDir == null) {
			File parentTempDir = (File)super.getAttribute(
				JavaConstants.JAVAX_SERVLET_CONTEXT_TEMPDIR);

			String servletContextName = getServletContextName();

			File tempDir = new File(parentTempDir, servletContextName);

			if (!tempDir.exists() && !tempDir.mkdirs()) {
				throw new IllegalStateException(
					"Can't create webapp tempDir for " +
						getServletContextName());
			}

			_tempDir = tempDir;
		}

		return _tempDir;
	}

	protected boolean isValidPath(String path) {
		for (String illegalPath : _ILLEGAL_PATHS) {
			if (path.contains(illegalPath)) {
				return false;
			}
		}

		return true;
	}

	protected void validate(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}

		if (name.endsWith(StringPool.SLASH) && !name.equals(StringPool.SLASH)) {
			throw new IllegalArgumentException("Invalid name " + name);
		}
	}

	protected void validate(
			String filterMapping, Filter filter, HttpContext httpContext)
		throws NamespaceException {

		if (filterMapping == null) {
			throw new IllegalArgumentException("FilterMapping cannot be null");
		}

		if (filterMapping.endsWith(StringPool.SLASH) &&
			!filterMapping.equals(StringPool.SLASH)) {

			throw new IllegalArgumentException(
				"Invalid filterMapping " + filterMapping);
		}

		if (filter == null) {
			throw new IllegalArgumentException("Filter must not be null");
		}

		if (_filtersMap.containsValue(filter)) {
			throw new IllegalArgumentException("Filter is already registered");
		}

		if (httpContext == null) {
			throw new IllegalArgumentException("HttpContext cannot be null");
		}
	}

	protected void validate(
			String alias, Servlet servlet, HttpContext httpContext)
		throws NamespaceException {

		if (Validator.isNull(alias)) {
			throw new IllegalArgumentException(
				"Empty aliases are not allowed");
		}

		if (!alias.startsWith(StringPool.SLASH) ||
			(alias.endsWith(StringPool.SLASH) &&
			 !alias.equals(StringPool.SLASH))) {

			throw new IllegalArgumentException(
				"Alias must start with / but must not end with it");
		}

		if (_servletsMap.containsKey(alias)) {
			throw new NamespaceException("Alias " + alias + " already exists");
		}

		if (servlet == null) {
			throw new IllegalArgumentException("Servlet must not be null");
		}

		if (_servletsMap.containsValue(servlet)) {
			throw new IllegalArgumentException("Servlet is already registered");
		}

		if (httpContext == null) {
			throw new IllegalArgumentException("HttpContext cannot be null");
		}
	}

	private static final String[] _ILLEGAL_PATHS = new String[] {
		"WEB-INF/", "OSGI-INF/", "META-INF/", "OSGI-OPT/"
	};
	private static final String _EXTENSION_PREFIX = "*.";

	private static final Log _log = LogFactoryUtil.getLog(
		BundleServletContext.class);

	private Bundle _bundle;
	private Map<String, Object> _contextAttributes =
		new ConcurrentHashMap<String, Object>();
	private String _contextPath;
	private Map<String, Filter> _filtersMap =
		new ConcurrentHashMap<String, Filter>();
	private Map<String, String> _initParams = new HashMap<String, String>();
	private List<Object[]> _filterList = new ArrayList<Object[]>();
	private HttpServiceTracker _httpServiceTracker;
	private List<ServletContextAttributeListener>
		_servletContextAttributeListeners =
			new ArrayList<ServletContextAttributeListener>();
	private List<ServletContextListener> _servletContextListeners =
		new ArrayList<ServletContextListener>();
	private List<ServletRequestAttributeListener>
		_servletRequestAttributeListeners =
			new ArrayList<ServletRequestAttributeListener>();
	private List<ServletRequestListener> _servletRequestListeners =
		new ArrayList<ServletRequestListener>();
	private Servlet _webExtenderServlet;
	private String _servletContextName;
	private Map<String, Servlet> _servletsMap =
		new ConcurrentHashMap<String, Servlet>();
	private File _tempDir;

}
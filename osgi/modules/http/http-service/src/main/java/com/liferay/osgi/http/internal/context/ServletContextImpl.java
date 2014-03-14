/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.osgi.http.internal.context;

import com.liferay.osgi.http.internal.holder.FilterHolder;
import com.liferay.osgi.http.internal.holder.FilterHolderComparator;
import com.liferay.osgi.http.internal.holder.Holder;
import com.liferay.osgi.http.internal.servlet.RequestDispatcherImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

import java.security.AccessControlContext;
import java.security.AccessController;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.osgi.dto.DTO;
import org.osgi.service.http.ServletContextHelper;
import org.osgi.service.http.runtime.ErrorPageDTO;
import org.osgi.service.http.runtime.FilterDTO;
import org.osgi.service.http.runtime.ListenerDTO;
import org.osgi.service.http.runtime.ResourceDTO;
import org.osgi.service.http.runtime.ServletContextDTO;
import org.osgi.service.http.runtime.ServletDTO;

/**
 * @author Raymond Augé
 */
public class ServletContextImpl implements ServletContext {

	public ServletContextImpl(
		ServletContext servletContext,
		ServletContextHelper servletContextHelper,
		ServletContextHelperProperties schProperties, ClassLoader classLoader) {

		_servletContext = servletContext;
		_servletContextHelper = servletContextHelper;
		_schProperties = schProperties;
		_classLoader = classLoader;

		_contextName = _schProperties.getContextName();
		_contextPath = _schProperties.getContextPath();
		_shared = _schProperties.getProps().
			osgi_http_whiteboard_context_shared();

		_accessControlContext = AccessController.getContext();
		_attributes = new ConcurrentHashMap<String, Object>();
		_filters = new ConcurrentSkipListSet<FilterHolder>(
			new FilterHolderComparator());
		_servlets =
			new ConcurrentHashMap<Servlet, Holder<Servlet, ? extends DTO>>();
		_servletRequestAttributeListeners =
			new CopyOnWriteArrayList<ServletRequestAttributeListener>();
		_servletRequestListeners =
			new CopyOnWriteArrayList<ServletRequestListener>();
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
		String filterName, Class<? extends Filter> clazz) {

		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
		String filterName, Filter filter) {

		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
		String filterName, String className) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(Class<? extends EventListener> clazz) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(String className) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> void addListener(T eventListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(
		String servletName, Class<? extends Servlet> clazz) {

		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(
		String servletName, Servlet servlet) {

		throw new UnsupportedOperationException();
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(
		String servletName, String className) {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> clazz)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz)
		throws ServletException {

		throw new UnsupportedOperationException();
	}

	@Override
	public void declareRoles(String... arg0) {
		throw new UnsupportedOperationException();
	}

	public AccessControlContext getAccessControlContext() {
		return _accessControlContext;
	}

	@Override
	public Object getAttribute(String name) {
		return _attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(_attributes.keySet());
	}

	public Map<String, Object> getAttributes() {
		return _attributes;
	}

	@Override
	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	@Override
	public ServletContext getContext(String uriPath) {
		return _servletContext.getContext(uriPath);
	}

	@Override
	public String getContextPath() {
		return _contextPath;
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 3;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		throw new UnsupportedOperationException();
	}

	public Set<FilterHolder> getFilters() {
		return _filters;
	}

	@Override
	public String getInitParameter(String name) {
		return String.valueOf(_schProperties.getRawProperties().get(name));
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(
			_schProperties.getRawProperties().keySet());
	}

	public Map<String, Object> getInitParameters() {
		return _schProperties.getRawProperties();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMajorVersion() {
		return 3;
	}

	@Override
	public String getMimeType(String file) {
		return _servletContextHelper.getMimeType(file);
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		if ((name == null) || (name.equals(""))) {
			return null;
		}

		Servlet servletMatch = getServletMatch(null, name);

		if (servletMatch == null) {
			return null;
		}

		return new RequestDispatcherImpl(
			this, null, null, null, name, servletMatch);
	}

	@Override
	public String getRealPath(String path) {
		return _servletContextHelper.getRealPath(path);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		if ((path == null) || (path.equals(""))) {
			return null;
		}

		if (!isValidPath(path)) {
			return null;
		}

		try {
			path = URLDecoder.decode(path, _UTF_8);
			path = URI.create(path).normalize().getPath();
		}
		catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
		}

		String uri = _contextPath.concat(path);

		Servlet servletMatch = getServletMatch(path, null);

		if (servletMatch == null) {
			return _servletContext.getRequestDispatcher(uri);
		}

		return new RequestDispatcherImpl(
			this, uri, path, null, null, servletMatch);
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return _servletContextHelper.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		URL resource = _servletContextHelper.getResource(path);

		if (resource == null) {
			return null;
		}

		try {
			return resource.openStream();
		}
		catch (IOException e) {
			return null;
		}
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return _servletContextHelper.getResourcePaths(path);
	}

	@Override
	public String getServerInfo() {
		return _servletContext.getServerInfo();
	}

	public long getServiceId() {
		return _schProperties.getProps().service_id();
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		throw new UnsupportedOperationException();
	}

	public ServletContextHelper getServletContextHelper() {
		return _servletContextHelper;
	}

	@Override
	public String getServletContextName() {
		return _contextName;
	}

	public ConcurrentMap<Servlet, Holder<Servlet, ? extends DTO>>
		getServletMap() {

		return _servlets;
	}

	@Override
	public Enumeration<String> getServletNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		throw new UnsupportedOperationException();
	}

	public List<ServletRequestAttributeListener>
		getServletRequestAttributeListeners() {

		return _servletRequestAttributeListeners;
	}
	public List<ServletRequestListener> getServletRequestListeners() {
		return _servletRequestListeners;
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		throw new UnsupportedOperationException();
	}

	public boolean isShared() {
		return _shared;
	}

	@Override
	public void log(Exception arg0, String arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String name) {
		_attributes.remove(name);
	}

	public void removeServlet(Servlet servlet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, Object value) {
		_attributes.put(name, value);
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		return false;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		throw new UnsupportedOperationException();
	}

	public ServletContextDTO toDTO() {
		ServletContextDTO contextDTO = new ServletContextDTO();

		contextDTO.attributes = Collections.unmodifiableMap(getAttributes());
		contextDTO.contextName = getServletContextName();
		contextDTO.contextPath = getContextPath();

		Map<String, String> initParams = new HashMap<String, String>();

		Iterator<Entry<String, Object>> iterator =
			getInitParameters().entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();

			initParams.put(entry.getKey(), String.valueOf(entry.getValue()));
		}

		contextDTO.initParams = Collections.unmodifiableMap(initParams);
		contextDTO.serviceId = getServiceId();
		contextDTO.shared = isShared();

		Set<ErrorPageDTO> errorPageDTOs = new HashSet<ErrorPageDTO>();
		Set<ResourceDTO> resourceDTOs = new HashSet<ResourceDTO>();
		Set<ServletDTO> servletDTOs = new HashSet<ServletDTO>();

		Collection<Holder<Servlet, ? extends DTO>> holders =
			getServletMap().values();

		for (Holder<Servlet, ? extends DTO> holder : holders) {
			if (holder.d instanceof ErrorPageDTO) {
				errorPageDTOs.add((ErrorPageDTO)holder.d);
			}
			else if (holder.d instanceof ResourceDTO) {
				resourceDTOs.add((ResourceDTO)holder.d);
			}
			else if (holder.d instanceof ServletDTO) {
				servletDTOs.add((ServletDTO)holder.d);
			}
		}

		contextDTO.errorPageDTOs = errorPageDTOs.toArray(
			new ErrorPageDTO[errorPageDTOs.size()]);
		contextDTO.resourceDTOs = resourceDTOs.toArray(
			new ResourceDTO[resourceDTOs.size()]);
		contextDTO.servletDTOs = servletDTOs.toArray(
			new ServletDTO[servletDTOs.size()]);

		Set<String> names = new HashSet<String>();

		names.add(_schProperties.getContextName());
		names.addAll(
			_schProperties.getProps().osgi_http_whiteboard_context_name());

		contextDTO.names = names.toArray(new String[names.size()]);

		// TODO
		contextDTO.filterDTOs = new FilterDTO[0];
		contextDTO.listenerDTOs = new ListenerDTO[0];

		return contextDTO;
	}

	protected Servlet getServletMatch(String requestURI, String name) {
		ConcurrentMap<Servlet, Holder<Servlet, ? extends DTO>> servletMap =
			getServletMap();

		Servlet servlet;

		for (Holder<Servlet, ? extends DTO> holder : servletMap.values()) {
			if ((servlet = holder.match(requestURI, name)) != null) {
				return servlet;
			}
		}

		return null;
	}

	protected boolean isValidPath(String path) {
		if (!path.startsWith("/")) {
			path = "/".concat(path);
		}

		for (String illegalPath : _ILLEGAL_PATHS) {
			if (path.startsWith(illegalPath)) {
				return false;
			}
		}

		return true;
	}

	private static final String _UTF_8 = "UTF-8";

	private static final String[] _ILLEGAL_PATHS = new String[] {
		"/META-INF/", "/OSGI-INF/", "/OSGI-OPT/", "/WEB-INF/"
	};

	private final AccessControlContext _accessControlContext;
	private final Map<String, Object> _attributes;
	private ClassLoader _classLoader;
	private final String _contextName;
	private final String _contextPath;
	private Set<FilterHolder> _filters;
	private final ServletContextHelperProperties _schProperties;
	private final ServletContext _servletContext;
	private final ServletContextHelper _servletContextHelper;
	private ConcurrentMap<Servlet, Holder<Servlet, ? extends DTO>> _servlets;
	private final List<ServletRequestAttributeListener>
		_servletRequestAttributeListeners;
	private final List<ServletRequestListener> _servletRequestListeners;
	private boolean _shared;

}
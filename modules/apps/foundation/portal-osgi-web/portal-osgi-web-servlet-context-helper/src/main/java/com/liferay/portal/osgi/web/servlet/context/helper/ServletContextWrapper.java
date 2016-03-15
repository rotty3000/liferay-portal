/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portal.osgi.web.servlet.context.helper;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.osgi.framework.Bundle;

/**
 * @author Juan Gonzalez
 *
 */
public class ServletContextWrapper implements ServletContext {

	public ServletContextWrapper(Bundle bundle, ServletContext servletContext) {
		_bundle = bundle;
		_servletContext = servletContext;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
		String filterName, Class<? extends Filter> filterClass) {

		return addFilter(filterName, filterClass.getName());
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
		String filterName, Filter filter) {

		checkInitiated();

		FilterRegistrationImpl filterRegistrationImpl =
			getFilterRegistrationImpl(filterName);

		if (filterRegistrationImpl == null) {
			filterRegistrationImpl = new FilterRegistrationImpl();
		}

		Class<? extends Filter> filterClass = filter.getClass();
		filterRegistrationImpl.setClassName(filterClass.getName());
		filterRegistrationImpl.setName(filterName);
		filterRegistrationImpl.setInstance(filter);
		_filterRegistrations.put(filterName, filterRegistrationImpl);

		return filterRegistrationImpl;
	}

	@Override
	public javax.servlet.FilterRegistration.Dynamic addFilter(
		String filterName, String className) {

		checkInitiated();

		FilterRegistrationImpl filterRegistrationImpl =
			getFilterRegistrationImpl(filterName);

		if (filterRegistrationImpl == null) {
			filterRegistrationImpl = new FilterRegistrationImpl();
		}

		filterRegistrationImpl.setClassName(className);
		filterRegistrationImpl.setName(filterName);
		_filterRegistrations.put(filterName, filterRegistrationImpl);

		return filterRegistrationImpl;
	}

	@Override
	public void addListener(Class<? extends EventListener> listenerClass) {
		checkInitiated();

		_listeners.put(listenerClass, null);
	}

	@Override
	public void addListener(String className) {
		checkInitiated();

		try {
			Class<?> clazz = _bundle.loadClass(className);

			if (!EventListener.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException();
			}

			Class<? extends EventListener> listenerClass = clazz.asSubclass(
				EventListener.class);

			_listeners.put(listenerClass, null);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(
				"Bundle " + _bundle + " is unable to load filter " + className);
		}
	}

	@Override
	public <T extends EventListener> void addListener(T t) {
		checkInitiated();

		_listeners.put(t.getClass(), t);
	}

	@Override
	public Dynamic addServlet(
		String servletName, Class<? extends Servlet> servletClass) {

		return addServlet(servletName, servletClass.getName());
	}

	@Override
	public Dynamic addServlet(String servletName, Servlet servlet) {
		checkInitiated();

		ServletRegistrationImpl servletRegistrationImpl =
			getServletRegistrationImpl(servletName);

		if (servletRegistrationImpl == null) {
			servletRegistrationImpl = new ServletRegistrationImpl();
		}

		Class<? extends Servlet> servetClass = servlet.getClass();
		servletRegistrationImpl.setClassName(servetClass.getName());
		servletRegistrationImpl.setName(servletName);
		servletRegistrationImpl.setInstance(servlet);
		_servletRegistrations.put(servletName, servletRegistrationImpl);

		return servletRegistrationImpl;
	}

	@Override
	public Dynamic addServlet(String servletName, String className) {
		checkInitiated();

		ServletRegistrationImpl servletRegistrationImpl =
			getServletRegistrationImpl(servletName);

		if (servletRegistrationImpl == null) {
			servletRegistrationImpl = new ServletRegistrationImpl();
		}

		servletRegistrationImpl.setClassName(className);
		servletRegistrationImpl.setName(servletName);
		_servletRegistrations.put(servletName, servletRegistrationImpl);

		return servletRegistrationImpl;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> clazz)
		throws ServletException {

		try {
			return clazz.newInstance();
		}
		catch (Throwable t) {
			throw new ServletException(
				"Bundle " + _bundle + " is unable to load filter " + clazz);
		}
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> clazz)
		throws ServletException {

		try {
			return clazz.newInstance();
		}
		catch (Throwable t) {
			throw new ServletException(
				"Bundle " + _bundle + " is unable to load listener " + clazz);
		}
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> clazz)
		throws ServletException {

		try {
			return clazz.newInstance();
		}
		catch (Throwable t) {
			throw new ServletException(
				"Bundle " + _bundle + " is unable to load servlet " + clazz);
		}
	}

	@Override
	public void declareRoles(String... roleNames) {
		getWrapped().declareRoles(roleNames);
	}

	@Override
	public Object getAttribute(String name) {
		return getWrapped().getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return getWrapped().getAttributeNames();
	}

	@Override
	public ClassLoader getClassLoader() {
		return getWrapped().getClassLoader();
	}

	@Override
	public ServletContext getContext(String uripath) {
		return getWrapped().getContext(uripath);
	}

	@Override
	public String getContextPath() {
		return getWrapped().getContextPath();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return getWrapped().getDefaultSessionTrackingModes();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return getWrapped().getEffectiveMajorVersion();
	}

	@Override
	public int getEffectiveMinorVersion() {
		return getWrapped().getEffectiveMinorVersion();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return getWrapped().getEffectiveSessionTrackingModes();
	}

	@Override
	public FilterRegistration getFilterRegistration(String filterName) {
		return getFilterRegistrationImpl(filterName);
	}

	public FilterRegistrationImpl getFilterRegistrationImpl(String filterName) {
		return _filterRegistrations.get(filterName);
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return getFilterRegistrationsImpl();
	}

	public Map<String, ? extends FilterRegistrationImpl>
		getFilterRegistrationsImpl() {

		return _filterRegistrations;
	}

	@Override
	public String getInitParameter(String name) {
		return getWrapped().getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return getWrapped().getInitParameterNames();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return getWrapped().getJspConfigDescriptor();
	}

	public Set<Class<? extends EventListener>> getListenerClasses() {
		return _listeners.keySet();
	}

	public Collection<EventListener> getListenerInstances() {
		return _listeners.values();
	}

	public Map<Class<? extends EventListener>, EventListener> getListeners() {
		return _listeners;
	}

	@Override
	public int getMajorVersion() {
		return getWrapped().getMajorVersion();
	}

	@Override
	public String getMimeType(String file) {
		return getWrapped().getMimeType(file);
	}

	@Override
	public int getMinorVersion() {
		return getWrapped().getMinorVersion();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		return getWrapped().getNamedDispatcher(name);
	}

	@Override
	public String getRealPath(String path) {
		return getWrapped().getRealPath(path);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return getWrapped().getRequestDispatcher(path);
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return getWrapped().getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return getWrapped().getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return getWrapped().getResourcePaths(path);
	}

	@Override
	public String getServerInfo() {
		return getWrapped().getServerInfo();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Servlet getServlet(String name) throws ServletException {
		return getWrapped().getServlet(name);
	}

	@Override
	public String getServletContextName() {
		return getWrapped().getServletContextName();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Enumeration<String> getServletNames() {
		return getWrapped().getServletNames();
	}

	@Override
	public ServletRegistration getServletRegistration(String servletName) {
		return getServletRegistrationImpl(servletName);
	}

	public ServletRegistrationImpl getServletRegistrationImpl(
		String servletName) {

		return _servletRegistrations.get(servletName);
	}

	@Override
	public Map<String, ? extends ServletRegistration>
		getServletRegistrations() {

		return getServletRegistrationsImpl();
	}

	public Map<String, ? extends ServletRegistrationImpl>
		getServletRegistrationsImpl() {

		return _servletRegistrations;
	}

	@Override
	@SuppressWarnings("deprecation")
	public Enumeration<Servlet> getServlets() {
		return getWrapped().getServlets();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return getWrapped().getSessionCookieConfig();
	}

	public ServletContext getWrapped() {
		return _servletContext;
	}

	public boolean isInitiated() {
		return _initiated;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void log(Exception exception, String msg) {
		getWrapped().log(exception, msg);
	}

	@Override
	public void log(String msg) {
		getWrapped().log(msg);
	}

	@Override
	public void log(String message, Throwable throwable) {
		getWrapped().log(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		getWrapped().removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		getWrapped().setAttribute(name, object);
	}

	public void setInitiated(boolean initiated) {
		_initiated = initiated;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		return getWrapped().setInitParameter(name, value);
	}

	@Override
	public void setSessionTrackingModes(
		Set<SessionTrackingMode> sessionTrackingModes) {

		getWrapped().setSessionTrackingModes(sessionTrackingModes);
	}

	protected void checkInitiated() {
		if (_initiated) {
			throw new IllegalStateException(
				"Context had already been initialized");
		}
	}

	private final Bundle _bundle;
	private final Map<String, FilterRegistrationImpl>
		_filterRegistrations = new LinkedHashMap<>();
	private boolean _initiated;
	private final Map<Class<? extends EventListener>, EventListener>
		_listeners = new LinkedHashMap<>();
	private final ServletContext _servletContext;
	private final Map<String, ServletRegistrationImpl>
		_servletRegistrations = new LinkedHashMap<>();

}
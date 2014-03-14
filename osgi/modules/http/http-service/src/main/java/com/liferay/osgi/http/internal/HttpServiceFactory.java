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

package com.liferay.osgi.http.internal;

import com.liferay.osgi.http.internal.context.DefaultServletContextHelper;
import com.liferay.osgi.http.internal.context.HttpContextNameAware;
import com.liferay.osgi.http.internal.context.ServletContextHelperProperties;
import com.liferay.osgi.http.internal.context.ServletContextHelperWrapper;
import com.liferay.osgi.http.internal.context.ServletContextImpl;
import com.liferay.osgi.http.internal.servlet.ServletProperties;
import com.liferay.osgi.util.UnmodifiableMapDictionary;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.http.HttpConstants;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.ServletContextHelper;

/**
 * @author Raymond Augé
 */
@Component(
	factory = "http.service"
)
@SuppressWarnings("deprecation")
public class HttpServiceFactory implements HttpService {

	@Override
	public HttpContext createDefaultHttpContext() {
		_lock.lock();

		try {
			return _httpContext;
		}
		finally {
			_lock.unlock();
		}
	}

	@Override
	public void registerResources(
			String alias, String name, HttpContext httpContext)
		throws NamespaceException {

		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}

		Bundle bundle = _componentContext.getUsingBundle();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		_lock.lock();

		try {
			String contextName = _httpContext.getContextName();

			if ((httpContext != null) &&
				(!(httpContext instanceof ServletContextHelperWrapper))) {

				httpContext = new ServletContextHelperWrapper(
					httpContext, (ServletContextHelper)_httpContext,
					contextName);

				_httpContext = (HttpContextNameAware)httpContext;
			}

			properties.put(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME, contextName);
		}
		finally {
			_lock.unlock();
		}

		properties.put(HttpConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, name);
		properties.put(HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, alias);

		ServiceRegistration<Servlet> serviceRegistration =
			bundle.getBundleContext().registerService(
				Servlet.class, new HttpServlet() {/**/}, properties);

		_serviceRegistrations.put(alias, serviceRegistration);
	}

	@Override
	public void registerServlet(
			String alias, Servlet servlet,
			Dictionary<String, String> initparams, HttpContext httpContext)
		throws ServletException, NamespaceException {

		Bundle bundle = _componentContext.getUsingBundle();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		if (initparams != null) {
			for (Enumeration<String> em = initparams.keys();
					em.hasMoreElements();) {

				String key = em.nextElement();

				properties.put(key, initparams.get(key));
			}
		}

		_lock.lock();

		try {
			String contextName = _httpContext.getContextName();

			if ((httpContext != null) &&
				(!(httpContext instanceof ServletContextHelperWrapper))) {

				httpContext = new ServletContextHelperWrapper(
					httpContext, (ServletContextHelper)_httpContext,
					contextName);

				_httpContext = (HttpContextNameAware)httpContext;
			}

			properties.put(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME, contextName);
		}
		finally {
			_lock.unlock();
		}

		properties.put(HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, alias);

		try {
			ServletProperties servletProperties = ServletProperties.cnv(
				properties, servlet);

			properties.put(
				HttpConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED,
				servletProperties.getProps().
					osgi_http_whiteboard_servlet_asyncSupported());

			List<String> errorPage = servletProperties.getProps().
				osgi_http_whiteboard_servlet_errorPage();

			if (errorPage != null) {
				properties.put(
					HttpConstants.HTTP_WHITEBOARD_SERVLET_ERROR_PAGE,
					errorPage);
			}

			properties.put(
				HttpConstants.HTTP_WHITEBOARD_SERVLET_NAME,
				servletProperties.getServletName());
			properties.put(
				HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				servletProperties.getProps().
					osgi_http_whiteboard_servlet_pattern());
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		ServiceRegistration<Servlet> serviceRegistration =
			bundle.getBundleContext().registerService(
				Servlet.class, servlet, properties);

		_serviceRegistrations.put(alias, serviceRegistration);
	}

	@Override
	public void unregister(String alias) {
		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(alias);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	@Activate
	protected void activate(
		ComponentContext componentContext, Map<String, Object> properties) {

		try {
			_componentContext = componentContext;

			ServletContextHelperProperties schProperties =
				ServletContextHelperProperties.cnv(properties);

			ServletContextImpl servletContextImpl =
				_httpServiceImpl.getServletContext(schProperties);

			generateHttpContext(
				servletContextImpl, schProperties.getContextName(), properties);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Deactivate
	protected void deactivate() {
		Iterator<Entry<Object, ServiceRegistration<?>>> iterator =
			_serviceRegistrations.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Object, ServiceRegistration<?>> entry = iterator.next();

			entry.getValue().unregister();
			iterator.remove();
		}

		_serviceRegistrations.clear();

		_componentContext = null;
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY
	)
	protected void setHttpServiceImpl(HttpServiceImpl httpServiceImpl) {
		_httpServiceImpl = httpServiceImpl;
	}

	protected void unsetHttpServiceImpl(HttpServiceImpl httpServiceImpl) {
		_httpServiceImpl = null;
	}

	private void generateHttpContext(
		ServletContextImpl servletContextImpl, String contextName,
		Map<String, Object> properties) {

		_lock.lock();

		try {
			if (servletContextImpl != null) {
				_httpContext =
					(HttpContextNameAware)servletContextImpl.
						getServletContextHelper();

				return;
			}

			Bundle bundle = _componentContext.getUsingBundle();

			DefaultServletContextHelper servletContextHelper =
				new DefaultServletContextHelper(
					_componentContext.getUsingBundle(), contextName);

			_httpContext = servletContextHelper;

			ServiceRegistration<ServletContextHelper> serviceRegistration =
				bundle.getBundleContext().registerService(
					ServletContextHelper.class, servletContextHelper,
					new UnmodifiableMapDictionary<String, Object>(properties));

			_serviceRegistrations.put(
				servletContextHelper, serviceRegistration);
		}
		finally {
			_lock.unlock();
		}
	}

	private ComponentContext _componentContext;
	private HttpContextNameAware _httpContext;
	private HttpServiceImpl _httpServiceImpl;
	private final Lock _lock = new ReentrantLock();
	private final Map<Object, ServiceRegistration<?>> _serviceRegistrations =
		new ConcurrentHashMap<Object, ServiceRegistration<?>>();

}
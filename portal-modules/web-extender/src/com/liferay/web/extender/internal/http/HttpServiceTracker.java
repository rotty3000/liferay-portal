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

package com.liferay.web.extender.internal.http;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.web.extender.internal.definition.FilterDefinition;
import com.liferay.web.extender.internal.definition.ListenerDefinition;
import com.liferay.web.extender.internal.definition.ServletDefinition;
import com.liferay.web.extender.internal.servlet.WebXML;
import com.liferay.web.extender.internal.servlet.WebXMLLoader;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class HttpServiceTracker
	extends ServiceTracker<HttpService, HttpService> {

	public HttpServiceTracker(BundleContext bundleContext) {
		super(bundleContext, HttpService.class, null);

		_bundleContext = bundleContext;
		_bundle = bundleContext.getBundle();
		_webXMLLoader = new WebXMLLoader();
	}

	@Override
	public HttpService addingService(ServiceReference<HttpService> reference) {
		_serviceReference = reference;

		HttpService httpService = _bundleContext.getService(_serviceReference);

		if (httpService == null) {
			return httpService;
		}

		HttpContext httpContext = httpService.createDefaultHttpContext();

		readConfiguration(_bundle);

		initListeners((ExtendedHttpService)httpService, httpContext);
		initServlets(httpService, httpContext);
		initFilters((ExtendedHttpService)httpService, httpContext);
		initMimeResourceMappings(httpService, _bundle, httpContext, "/");

		return httpService;
	}

	@Override
	public void removedService(
		ServiceReference<HttpService> serviceReference,
		HttpService httpService) {

		if (_webXML == null) {
			return;
		}

		destroyMimeResourceMappings(httpService);
		destroyFilters((ExtendedHttpService)httpService);
		destroyServlets(httpService);
		destroyListeners((ExtendedHttpService)httpService);

		_webXML = null;
	}

	protected void destroyFilters(ExtendedHttpService httpService) {
		Map<String, FilterDefinition> filters = _webXML.getFilters();

		for (String filterMapping : filters.keySet()) {
			try {
				httpService.unregisterFilter(filterMapping);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	protected void destroyListeners(ExtendedHttpService httpService) {
		List<ListenerDefinition> listeners = _webXML.getListeners();

		for (ListenerDefinition listenerDefinition : listeners) {
			try {
				httpService.unregisterListener(
					listenerDefinition.getListener());
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	protected void destroyMimeResourceMappings(HttpService httpService) {
		for (String extension : _webXML.getMimeTypes().keySet()) {
			httpService.unregister(extension);
		}
	}

	protected void destroyServlets(HttpService httpService) {
		Map<String, ServletDefinition> servlets = _webXML.getServlets();

		for (String servletMapping : servlets.keySet()) {
			httpService.unregister(servletMapping);
		}
	}

	protected void initFilters(
		ExtendedHttpService httpService, HttpContext httpContext) {

		Map<String, FilterDefinition> filters = _webXML.getFilters();

		for (String filterMapping : filters.keySet()) {
			FilterDefinition filterDefinition = filters.get(filterMapping);

			try {
				httpService.registerFilter(
					filterMapping, filterDefinition.getFilter(),
					filterDefinition.getInitParams(), httpContext);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	protected void initListeners(
		ExtendedHttpService httpService, HttpContext httpContext) {

		List<ListenerDefinition> listeners = _webXML.getListeners();

		for (ListenerDefinition listenerDefinition : listeners) {
			try {
				httpService.registerListener(
					listenerDefinition.getListener(),
					listenerDefinition.getContextParams(), httpContext);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	protected void initMimeResourceMappings(
		HttpService httpService, Bundle bundle, HttpContext httpContext,
		String basePath) {

		Map<String, String> mimeTypes = _webXML.getMimeTypes();
		Map<String, ServletDefinition> servlets = _webXML.getServlets();

		Enumeration<String> entryPaths = bundle.getEntryPaths(basePath);

		while (entryPaths.hasMoreElements()) {
			String entryPath = entryPaths.nextElement();

			if (entryPath.startsWith("META-INF/") ||
				entryPath.startsWith("OSGI-INF/") ||
				entryPath.startsWith("WEB-INF/")) {

				continue;
			}

			entryPath = StringPool.SLASH.concat(entryPath);

			if (entryPath.endsWith(StringPool.SLASH)) {
				initMimeResourceMappings(
					httpService, bundle, httpContext, entryPath);

				continue;
			}

			boolean matches = false;

			for (String servletMapping : servlets.keySet()) {
				String regexMapping = StringUtil.replace(
					servletMapping, new String[] {".", "*"},
					new String[] {"\\.", ".*"});

				if (entryPath.matches(regexMapping)) {
					matches = true;
					break;
				}
			}

			if (matches) {
				continue;
			}

			try {
				if (_log.isDebugEnabled()) {
					_log.debug("registering path: " + entryPath);
				}

				httpService.registerResources(
					entryPath, entryPath, httpContext);

				mimeTypes.put(entryPath, entryPath);
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}
	}

	protected void initServlets(
		HttpService httpService, HttpContext httpContext) {

		Map<String, ServletDefinition> servlets = _webXML.getServlets();

		for (String servletMapping : servlets.keySet()) {
			ServletDefinition servletDefinition = servlets.get(servletMapping);

			try {
				httpService.registerServlet(
					servletMapping, servletDefinition.getServlet(),
					servletDefinition.getInitParams(), httpContext);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	protected void readConfiguration(Bundle bundle) {
		_webXML = _webXMLLoader.loadWebXML(bundle);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpServiceTracker.class);

	private Bundle _bundle;
	private BundleContext _bundleContext;
	private ServiceReference<HttpService> _serviceReference;
	protected WebXML _webXML;
	protected WebXMLLoader _webXMLLoader;

}
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


import java.io.IOException;

import java.net.URL;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.ServletContextHelper;

/**
 * @author Raymond Augé
 */
@SuppressWarnings("deprecation")
public class ServletContextHelperWrapper extends ServletContextHelper
	implements HttpContextNameAware {

	public ServletContextHelperWrapper(
		HttpContext httpContext, ServletContextHelper servletContextHelper,
		String contextName) {

		_httpContext = httpContext;
		_servletContextHelper = servletContextHelper;
		_contextName = contextName;
	}

	@Override
	public String getContextName() {
		return _contextName;
	}

	public HttpContext getHttpContext() {
		return _httpContext;
	}

	@Override
	public String getMimeType(String name) {
		return _httpContext.getMimeType(name);
	}

	@Override
	public String getRealPath(String path) {
		return _servletContextHelper.getRealPath(path);
	}

	@Override
	public URL getResource(String name) {
		return _httpContext.getResource(name);
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		return _servletContextHelper.getResourcePaths(path);
	}

	@Override
	public boolean handleSecurity(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		return _httpContext.handleSecurity(request, response);
	}

	private final String _contextName;
	private final HttpContext _httpContext;
	private final ServletContextHelper _servletContextHelper;

}
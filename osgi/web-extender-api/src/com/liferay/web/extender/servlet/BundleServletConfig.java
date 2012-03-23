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

package com.liferay.web.extender.servlet;

import java.util.Dictionary;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.osgi.service.http.HttpContext;

/**
 * @author Raymond Augé
 */
public class BundleServletConfig implements ServletConfig {

	public BundleServletConfig(
		ServletContext servletContext, String servletName,
		Dictionary<String,String> initParameters, HttpContext httpContext) {

		_servletContext = servletContext;
		_servletName = servletName;
		_initParameters = initParameters;
		_httpContext = httpContext;
	}

	public String getInitParameter(String name) {
		return _initParameters.get(name);
	}

	public Enumeration getInitParameterNames() {
		return _initParameters.keys();
	}

	public HttpContext getHttpContext() {
		return _httpContext;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	public String getServletName() {
		return _servletName;
	}

	private HttpContext _httpContext;
	private Dictionary<String,String> _initParameters;
	private ServletContext _servletContext;
	private String _servletName;

}
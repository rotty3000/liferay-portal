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

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * @author Raymond Augé
 */
public class BundleRequestDispatcher implements RequestDispatcher {

	public static final String INCLUDE_CONTEXT_PATH =
		"javax.servlet.include.context_path";
	public static final String INCLUDE_PATH_INFO =
		"javax.servlet.include.path_info";
	public static final String INCLUDE_QUERY_STRING =
		"javax.servlet.include.query_string";
	public static final String INCLUDE_REQUEST_URI =
		"javax.servlet.include.request_uri";
	public static final String INCLUDE_SERVLET_PATH =
		"javax.servlet.include.servlet_path";

	public BundleRequestDispatcher(
		String servletMapping, boolean extensionMapping, String requestURI,
		Servlet servlet, BundleFilterChain bundleFilterChain) {

		_servletMapping = servletMapping;
		_extensionMapping = extensionMapping;
		_bundleFilterChain = bundleFilterChain;
		_servletConfig = servlet.getServletConfig();
		_servletContext = _servletConfig.getServletContext();

		_bundleFilterChain.setServlet(servlet);

		String contextPath = _servletContext.getContextPath();

		_requestURI = StringUtil.replace(
			requestURI, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		_contextPath = contextPath;
		_pathInfo = requestURI;
		_queryString = StringPool.BLANK;
		_servletPath = StringPool.BLANK;

		if (!_extensionMapping) {
			_servletPath = _servletMapping;
		}

		int pos = -1;

		if (!StringPool.BLANK.equals(_servletPath)) {
			_requestURI.indexOf(_servletPath);
		}

		if (pos != -1) {
			_pathInfo = _requestURI.substring(pos + _servletPath.length());
		}
	}

	public void forward(ServletRequest request, ServletResponse response)
		throws IOException, ServletException {

		request = new BundleServletRequest(
			(HttpServletRequest)request, _servletContext);

		doDispatch(request, response);
	}

	public void include(ServletRequest request, ServletResponse response)
		throws IOException, ServletException {

		request = new BundleServletRequest(
			(HttpServletRequest)request, _servletContext);

		HttpServletRequest httpServletRequest = (HttpServletRequest)request;

		if (_contextPath != null) {
			httpServletRequest.setAttribute(INCLUDE_CONTEXT_PATH, _contextPath);
		}

		if (_pathInfo != null) {
			httpServletRequest.setAttribute(INCLUDE_PATH_INFO, _pathInfo);
		}

		if (_queryString != null) {
			httpServletRequest.setAttribute(INCLUDE_QUERY_STRING, _queryString);
		}

		if (_requestURI != null) {
			httpServletRequest.setAttribute(INCLUDE_REQUEST_URI, _requestURI);
		}

		if (_servletPath != null) {
			httpServletRequest.setAttribute(INCLUDE_SERVLET_PATH, _servletPath);
		}

		doDispatch(request, response);
	}

	public void doDispatch(
			ServletRequest request, ServletResponse response)
		throws IOException, ServletException {

		if (_servletConfig instanceof BundleServletConfig) {
			BundleServletConfig bundleServletConfig =
				(BundleServletConfig)_servletConfig;

			HttpContext httpContext = bundleServletConfig.getHttpContext();

			if (!httpContext.handleSecurity(
					(HttpServletRequest)request,
					(HttpServletResponse)response)) {

				return;
			}
		}

		_bundleFilterChain.doFilter(request, response);
	}

	private ServletConfig _servletConfig;
	private ServletContext _servletContext;
	private String _contextPath;
	private boolean _extensionMapping;
	private BundleFilterChain _bundleFilterChain;
	private String _pathInfo;
	private String _queryString;
	private String _requestURI;
	private String _servletPath;
	private String _servletMapping;

	public class BundleServletRequest extends HttpServletRequestWrapper {

		public BundleServletRequest(
			HttpServletRequest request, ServletContext servletContext) {

			super(request);

			_servletContext = servletContext;
		}

		@Override
		public Object getAttribute(String name) {
			if (name.equals(INCLUDE_CONTEXT_PATH) ||
				name.equals(INCLUDE_PATH_INFO) ||
				name.equals(INCLUDE_QUERY_STRING) ||
				name.equals(INCLUDE_REQUEST_URI) ||
				name.equals(INCLUDE_SERVLET_PATH)) {

				return _attributes.get(name);
			}

			return super.getAttribute(name);
		}

		@Override
		public Enumeration getAttributeNames() {
			List<String> attributeNames = new Vector<String>();

			Enumeration<String> enu = super.getAttributeNames();

			while (enu.hasMoreElements()) {
				String name = enu.nextElement();
			}

			attributeNames.addAll(_attributes.keySet());

			return Collections.enumeration(attributeNames);
		}

		@Override
		public String getContextPath() {
			return _servletContext.getContextPath();
		}

		@Override
		public String getPathInfo() {
			return _pathInfo;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(path);

			if (requestDispatcher != null) {
				return requestDispatcher;
			}

			return super.getRequestDispatcher(path);
		}

		@Override
		public String getServletPath() {
			return _servletPath;
		}

		@Override
		public void setAttribute(String name, Object value) {
			if (name.equals(INCLUDE_CONTEXT_PATH) ||
				name.equals(INCLUDE_PATH_INFO) ||
				name.equals(INCLUDE_QUERY_STRING) ||
				name.equals(INCLUDE_REQUEST_URI) ||
				name.equals(INCLUDE_SERVLET_PATH)) {

				_attributes.put(name, value);
			}
			else {
				super.setAttribute(name, value);
			}
		}

		private Map<String, Object> _attributes = new HashMap<String, Object>();
		private ServletContext _servletContext;

	}

}
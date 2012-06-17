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

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
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
		Servlet servlet, FilterChain filterChain) {

		_servletMapping = servletMapping;
		_extensionMapping = extensionMapping;
		_servlet = servlet;
		_filterChain = filterChain;

		ServletContext servletContext =
			_servlet.getServletConfig().getServletContext();

		String contextPath = servletContext.getContextPath();

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

		doDispatch(request, response, false);
	}

	public void include(ServletRequest request, ServletResponse response)
		throws IOException, ServletException {

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

		doDispatch(request, response, true);
	}

	public void doDispatch(
			ServletRequest request, ServletResponse response, boolean include)
		throws IOException, ServletException {

		BundleServletConfig bundleServletConfig =
			(BundleServletConfig)_servlet.getServletConfig();

		HttpContext httpContext = bundleServletConfig.getHttpContext();

		if (!httpContext.handleSecurity(
				(HttpServletRequest)request, (HttpServletResponse)response)) {

			return;
		}

		request = new BundleServletRequest(
			(HttpServletRequest)request,
			bundleServletConfig.getServletContext());

		_filterChain.doFilter(request, response);

		_servlet.service(request, response);
	}

	private String _contextPath;
	private boolean _extensionMapping;
	private FilterChain _filterChain;
	private String _pathInfo;
	private String _queryString;
	private String _requestURI;
	private String _servletPath;
	private Servlet _servlet;
	private String _servletMapping;

	public class BundleServletRequest extends HttpServletRequestWrapper {

		public BundleServletRequest(
			HttpServletRequest request, ServletContext servletContext) {

			super(request);

			_servletContext = servletContext;
		}

		@Override
		public String getContextPath() {
			return _servletContext.getContextPath();
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
		public String getPathInfo() {
			return _pathInfo;
		}

		private ServletContext _servletContext;

	}

}
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

package com.liferay.osgi.http.internal.servlet;

import com.liferay.osgi.http.internal.context.ServletContextImpl;
import com.liferay.osgi.http.internal.holder.FilterHolder;

import java.io.IOException;

import java.util.List;

import javax.servlet.Filter;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class RequestDispatcherImpl implements RequestDispatcher {

	public RequestDispatcherImpl(
		ServletContextImpl servletContextImpl, String requestURI,
		String pathInfo, String queryString, String name, Servlet servlet) {

		_servletContextImpl = servletContextImpl;
		_requestURI = requestURI;
		_pathInfo = pathInfo;
		_queryString = queryString;
		_name = name;
		_servlet = servlet;

		_bundleFilterChain = buildFilterChain();
	}

	@Override
	public void forward(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		HttpServletRequestImpl bundleServletRequest = new HttpServletRequestImpl(
			this, (HttpServletRequest)servletRequest);

		doDispatch(bundleServletRequest, servletResponse);
	}

	@Override
	public void include(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		HttpServletRequestImpl bundleServletRequest = new HttpServletRequestImpl(
			this, (HttpServletRequest)servletRequest);

		String contextPath = _servletContextImpl.getContextPath();

		if (_requestURI != null) {
			bundleServletRequest.setAttribute(
				RequestDispatcher.INCLUDE_REQUEST_URI,
				_servletContextImpl.getContextPath().concat(_requestURI));
		}

		if (contextPath != null) {
			bundleServletRequest.setAttribute(
				RequestDispatcher.FORWARD_CONTEXT_PATH, contextPath);
		}

		if (_servletPath != null) {
			bundleServletRequest.setAttribute(
				RequestDispatcher.FORWARD_SERVLET_PATH, _servletPath);
		}

		if (_queryString != null) {
			bundleServletRequest.setAttribute(
				RequestDispatcher.INCLUDE_QUERY_STRING, _queryString);
		}

		if (_pathInfo != null) {
			bundleServletRequest.setAttribute(
				RequestDispatcher.INCLUDE_PATH_INFO, _pathInfo);
		}

		doDispatch(bundleServletRequest, servletResponse);
	}

	protected ServletContextImpl getHttpServletContext() {
		return _servletContextImpl;
	}

	protected String getPathInfo() {
		return _pathInfo;
	}

	protected String getRequestURI() {
		return _requestURI;
	}

	protected String getServletPath() {
		return _servletPath;
	}

	private FilterChainImpl buildFilterChain() {
		FilterChainImpl bundleFilterChain = new FilterChainImpl();

		Filter filter;

		for (FilterHolder holder : _servletContextImpl.getFilters()) {
			if ((filter = holder.match(_pathInfo, _name)) != null) {
				bundleFilterChain.addFilter(filter);
			}
		}

		bundleFilterChain.setServlet(_servlet);

		return bundleFilterChain;
	}

	private void doDispatch(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		if (_bundleFilterChain.getServlet() == null) {
			HttpServletResponse response = (HttpServletResponse)servletResponse;

			response.sendError(
				HttpServletResponse.SC_NOT_FOUND, _requestURI);

			return;
		}

		List<ServletRequestListener> servletRequestListeners =
			_servletContextImpl.getServletRequestListeners();

		executePreListeners(servletRequest, servletRequestListeners);

		_bundleFilterChain.doFilter(servletRequest, servletResponse);

		executePostListeners(servletRequest, servletRequestListeners);
	}

	private void executePostListeners(
		ServletRequest servletRequest,
		List<ServletRequestListener> servletRequestListeners) {

		for (ServletRequestListener servletRequestListener :
				servletRequestListeners) {

			ServletRequestEvent servletRequestEvent =
				new ServletRequestEvent(
					_servletContextImpl, servletRequest);

			servletRequestListener.requestDestroyed(servletRequestEvent);
		}
	}

	private void executePreListeners(
		ServletRequest servletRequest,
		List<ServletRequestListener> servletRequestListeners) {

		for (ServletRequestListener servletRequestListener :
				servletRequestListeners) {

			ServletRequestEvent servletRequestEvent =
				new ServletRequestEvent(
					_servletContextImpl, servletRequest);

			servletRequestListener.requestInitialized(servletRequestEvent);
		}
	}

	private FilterChainImpl _bundleFilterChain;
	private final ServletContextImpl _servletContextImpl;
	private final String _name;
	private String _pathInfo;
	private final String _queryString;
	private final String _requestURI;
	private final Servlet _servlet;
	private String _servletPath;

}
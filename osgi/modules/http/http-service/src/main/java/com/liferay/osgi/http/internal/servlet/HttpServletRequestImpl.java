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

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class HttpServletRequestImpl extends HttpServletRequestWrapper {

	public HttpServletRequestImpl(
		RequestDispatcherImpl requestDispatcherImpl,
		HttpServletRequest request) {

		super(request);

		_requestDispatcherImpl = requestDispatcherImpl;

		_servletContextImpl =
			_requestDispatcherImpl.getHttpServletContext();

		_session = new HttpSessionImpl(
			_servletContextImpl, request.getSession());
	}

	@Override
	public Object getAttribute(String name) {
		if (_maskedAttributes.contains(name)) {
			return _attributes.get(name);
		}

		return super.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		Set<String> attributeNames = new HashSet<String>();

		Enumeration<String> enumeration = super.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			attributeNames.add(name);
		}

		attributeNames.addAll(_attributes.keySet());

		return Collections.enumeration(attributeNames);
	}

	@Override
	public String getContextPath() {
		return _servletContextImpl.getContextPath();
	}

	@Override
	public String getPathInfo() {
		return _requestDispatcherImpl.getPathInfo();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher requestDispatcher =
			_servletContextImpl.getRequestDispatcher(path);

		if (requestDispatcher != null) {
			return requestDispatcher;
		}

		return super.getRequestDispatcher(path);
	}

	@Override
	public String getRequestURI() {
		String contextPath = getContextPath();

		return contextPath.concat(_requestDispatcherImpl.getRequestURI());
	}

	@Override
	public String getServletPath() {
		return _requestDispatcherImpl.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		return _session;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return _session;
	}

	@Override
	public void removeAttribute(String name) {
		Object oldValue = null;

		if (_maskedAttributes.contains(name)) {
			oldValue = _attributes.remove(name);
		}
		else {
			oldValue = super.getAttribute(name);

			super.removeAttribute(name);
		}

		List<ServletRequestAttributeListener> servletRequestAttributeListeners =
			_servletContextImpl.getServletRequestAttributeListeners();

		for (ServletRequestAttributeListener servletRequestAttributeListener :
				servletRequestAttributeListeners) {

			ServletRequestAttributeEvent servletRequestAttributeEvent =
				new ServletRequestAttributeEvent(
					_servletContextImpl, this, name, oldValue);

			servletRequestAttributeListener.attributeReplaced(
				servletRequestAttributeEvent);
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		Object oldValue = null;

		if (_maskedAttributes.contains(name)) {
			oldValue = _attributes.put(name, value);
		}
		else {
			oldValue = super.getAttribute(name);

			super.setAttribute(name, value);
		}

		List<ServletRequestAttributeListener> servletRequestAttributeListeners =
			_servletContextImpl.getServletRequestAttributeListeners();

		for (ServletRequestAttributeListener servletRequestAttributeListener :
				servletRequestAttributeListeners) {

			ServletRequestAttributeEvent servletRequestAttributeEvent =
				new ServletRequestAttributeEvent(
					_servletContextImpl, this, name, oldValue);

			if (oldValue != null) {
				servletRequestAttributeListener.attributeReplaced(
					servletRequestAttributeEvent);
			}
			else {
				servletRequestAttributeListener.attributeAdded(
					servletRequestAttributeEvent);
			}
		}
	}

	private static Set<String> _maskedAttributes = new HashSet<String>(
		Arrays.asList(
			new String[] {
				RequestDispatcher.ERROR_REQUEST_URI,
				RequestDispatcher.FORWARD_CONTEXT_PATH,
				RequestDispatcher.FORWARD_SERVLET_PATH,
				RequestDispatcher.INCLUDE_PATH_INFO,
				RequestDispatcher.INCLUDE_QUERY_STRING
			}
		));

	private Map<String, Object> _attributes = new HashMap<String, Object>();
	private RequestDispatcherImpl _requestDispatcherImpl;
	private ServletContextImpl _servletContextImpl;
	private HttpSession _session;

}
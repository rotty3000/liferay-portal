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

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class HttpSessionImpl implements HttpSession {

	public HttpSessionImpl(ServletContext servletContext, HttpSession session) {
		_session = session;

		_servletContext = servletContext;
	}

	@Override
	public Object getAttribute(String attribute) {
		return _session.getAttribute(attribute);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return _session.getAttributeNames();
	}
	@Override
	public long getCreationTime() {
		return _session.getCreationTime();
	}

	@Override
	public String getId() {
		return _session.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return _session.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		return _session.getMaxInactiveInterval();
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return _session.getSessionContext();
	}

	@Override
	public Object getValue(String key) {
		return _session.getValue(key);
	}

	@Override
	public String[] getValueNames() {
		return _session.getValueNames();
	}

	@Override
	public void invalidate() {
		_session.invalidate();
	}

	@Override
	public boolean isNew() {
		return _session.isNew();
	}

	@Override
	public void putValue(String key, Object value) {
		_session.putValue(key, value);
	}

	@Override
	public void removeAttribute(String attribute) {
		_session.removeAttribute(attribute);
	}

	@Override
	public void removeValue(String key) {
		_session.removeValue(key);
	}

	@Override
	public void setAttribute(String attribute, Object value) {
		_session.setAttribute(attribute, value);
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		_session.setMaxInactiveInterval(interval);
	}

	private ServletContext _servletContext;

	private HttpSession _session;

}
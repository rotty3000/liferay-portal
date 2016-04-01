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

package com.liferay.portal.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Raymond Augé
 */
public class DirectRequestDispatcherRequest extends HttpServletRequestWrapper {

	public DirectRequestDispatcherRequest(
		HttpServletRequest request, String servletPath, String pathInfo) {

		super(request);

		_servletPath = servletPath;
		_pathInfo = pathInfo;
	}

	@Override
	public Object getAttribute(String name) {
		if (RequestDispatcher.INCLUDE_PATH_INFO.equals(name)) {
			return _pathInfo;
		}
		else if (RequestDispatcher.INCLUDE_SERVLET_PATH.equals(name)) {
			return _servletPath;
		}

		return super.getAttribute(name);
	}

	@Override
	public void removeAttribute(String name) {
		if (RequestDispatcher.INCLUDE_PATH_INFO.equals(name)) {
			_pathInfo = null;

			return;
		}
		else if (RequestDispatcher.INCLUDE_SERVLET_PATH.equals(name)) {
			_servletPath = null;

			return;
		}

		super.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (RequestDispatcher.INCLUDE_PATH_INFO.equals(name)) {
			_pathInfo = (String)value;

			return;
		}
		else if (RequestDispatcher.INCLUDE_SERVLET_PATH.equals(name)) {
			_servletPath = (String)value;

			return;
		}

		super.setAttribute(name, value);
	}

	private String _pathInfo;
	private String _servletPath;

}
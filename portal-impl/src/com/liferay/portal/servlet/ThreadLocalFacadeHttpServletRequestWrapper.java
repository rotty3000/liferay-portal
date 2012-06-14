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

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;

import java.io.Closeable;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Shuyang Zhou
 */
public class ThreadLocalFacadeHttpServletRequestWrapper
	extends HttpServletRequestWrapper implements Closeable {

	public ThreadLocalFacadeHttpServletRequestWrapper(
		ServletRequestWrapper servletRequestWrapper,
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);

		_servletRequestWrapper = servletRequestWrapper;

		_nextHttpServletRequestThreadLocal.set(httpServletRequest);
	}

	public void close() {
		if (_servletRequestWrapper != null) {
			HttpServletRequest nextHttpServletRequest =
				_nextHttpServletRequestThreadLocal.get();

			_servletRequestWrapper.setRequest(nextHttpServletRequest);
		}
	}

	@Override
	public Object getAttribute(String name) {
		ServletRequest servletRequest = getRequest();

		return servletRequest.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		ServletRequest servletRequest = getRequest();

		return servletRequest.getAttributeNames();
	}

	@Override
	public ServletRequest getRequest() {
		return _nextHttpServletRequestThreadLocal.get();
	}

	@Override
	public void removeAttribute(String name) {
		ServletRequest servletRequest = getRequest();

		servletRequest.removeAttribute(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		ServletRequest servletRequest = getRequest();

		servletRequest.setAttribute(name, o);
	}

	@Override
	public void setRequest(ServletRequest request) {
		_nextHttpServletRequestThreadLocal.set((HttpServletRequest)request);
	}

	private static ThreadLocal<HttpServletRequest>
		_nextHttpServletRequestThreadLocal =
			new AutoResetThreadLocal<HttpServletRequest>(
				ThreadLocalFacadeHttpServletRequestWrapper.class +
					"._nextHttpServletRequestThreadLocal") {

				@Override
				protected HttpServletRequest copy(
					HttpServletRequest httpServletRequest) {

					return httpServletRequest;
				}

			};

	private ServletRequestWrapper _servletRequestWrapper;

}
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

import java.io.IOException;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.ServletContextHelper;

/**
 * @author Raymond Augé
 */
public class FilterChainImpl implements FilterChain {

	public void addFilter(Filter filter) {
		_filters.add(filter);
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		Filter filter = _filters.poll();

		if (filter == null) {
			ServletConfig servletConfig = _servlet.getServletConfig();

			if (servletConfig instanceof ServletConfigImpl) {
				ServletContextHelper servletContextHelper =
					getServletContextHelper(servletConfig);

				if (!servletContextHelper.handleSecurity(
						(HttpServletRequest)servletRequest,
						(HttpServletResponse)servletResponse)) {

					return;
				}
			}

			_servlet.service(servletRequest, servletResponse);

			HttpServletResponse httpServletResponse =
				(HttpServletResponse)servletResponse;

			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		}
		else {
			filter.doFilter(servletRequest, servletResponse, this);
		}
	}

	public Servlet getServlet() {
		return _servlet;
	}

	public void setServlet(Servlet servlet) {
		_servlet = servlet;
	}

	private ServletContextHelper getServletContextHelper(
		ServletConfig servletConfig) {

		ServletConfigImpl httpServletConfig = (ServletConfigImpl)servletConfig;

		return httpServletConfig.getServletContext().getServletContextHelper();
	}

	private final Queue<Filter> _filters = new ArrayDeque<Filter>();
	private Servlet _servlet;

}
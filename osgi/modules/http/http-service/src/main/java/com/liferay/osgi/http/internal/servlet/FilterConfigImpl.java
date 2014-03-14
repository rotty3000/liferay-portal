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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.FilterConfig;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class FilterConfigImpl implements FilterConfig {

	public FilterConfigImpl(
		ServletContextImpl servletContextImpl, String filterName,
		Map<String, String> initParameters) {

		_servletContextImpl = servletContextImpl;
		_filterName = filterName;
		_initParameters = initParameters;

		if (_initParameters == null) {
			_initParameters = new Hashtable<String, String>();
		}
	}

	@Override
	public String getFilterName() {
		return _filterName;
	}

	@Override
	public String getInitParameter(String parameter) {
		return _initParameters.get(parameter);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_initParameters.keySet());
	}

	@Override
	public ServletContextImpl getServletContext() {
		return _servletContextImpl;
	}

	private String _filterName;
	private Map<String, String> _initParameters;
	private ServletContextImpl _servletContextImpl;

}
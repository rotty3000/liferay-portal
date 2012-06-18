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

import java.io.IOException;

import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Raymond Augé
 */
public class BundleFilterChain implements FilterChain {

	public void addFilter(Filter filter) {
		_filters.add(filter);
	}

	public void doFilter(ServletRequest request, ServletResponse response)
		throws IOException, ServletException {

		Filter currentFilter = _filters.poll();

		if (currentFilter == null) {
			return;
		}

		currentFilter.doFilter(request, response, this);
	}

	private Queue<Filter> _filters = new LinkedList<Filter>();

}
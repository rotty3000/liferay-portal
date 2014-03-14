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

package com.liferay.osgi.http.internal;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Raymond Augé
 */
public class TestServlet extends HttpServlet {

	@Override
	public void destroy() {
		inited.set(false);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		inited.set(true);
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		if (!inited.get()) {
			throw new IllegalStateException();
		}

		PrintWriter writer = response.getWriter();

		writer.print("hello");
	}

	public AtomicBoolean inited = new AtomicBoolean(false);

}
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

package com.liferay.osgi.http.internal.bridge;

import com.liferay.osgi.http.internal.HttpServiceImpl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"bean.id=javax.servlet.http.HttpServlet",
		"original.bean=true"
	},
	service = {
		HttpServlet.class
	}
)
public class BridgeServlet extends HttpServlet {

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY
	)
	public void setHttpServiceImpl(HttpServiceImpl httpServiceImpl) {
		_httpServiceImpl = httpServiceImpl;
	}

	@SuppressWarnings("unused")
	public void unsetHttpServiceImpl(HttpServiceImpl httpServiceImpl) {
		_httpServiceImpl = null;
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		RequestDispatcher requestDispatcher =
			_httpServiceImpl.getRequestDispatcher(requestURI);

		if (requestDispatcher != null) {
			requestDispatcher.forward(request, response);

			return;
		}

		response.sendError(
			HttpServletResponse.SC_NOT_FOUND,
			"No endpoint found for URI " + requestURI);
	}

	private HttpServiceImpl _httpServiceImpl;

}
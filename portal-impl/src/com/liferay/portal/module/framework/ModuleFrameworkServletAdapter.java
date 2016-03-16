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

package com.liferay.portal.module.framework;

import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
public class ModuleFrameworkServletAdapter extends HttpServlet {

	public ModuleFrameworkServletAdapter() {
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(&(bean.id=" + HttpServlet.class.getName() + ")(objectClass=" +
				HttpServlet.class.getName() + ")(original.bean=*))");

		_serviceTracker = registry.trackServices(filter);

		_serviceTracker.open();
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		if (_serviceTracker.isEmpty()) {
			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException("Module framework is unavailable"),
				request, response);

			return;
		}

		HttpServlet httpServlet = _serviceTracker.getService();

		httpServlet.service(request, response);
	}

	private final ServiceTracker<HttpServlet, HttpServlet> _serviceTracker;

}
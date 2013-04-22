/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.registry.Filter;
import com.liferay.portal.service.registry.ServiceRegistryUtil;
import com.liferay.portal.service.registry.ServiceTracker;
import com.liferay.portal.util.PortalUtil;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 * @see    {@link ModuleFrameworkClassloader}
 */
public class ModuleFrameworkServletAdapter extends HttpServlet {

	@Override
	public void destroy() {
		if (_serviceTracker == null) {
			return;
		}

		_serviceTracker.close();
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		try {
			Filter filter = ServiceRegistryUtil.getFilter(
				"(&(objectClass=" + HttpServlet.class.getName() +
					")(original.bean=*))");

			_serviceTracker = ServiceRegistryUtil.trackServices(filter);

			_serviceTracker.open();
		}
		catch (Exception ise) {
			_log.error(ise, ise);
		}
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		if (_serviceTracker == null) {
			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException(
					"Module framework is unavailable"),
				request, response);

			return;
		}

		HttpServlet httpServlet = _serviceTracker.getService();

		if (httpServlet == null) {
			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException(
					"Module framework HTTP service is unavailable"),
				request, response);

			return;
		}

		httpServlet.service(request, response);
	}

	private static Log _log = LogFactoryUtil.getLog(
		ModuleFrameworkServletAdapter.class);

	private ServiceTracker<HttpServlet, HttpServlet> _serviceTracker;

}
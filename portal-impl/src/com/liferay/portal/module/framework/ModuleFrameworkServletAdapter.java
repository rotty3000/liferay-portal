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

import com.liferay.portal.util.PortalUtil;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Miguel Pastor
 * @author Raymond Augé
 */
public class ModuleFrameworkServletAdapter extends HttpServlet {

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(&(bean.id=" + HttpServlet.class.getName() +
				")(original.bean=*))");

		_serviceTracker = registry.trackServices(
			filter, new HttpServletServiceTrackerCustomizer(servletConfig));

		_serviceTracker.open();
	}

	@Override
	public void destroy() {
		super.destroy();

		_serviceTracker.close();
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		HttpServlet httpServlet = _serviceTracker.getService();

		if (httpServlet == null) {
			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException("Module framework is unavailable"),
				request, response);

			return;
		}

		httpServlet.service(request, response);
	}

	private ServiceTracker<HttpServlet, HttpServlet> _serviceTracker;

	private class HttpServletServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<HttpServlet, HttpServlet> {

		public HttpServletServiceTrackerCustomizer(
			ServletConfig servletConfig) {

			_servletConfig = servletConfig;
		}

		@Override
		public HttpServlet addingService(
			ServiceReference<HttpServlet> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			HttpServlet httpServlet = registry.getService(serviceReference);

			try {
				httpServlet.init(_servletConfig);
			}
			catch (ServletException e) {
				e.printStackTrace();

				return null;
			}

			return httpServlet;
		}

		@Override
		public void modifiedService(
			ServiceReference<HttpServlet> serviceReference,
			HttpServlet httpServlet) {
		}

		@Override
		public void removedService(
			ServiceReference<HttpServlet> serviceReference,
			HttpServlet httpServlet) {

			httpServlet.destroy();
		}

		private ServletConfig _servletConfig;
	}

}
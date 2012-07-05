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

package com.liferay.web.extender.internal.servlet;

import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.web.extender.internal.Activator;
import com.liferay.web.extender.internal.http.ExtendedHttpService;
import com.liferay.web.extender.internal.http.HttpServiceFactory;

import java.io.IOException;

import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

/**
 * @author Raymond Augé
 */
public class WebExtenderServlet extends PortletServlet
	implements ModuleFrameworkConstants, StrutsAction {

	public static final String NAME = "Web Extender Servlet";

	public WebExtenderServlet() {
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		Hashtable<String,Object> properties = new Hashtable<String, Object>();

		properties.put(BEAN_ID, HttpService.class.getName());
		properties.put(ORIGINAL_BEAN, Boolean.TRUE);
		properties.put(SERVICE_VENDOR, ReleaseInfo.getVendor());

		HttpServiceFactory httpServiceFactory = new HttpServiceFactory(this);

		_serviceRegistration = Activator.getBundleContext().registerService(
			new String[] {
				HttpService.class.getName(),
				ExtendedHttpService.class.getName()},
			httpServiceFactory, properties);
	}

	@Override
	public void destroy() {
		_serviceRegistration.unregister();

		super.destroy();
	}

	public String execute(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		service(request, response);

		return null;
	}

	public String execute(
			StrutsAction originalStrutsAction, HttpServletRequest request,
			HttpServletResponse response)
		throws Exception {

		service(request, response);

		return null;
	}

	@Override
	public void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		String portletId = (String)request.getAttribute(WebKeys.PORTLET_ID);
		String requestURI = request.getRequestURI();

		Portlet portlet = null;

		if (Validator.isNotNull(portletId)) {
			try {
				String rootPortletId = PortletConstants.getRootPortletId(portletId);

				portlet = PortletLocalServiceUtil.getPortletById(rootPortletId);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		String servletContextName = null;

		if (portlet != null) {
			servletContextName =
				portlet.getPortletApp().getServletContextName();
		}
		else {
			if (requestURI != null) {
				String pathMain = PortalUtil.getPathMain();

				if (requestURI.startsWith(pathMain)) {
					requestURI = requestURI.substring(pathMain.length());
				}

				if (requestURI.startsWith(MODULE_MAPPING)) {
					requestURI = requestURI.substring(MODULE_MAPPING.length());
				}

				servletContextName = requestURI;

				if (servletContextName.startsWith(StringPool.SLASH)) {
					servletContextName = servletContextName.substring(1);
				}

				int pos = servletContextName.indexOf(StringPool.SLASH);

				if (pos != -1) {
					requestURI = servletContextName.substring(
						pos, servletContextName.length());

					servletContextName = servletContextName.substring(0, pos);
				}
			}
		}

		ServletContext servletContext = ServletContextPool.get(
			servletContextName);

		if (servletContext == null) {
			PortalUtil.sendError(
				HttpServletResponse.SC_NOT_FOUND,
				new IllegalArgumentException(
					"No application mapped to this path"), request, response);

			return;
		}

		service(request, response, servletContext, portletId, requestURI);
	}

	protected void service(
			HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext, String portletId, String pathInfo)
		throws IOException, ServletException {

		BundleServletContext bundleServletContext =
			(BundleServletContext)servletContext;

		Thread currentThread = Thread.currentThread();

		ClassLoader bundleClassLoader = bundleServletContext.getClassLoader();
		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			currentThread.setContextClassLoader(bundleClassLoader);

			if (pathInfo.endsWith(INVOKER_PATH)) {
				if (Validator.isNotNull(portletId)) {
					super.service(request, response);

					return;
				}

				PortalUtil.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					new IllegalAccessException("Illegal request"), request,
					response);

				return;
			}

			if (Validator.isNotNull(portletId) &&
				pathInfo.equals(INVOKER_PATH)) {

				super.service(request, response);

				return;
			}

			RequestDispatcher requestDispatcher =
				bundleServletContext.getRequestDispatcher(pathInfo);

			if (requestDispatcher != null) {
				requestDispatcher.forward(request, response);

				return;
			}

			PortalUtil.sendError(
				HttpServletResponse.SC_NOT_FOUND,
				new IllegalArgumentException(
					"No servlet or resource mapped to this path: " + pathInfo),
					request, response);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	private ServiceRegistration<?> _serviceRegistration;

}
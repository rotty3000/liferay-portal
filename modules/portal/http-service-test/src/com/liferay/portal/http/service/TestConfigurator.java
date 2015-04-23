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

package com.liferay.portal.http.service;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true,
	service = TestConfigurator.class
)
public class TestConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
			"http-bug-test");
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
			"/http-bug-test");

		_servletContextHelperServiceRegistration =
			bundleContext.registerService(
				ServletContextHelper.class,
				new ServletContextHelper(bundleContext.getBundle()) {},
				properties);

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
			"http-bug-test");
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
			"TestServlet");
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");

		_servletServiceRegistration = bundleContext.registerService(
			Servlet.class, new HttpServlet() {
				@Override
				protected void doGet(
					HttpServletRequest req, HttpServletResponse resp)
					throws ServletException, IOException {

					resp.setStatus(422);

					PrintWriter printWriter = new PrintWriter(
						resp.getOutputStream());

					printWriter.println("{");
					printWriter.println("error: 'An error message',");
					printWriter.println("code: 'An error code'");
					printWriter.println("}");

					printWriter.flush();
				}
			}, properties);
	}

	@Deactivate
	public void deactivate() {
		_servletServiceRegistration.unregister();

		_servletContextHelperServiceRegistration.unregister();
	}

	private ServiceRegistration<ServletContextHelper>
		_servletContextHelperServiceRegistration;

	private ServiceRegistration<Servlet> _servletServiceRegistration;
}

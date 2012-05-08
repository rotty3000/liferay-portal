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

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.BasePortalLifecycle;
import com.liferay.portal.kernel.util.InstanceFactory;

import java.io.IOException;

import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class SecureServlet
	extends BasePortalLifecycle implements ServletConfig, Servlet {

	public void destroy() {
		portalDestroy();
	}

	public String getInitParameter(String name) {
		return _servletConfig.getInitParameter(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return _servletConfig.getInitParameterNames();
	}

	public ServletConfig getServletConfig() {
		return _servletConfig;
	}

	public ServletContext getServletContext() {
		return _servletConfig.getServletContext();
	}

	public String getServletInfo() {
		return _servlet.getServletInfo();
	}

	public String getServletName() {
		return _servletConfig.getServletName();
	}

	public void init(ServletConfig servletConfig) {
		_servletConfig = servletConfig;

		registerPortalLifecycle();
	}

	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		_servlet.service(servletRequest, servletResponse);
	}

	@Override
	protected void doPortalDestroy() {
		_servlet.destroy();
	}

	@Override
	protected void doPortalInit() throws Exception {
		ServletContext servletContext = _servletConfig.getServletContext();

		ClassLoader classLoader = (ClassLoader)servletContext.getAttribute(
			PluginContextListener.PLUGIN_CLASS_LOADER);

		String servletClass = _servletConfig.getInitParameter("servlet-class");

		_servlet = (Servlet)InstanceFactory.newInstance(
			classLoader, servletClass);

		_servlet.init(_servletConfig);
	}

	private Servlet _servlet;
	private ServletConfig _servletConfig;

}
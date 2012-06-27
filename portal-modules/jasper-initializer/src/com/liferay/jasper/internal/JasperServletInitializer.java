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

package com.liferay.jasper.internal;

import com.liferay.web.extender.servlet.ServletInitializer;

import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.jasper.servlet.JspServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 */
public class JasperServletInitializer
	implements BundleActivator, ServletInitializer {

	public Servlet init(Servlet servlet, ServletConfig servletConfig)
		throws ServletException {

		servlet.init(servletConfig);

		return servlet;
	}

	public void start(BundleContext _bundleContext) throws Exception {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put("servletClassName", JspServlet.class.getName());

		_bundleContext.registerService(
			ServletInitializer.class, this, properties);
	}

	public void stop(BundleContext _bundleContext) throws Exception {
		// not needed
	}

}
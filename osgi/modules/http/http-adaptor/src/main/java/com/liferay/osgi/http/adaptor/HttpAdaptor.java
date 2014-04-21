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

package com.liferay.osgi.http.adaptor;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.eclipse.equinox.http.servlet.HttpServiceServlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true
)
public class HttpAdaptor {

	@Activate
	protected void activate(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		final Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put("bean.id", HttpServlet.class.getName());
		properties.put("original.bean", Boolean.TRUE.toString());

		String[] classes = new String[] {
			HttpServiceServlet.class.getName(),
			HttpServlet.class.getName()
		};

		_httpServiceServlet = new HttpServiceServlet();

		ServletConfig servletConfig = new ServletConfig() {

			@Override
			public String getServletName() {
				return "Liferay OSGi Proxy Servlet";
			}

			@Override
			public ServletContext getServletContext() {
				return _servletContext;
			}

			@Override
			public Enumeration<String> getInitParameterNames() {
				return properties.keys();
			}

			@Override
			public String getInitParameter(String name) {
				return String.valueOf(properties.get(name));
			}

		};

		try {
			_httpServiceServlet.init(servletConfig);

			_serviceRegistration = bundleContext.registerService(
				classes, _httpServiceServlet, properties);
		}
		catch (ServletException se) {
			throw new RuntimeException(se);
		}
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		_httpServiceServlet.destroy();
		_httpServiceServlet = null;
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		target ="(&(bean.id=javax.servlet.ServletContext)(original.bean=*))"
	)
	private void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private void unsetServletContext(ServletContext servletContext) {
		_servletContext = null;
	}

	private HttpServiceServlet _httpServiceServlet;
	private ServletContext _servletContext;
	private ServiceRegistration<?> _serviceRegistration;

}
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

import com.liferay.osgi.http.engine.HttpEngine;

import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true
)
public class BridgeHttpEngine implements HttpEngine {

	@Override
	public String[] getHttpServiceEndpoints() {
		return new String[] {_servletContext.getContextPath()};
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		target ="(&(bean.id=javax.servlet.ServletContext)(original.bean=*))"
	)
	public void setSerlvetContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	public void unsetSerlvetContext(ServletContext servletContext) {
		_servletContext = null;
	}

	private ServletContext _servletContext;

}
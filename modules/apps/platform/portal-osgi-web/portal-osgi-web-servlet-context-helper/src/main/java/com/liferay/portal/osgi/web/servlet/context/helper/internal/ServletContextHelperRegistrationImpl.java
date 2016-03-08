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

package com.liferay.portal.osgi.web.servlet.context.helper.internal;

import com.liferay.portal.osgi.web.servlet.context.helper.ServletContextHelperRegistration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Raymond Augé
 */
public class ServletContextHelperRegistrationImpl
	implements ServletContextHelperRegistration {

	public ServletContextHelperRegistrationImpl(
		ServiceRegistration<ServletContextHelper>
			servletContextHelperRegistration,
		ServiceRegistration<ServletContextListener>
			servletContextListenerRegistration,
		CustomServletContextHelper customServletContextHelper) {

		_servletContextHelperRegistration = servletContextHelperRegistration;
		_servletContextListenerRegistration =
			servletContextListenerRegistration;
		_customServletContextHelper = customServletContextHelper;
	}

	@Override
	public ServletContext getServletContext() {
		return _customServletContextHelper.getServletContext();
	}

	@Override
	public ServiceReference<ServletContextHelper>
		getServletContextHelperReference() {

		return _servletContextHelperRegistration.getReference();
	}

	@Override
	public ServiceReference<ServletContextListener>
		getServletContextListenerReference() {

		return _servletContextListenerRegistration.getReference();
	}

	@Override
	public ServiceRegistration<ServletContextHelper>
		getServletContextHelperRegistration() {

		return _servletContextHelperRegistration;
	}

	@Override
	public ServiceRegistration<ServletContextListener>
		getServletContextListenerRegistration() {

		return _servletContextListenerRegistration;
	}

	private final CustomServletContextHelper _customServletContextHelper;
	private final ServiceRegistration<ServletContextHelper>
		_servletContextHelperRegistration;
	private final ServiceRegistration<ServletContextListener>
		_servletContextListenerRegistration;

}
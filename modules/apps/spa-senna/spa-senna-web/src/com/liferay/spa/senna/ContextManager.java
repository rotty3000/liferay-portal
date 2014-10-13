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
package com.liferay.spa.senna;

import java.net.URL;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true
)
public class ContextManager {

	@Activate
	protected void activate(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "senna");
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/senna");

		_serviceRegistration = bundleContext.registerService(
			ServletContextHelper.class, new Context(bundleContext.getBundle()),
			properties);

		System.out.println(this + " activated!");
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		System.out.println(this + " deactivated!");
	}

	private ServiceRegistration<ServletContextHelper> _serviceRegistration;

	private class Context  extends ServletContextHelper {

		public Context(Bundle bundle) {
			super(bundle);

			_bundle = bundle;

			Class<?> clazz = getClass();

			_string = clazz.getSimpleName() + '[' + bundle.getBundleId() + ']';
		}

		@Override
		public String getRealPath(String path) {
			URL url = getResource(path);

			if (url == null) {
				return null;
			}

			return url.toExternalForm();
		}

		@Override
		public URL getResource(String name) {
			if ((name == null) || (_bundle == null)) {
				return null;
			}

			if (name.startsWith("/")) {
				name = name.substring(1);
			}

			URL url = _bundle.getEntry(name);

			if (url == null) {
				url = _bundle.getResource(name);
			}

			return url;
		}

		@Override
		public String toString() {
			return _string;
		}

		private Bundle _bundle;
		private String _string;

	}

}
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

package com.liferay.portal.security.auth;

import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceRegistration;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;
import com.liferay.registry.collections.StringServiceRegistrationMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Peter Fellwock
 */
public class ScreenNameGeneratorRegistryUtil {

	public static ScreenNameGenerator getScreenNameGenerator() {
		return _instance._serviceTracker.getService();
	}

	public static ScreenNameGenerator getScreenNameGenerator(String path) {
		return _instance._getScreenNameGenerator(path);
	}

	public static Map<String, ScreenNameGenerator> getScreenNameGenerators() {
		return _instance._getScreenNameGenerators();
	}

	public static void register(String path, ScreenNameGenerator screenNameGenerator) {
		_instance._register(path, screenNameGenerator);
	}

	public static void unregister(String path) {
		_instance._unregister(path);
	}

	private ScreenNameGeneratorRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter("(objectClass=" +
			ScreenNameGenerator.class.getName() + ")");

		_serviceTracker = registry.trackServices(filter,
			new ScreenNameGeneratorServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private ScreenNameGenerator _getScreenNameGenerator(String path) {
		ScreenNameGenerator screenNameGenerator = _generators.get(path);

		if (screenNameGenerator != null) {
			return screenNameGenerator;
		}

		for (Map.Entry<String, ScreenNameGenerator> entry : _generators
				.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, ScreenNameGenerator> _getScreenNameGenerators() {
		return _generators;
	}

	private void _register(String path, ScreenNameGenerator screenNameGenerator) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("path", path);

		ServiceRegistration<ScreenNameGenerator> serviceRegistration = registry
				.registerService(ScreenNameGenerator.class, screenNameGenerator,
					properties);

		_screenNameGeneratorServiceRegistrations.put(path, serviceRegistration);
	}

	private void _unregister(String path) {
		ServiceRegistration<?> serviceRegistration = _screenNameGeneratorServiceRegistrations
			.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _screenNameGeneratorServiceRegistrations
			.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static ScreenNameGeneratorRegistryUtil _instance = new ScreenNameGeneratorRegistryUtil();

	private Map<String, ScreenNameGenerator> _generators = new ConcurrentHashMap<String, ScreenNameGenerator>();
	private StringServiceRegistrationMap<ScreenNameGenerator> _screenNameGeneratorServiceRegistrations = new StringServiceRegistrationMap<ScreenNameGenerator>();
	private ServiceTracker<?, ScreenNameGenerator> _serviceTracker;

	private class ScreenNameGeneratorServiceTrackerCustomizer
			implements ServiceTrackerCustomizer<Object, ScreenNameGenerator> {

		@Override
		public ScreenNameGenerator addingService(
				ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			ScreenNameGenerator screenNameGenerator = (ScreenNameGenerator)service;

			String path = screenNameGenerator.getClass().getName();

			_generators.put(path, screenNameGenerator);

			return screenNameGenerator;
		}

		@Override
		public void modifiedService(ServiceReference<Object> serviceReference,
				ScreenNameGenerator service) {
		}

		@Override
		public void removedService(ServiceReference<Object> serviceReference,
				ScreenNameGenerator service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String path = (String) serviceReference.getProperty("path");
			_generators.remove(path);
		}

	}

}
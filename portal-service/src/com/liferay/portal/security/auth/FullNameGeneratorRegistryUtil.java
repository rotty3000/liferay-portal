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
public class FullNameGeneratorRegistryUtil {

	public static FullNameGenerator getFullNameGenerator() {
		return _instance._serviceTracker.getService();
	}

	public static FullNameGenerator getFullNameGenerator(String path) {
		return _instance._getFullNameGenerator(path);
	}

	public static Map<String, FullNameGenerator> getFullNameGenerators() {
		return _instance._getFullNameGenerators();
	}

	public static void register(String path, FullNameGenerator fullNameGenerator) {
		_instance._register(path, fullNameGenerator);
	}

	public static void unregister(String path) {
		_instance._unregister(path);
	}

	private FullNameGeneratorRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
				FullNameGenerator.class.getName(),
			new FullNameGeneratorServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private FullNameGenerator _getFullNameGenerator(String path) {
		FullNameGenerator fullNameGenerator = _generators.get(path);

		if (fullNameGenerator != null) {
			return fullNameGenerator;
		}

		for (Map.Entry<String, FullNameGenerator> entry : _generators
				.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, FullNameGenerator> _getFullNameGenerators() {
		return _generators;
	}

	private void _register(String path, FullNameGenerator fullNameGenerator) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("path", path);

		ServiceRegistration<FullNameGenerator> serviceRegistration = registry
				.registerService(FullNameGenerator.class, fullNameGenerator,
					properties);

		_fullNameGeneratorServiceRegistrations.put(path, serviceRegistration);
	}

	private void _unregister(String path) {
		ServiceRegistration<?> serviceRegistration = _fullNameGeneratorServiceRegistrations
			.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _fullNameGeneratorServiceRegistrations
			.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static FullNameGeneratorRegistryUtil _instance = new FullNameGeneratorRegistryUtil();

	private StringServiceRegistrationMap<FullNameGenerator> _fullNameGeneratorServiceRegistrations = new StringServiceRegistrationMap<FullNameGenerator>();
	private Map<String, FullNameGenerator> _generators = new ConcurrentHashMap<String, FullNameGenerator>();
	private ServiceTracker<?, FullNameGenerator> _serviceTracker;

	private class FullNameGeneratorServiceTrackerCustomizer
			implements ServiceTrackerCustomizer<Object, FullNameGenerator> {

		@Override
		public FullNameGenerator addingService(
				ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			FullNameGenerator fullNameGenerator = (FullNameGenerator)service;

			String path = fullNameGenerator.getClass().getName();

			_generators.put(path, fullNameGenerator);

			return fullNameGenerator;
		}

		@Override
		public void modifiedService(ServiceReference<Object> serviceReference,
				FullNameGenerator service) {
		}

		@Override
		public void removedService(ServiceReference<Object> serviceReference,
				FullNameGenerator service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String path = (String) serviceReference.getProperty("path");
			_generators.remove(path);
		}

	}

}
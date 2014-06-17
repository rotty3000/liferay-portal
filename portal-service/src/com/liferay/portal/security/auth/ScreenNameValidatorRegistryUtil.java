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
public class ScreenNameValidatorRegistryUtil {

	public static ScreenNameValidator getScreenNameValidator() {
		return _instance._serviceTracker.getService();
	}

	public static ScreenNameValidator getScreenNameValidator(String path) {
		return _instance._getScreenNameValidator(path);
	}

	public static Map<String, ScreenNameValidator> getScreenNameValidators() {
		return _instance._getScreenNameValidators();
	}

	public static void register(String path, ScreenNameValidator screenNameValidator) {
		_instance._register(path, screenNameValidator);
	}

	public static void unregister(String path) {
		_instance._unregister(path);
	}

	private ScreenNameValidatorRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(objectClass=" + ScreenNameValidator.class.getName() + ")");

		_serviceTracker = registry.trackServices(
			filter, new ScreenNameValidatorServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private ScreenNameValidator _getScreenNameValidator(String path) {
		ScreenNameValidator screenNameValidator = _validators.get(path);

		if (screenNameValidator != null) {
			return screenNameValidator;
		}

		for (Map.Entry<String, ScreenNameValidator> entry : _validators.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, ScreenNameValidator> _getScreenNameValidators() {
		return _validators;
	}

	private void _register(String path, ScreenNameValidator screenNameValidator) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("path", path);

		ServiceRegistration<ScreenNameValidator> serviceRegistration =
			registry.registerService(
				ScreenNameValidator.class, screenNameValidator, properties);

		_screenNameValidatorServiceRegistrations.put(path, serviceRegistration);
	}

	private void _unregister(String path) {
		ServiceRegistration<?> serviceRegistration =
			_screenNameValidatorServiceRegistrations.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _screenNameValidatorServiceRegistrations.remove(
			path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static ScreenNameValidatorRegistryUtil _instance =
		new ScreenNameValidatorRegistryUtil();

	private StringServiceRegistrationMap<ScreenNameValidator>
		_screenNameValidatorServiceRegistrations =
			new StringServiceRegistrationMap<ScreenNameValidator>();
	private ServiceTracker<?, ScreenNameValidator> _serviceTracker;
	private Map<String, ScreenNameValidator> _validators =
		new ConcurrentHashMap<String, ScreenNameValidator>();

	private class ScreenNameValidatorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, ScreenNameValidator> {

		@Override
		public ScreenNameValidator addingService(ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			ScreenNameValidator screenNameValidator = (ScreenNameValidator)service;

			String path = screenNameValidator.getClass().getName();

			_validators.put(path, screenNameValidator);

			return screenNameValidator;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, ScreenNameValidator service) {
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, ScreenNameValidator service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String path = (String)serviceReference.getProperty("path");
			_validators.remove(path);
		}

	}

}
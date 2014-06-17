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
public class FullNameValidatorRegistryUtil {

	public static FullNameValidator getFullNameValidator() {
		return _instance._serviceTracker.getService();
	}

	public static FullNameValidator getFullNameValidator(String path) {
		return _instance._getFullNameValidator(path);
	}

	public static Map<String, FullNameValidator> getFullNameValidators() {
		return _instance._getFullNameValidators();
	}

	public static void register(String path, FullNameValidator fullNameValidator) {
		_instance._register(path, fullNameValidator);
	}

	public static void unregister(String path) {
		_instance._unregister(path);
	}

	private FullNameValidatorRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			FullNameValidator.class.getName(),
			new FullNameValidatorServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private FullNameValidator _getFullNameValidator(String path) {
		FullNameValidator fullNameValidator = _validators.get(path);

		if (fullNameValidator != null) {
			return fullNameValidator;
		}

		for (Map.Entry<String, FullNameValidator> entry : _validators.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, FullNameValidator> _getFullNameValidators() {
		return _validators;
	}

	private void _register(String path, FullNameValidator fullNameValidator) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("path", path);

		ServiceRegistration<FullNameValidator> serviceRegistration =
			registry.registerService(
				FullNameValidator.class, fullNameValidator, properties);

		_fullNameValidatorServiceRegistrations.put(path, serviceRegistration);
	}

	private void _unregister(String path) {
		ServiceRegistration<?> serviceRegistration =
			_fullNameValidatorServiceRegistrations.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _fullNameValidatorServiceRegistrations.remove(
			path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static FullNameValidatorRegistryUtil _instance =
		new FullNameValidatorRegistryUtil();

	private StringServiceRegistrationMap<FullNameValidator>
		_fullNameValidatorServiceRegistrations =
			new StringServiceRegistrationMap<FullNameValidator>();
	private ServiceTracker<?, FullNameValidator> _serviceTracker;
	private Map<String, FullNameValidator> _validators =
		new ConcurrentHashMap<String, FullNameValidator>();

	private class FullNameValidatorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, FullNameValidator> {

		@Override
		public FullNameValidator addingService(ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			FullNameValidator fullNameValidator = (FullNameValidator)service;

			String path = fullNameValidator.getClass().getName();

			_validators.put(path, fullNameValidator);

			return fullNameValidator;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, FullNameValidator service) {
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, FullNameValidator service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String path = (String)serviceReference.getProperty("path");
			_validators.remove(path);
		}

	}

}
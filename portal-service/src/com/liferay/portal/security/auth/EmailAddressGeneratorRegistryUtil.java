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
public class EmailAddressGeneratorRegistryUtil {

	public static EmailAddressGenerator getEmailAddressGenerator() {
		return _instance._serviceTracker.getService();
	}

	public static EmailAddressGenerator getEmailAddressGenerator(String path) {
		return _instance._getEmailAddressGenerator(path);
	}

	public static Map<String, EmailAddressGenerator> getEmailAddressGenerators() {
		return _instance._getEmailAddressGenerators();
	}

	public static void register(String path, EmailAddressGenerator emailAddressGenerator) {
		_instance._register(path, emailAddressGenerator);
	}

	public static void unregister(String path) {
		_instance._unregister(path);
	}

	private EmailAddressGeneratorRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			EmailAddressGenerator.class.getName(),
			new EmailAddressGeneratorServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private EmailAddressGenerator _getEmailAddressGenerator(String path) {
		EmailAddressGenerator emailAddressGenerator = _generators.get(path);

		if (emailAddressGenerator != null) {
			return emailAddressGenerator;
		}

		for (Map.Entry<String, EmailAddressGenerator> entry : _generators.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, EmailAddressGenerator> _getEmailAddressGenerators() {
		return _generators;
	}

	private void _register(String path, EmailAddressGenerator emailAddressGenerator) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("path", path);

		ServiceRegistration<EmailAddressGenerator> serviceRegistration =
			registry.registerService(
				EmailAddressGenerator.class, emailAddressGenerator, properties);

		_emailAddressGeneratorServiceRegistrations.put(path, serviceRegistration);
	}

	private void _unregister(String path) {
		ServiceRegistration<?> serviceRegistration =
			_emailAddressGeneratorServiceRegistrations.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _emailAddressGeneratorServiceRegistrations.remove(
			path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static EmailAddressGeneratorRegistryUtil _instance =
		new EmailAddressGeneratorRegistryUtil();

	private StringServiceRegistrationMap<EmailAddressGenerator>
		_emailAddressGeneratorServiceRegistrations =
			new StringServiceRegistrationMap<EmailAddressGenerator>();
	private Map<String, EmailAddressGenerator> _generators =
		new ConcurrentHashMap<String, EmailAddressGenerator>();
	private ServiceTracker<?, EmailAddressGenerator> _serviceTracker;

	private class EmailAddressGeneratorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, EmailAddressGenerator> {

		@Override
		public EmailAddressGenerator addingService(ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			EmailAddressGenerator emailAddressGenerator = (EmailAddressGenerator)service;

			String path = emailAddressGenerator.getClass().getName();

			_generators.put(path, emailAddressGenerator);

			return emailAddressGenerator;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, EmailAddressGenerator service) {
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, EmailAddressGenerator service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String path = (String)serviceReference.getProperty("path");
			_generators.remove(path);
		}

	}

}
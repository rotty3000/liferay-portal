/**
 * opyright (c) 2000-present Liferay, Inc. All rights reserved.
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
public class EmailAddressValidatorRegistryUtil {

	public static EmailAddressValidator getEmailAddressValidator() {
		return _instance._serviceTracker.getService();
	}

	public static EmailAddressValidator getEmailAddressValidator(String path) {
		return _instance._getEmailAddressValidator(path);
	}

	public static Map<String, EmailAddressValidator> getEmailAddressValidators() {
		return _instance._getEmailAddressValidators();
	}

	public static void register(String path, EmailAddressValidator emailAddressValidator) {
		_instance._register(path, emailAddressValidator);
	}

	public static void unregister(String path) {
		_instance._unregister(path);
	}

	private EmailAddressValidatorRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			EmailAddressValidator.class.getName(),
			new EmailAddressValidatorServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private EmailAddressValidator _getEmailAddressValidator(String path) {
		EmailAddressValidator emailAddressValidator = _validators.get(path);

		if (emailAddressValidator != null) {
			return emailAddressValidator;
		}

		for (Map.Entry<String, EmailAddressValidator> entry : _validators.entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, EmailAddressValidator> _getEmailAddressValidators() {
		return _validators;
	}

	private void _register(String path, EmailAddressValidator emailAddressValidator) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("path", path);

		ServiceRegistration<EmailAddressValidator> serviceRegistration =
			registry.registerService(
				EmailAddressValidator.class, emailAddressValidator, properties);

		_emailAddressValidatorServiceRegistrations.put(path, serviceRegistration);
	}

	private void _unregister(String path) {
		ServiceRegistration<?> serviceRegistration =
			_emailAddressValidatorServiceRegistrations.remove(path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _emailAddressValidatorServiceRegistrations.remove(
			path);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static EmailAddressValidatorRegistryUtil _instance =
		new EmailAddressValidatorRegistryUtil();

	private StringServiceRegistrationMap<EmailAddressValidator>
		_emailAddressValidatorServiceRegistrations =
			new StringServiceRegistrationMap<EmailAddressValidator>();
	private ServiceTracker<?, EmailAddressValidator> _serviceTracker;
	private Map<String, EmailAddressValidator> _validators =
		new ConcurrentHashMap<String, EmailAddressValidator>();

	private class EmailAddressValidatorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, EmailAddressValidator> {

		@Override
		public EmailAddressValidator addingService(ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			EmailAddressValidator emailAddressValidator = (EmailAddressValidator)service;

			String path = emailAddressValidator.getClass().getName();

			_validators.put(path, emailAddressValidator);

			return emailAddressValidator;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, EmailAddressValidator service) {
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, EmailAddressValidator service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String path = (String)serviceReference.getProperty("path");
			_validators.remove(path);
		}

	}

}
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
public class AutoLoginRegistryUtil {

	public static AutoLogin getAutoLogin() {
		return _instance._serviceTracker.getService();
	}

	public static AutoLogin getAutoLogin(String classname) {
		if (_instance._getAutoLogin(classname) != null) {
			return _instance._getAutoLogin(classname);
		}

		return _delayGet(classname);
	}

	public static Map<String, AutoLogin> getAutoLogins() {
		return _instance._getAutoLogins();
	}

	public static void register(String classname, AutoLogin autoLogin) {
		_instance._register(classname, autoLogin);
	}

	public static void unregister(String classname) {
		_instance._unregister(classname);
	}

	private static AutoLogin _delayGet(String classname) {
		int count = 0;
		while (count < _delayAttempt) {
			_sleep();

			if (_instance._getAutoLogin(classname) != null) {
				return _instance._getAutoLogin(classname);
			}

			count++;
		}

		return _instance._getAutoLogin(classname);
	}

	private static void _sleep() {
		try {
			Thread.sleep(_delaySleep);
		}catch (Exception e) {}
	}

	private AutoLoginRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(AutoLogin.class.getName(),
			new AutoLoginServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private AutoLogin _getAutoLogin(String classname) {

		AutoLogin autoLogin = _tokens.get(classname);

		if (autoLogin != null) {
			return autoLogin;
		}

		for (Map.Entry<String, AutoLogin> entry : _tokens.entrySet()) {
			if (classname.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}

		return null;
	}

	private Map<String, AutoLogin> _getAutoLogins() {
		return _tokens;
	}

	private void _register(String classname, AutoLogin autoLogin) {
		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("classname", classname);

		ServiceRegistration<AutoLogin> serviceRegistration =
			registry.registerService(AutoLogin.class, autoLogin, properties);

		_autoLoginServiceRegistrations.put(classname, serviceRegistration);
	}

	private void _unregister(String classname) {
		ServiceRegistration<?> serviceRegistration =
			_autoLoginServiceRegistrations.remove(classname);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

		serviceRegistration = _autoLoginServiceRegistrations.remove(classname);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	private static AutoLoginRegistryUtil _instance =
		new AutoLoginRegistryUtil();

	private static int _delayAttempt = 2;
	private static int _delaySleep = 200;

	private StringServiceRegistrationMap<AutoLogin>
		_autoLoginServiceRegistrations =
			new StringServiceRegistrationMap<AutoLogin>();
	private ServiceTracker<?, AutoLogin> _serviceTracker;
	private Map<String, AutoLogin> _tokens =
		new ConcurrentHashMap<String, AutoLogin>();

	private class AutoLoginServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, AutoLogin> {

		@Override
		public AutoLogin addingService(ServiceReference<Object> serviceReference) {
			Registry registry = RegistryUtil.getRegistry();

			Object service = registry.getService(serviceReference);

			AutoLogin autoLogin = (AutoLogin)service;

			String classname = autoLogin.getClass().getName();

			_tokens.put(classname, autoLogin);

			return autoLogin;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, AutoLogin service) {
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, AutoLogin service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			String classname = (String)serviceReference.getProperty("path");

			_tokens.remove(classname);
		}

	}

}
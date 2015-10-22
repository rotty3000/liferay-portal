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

package com.liferay.portal.kernel.app;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Amos Fong
 */
public class AppRegistryUtil {

	public static boolean isVerified(long bundleId) {
		return _instance._isVerified(bundleId);
	}

	private AppRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			AppVerifier.class, new AppVerifierServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private boolean _isVerified(long bundleId) {
		if (_verifiedApps.containsKey(bundleId)) {
			return true;
		}

		if (_unverifiedApps.containsKey(bundleId)) {
			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AppRegistryUtil.class);

	private static final AppRegistryUtil _instance = new AppRegistryUtil();

	private final ServiceTracker<AppVerifier, AppVerifier> _serviceTracker;
	private final Map<Long, AppVerifier> _unverifiedApps =
		new ConcurrentHashMap<>();
	private final Map<Long, AppVerifier> _verifiedApps =
		new ConcurrentHashMap<>();

	private class AppVerifierServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<AppVerifier, AppVerifier> {

		@Override
		public AppVerifier addingService(
			ServiceReference<AppVerifier> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			AppVerifier appVerifier = registry.getService(serviceReference);

			Long bundleId = (Long)serviceReference.getProperty(
				"service.bundleid");

			if (_log.isInfoEnabled()) {
				_log.info("Verifying bundle: " + bundleId);
			}

			if (bundleId != null) {
				try {
					if (appVerifier.verify()) {
						if (_log.isInfoEnabled()) {
							_log.info(
								"Bundle passed verification: " + bundleId);
						}

						appVerifier.init(bundleId);

						_verifiedApps.put(bundleId, appVerifier);
					}
					else {
						if (_log.isInfoEnabled()) {
							_log.info(
								"Bundle failed verification: " + bundleId);
						}

						_unverifiedApps.put(bundleId, appVerifier);
					}
				}
				catch (Exception e) {
					_log.error("Bundle failed verification: " + bundleId, e);

					_unverifiedApps.put(bundleId, appVerifier);
				}
			}

			return appVerifier;
		}

		@Override
		public void modifiedService(
			ServiceReference<AppVerifier> serviceReference,
			AppVerifier appVerifier) {
		}

		@Override
		public void removedService(
			ServiceReference<AppVerifier> serviceReference,
			AppVerifier appVerifier) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			Long bundleId = (Long)serviceReference.getProperty(
				"service.bundleid");

			if (bundleId != null) {
				if (_log.isInfoEnabled()) {
					_log.info("Removing bundle: " + bundleId);
				}

				appVerifier.destroy();

				_verifiedApps.remove(bundleId);
				_unverifiedApps.remove(bundleId);
			}
		}

	}

}
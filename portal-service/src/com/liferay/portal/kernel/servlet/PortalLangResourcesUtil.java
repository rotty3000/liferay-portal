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

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Peter Fellwock
 */
public class PortalLangResourcesUtil {

	public static ResourceBundle getLangResourceBundle(
		ResourceBundle resourceBundle) {

		return _instance._resourceBundleMap.get(resourceBundle.hashCode());
	}

	private PortalLangResourcesUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			PortalLangResources.class,
			new PortalLangResourcesServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private static final PortalLangResourcesUtil _instance =
		new PortalLangResourcesUtil();

	private final
		Map<ServiceReference<PortalLangResources>, PortalLangResources>
			_portalLangResourcesMap = new ConcurrentHashMap<>();
	private final Map<Integer, ResourceBundle>
		_resourceBundleMap = new ConcurrentHashMap<>();
	private final ServiceTracker<PortalLangResources, PortalLangResources>
		_serviceTracker;

	private class PortalLangResourcesServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<PortalLangResources, PortalLangResources> {

		@Override
		public PortalLangResources addingService(
			ServiceReference<PortalLangResources> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			PortalLangResources portalLangResources = registry.getService(
				serviceReference);

			_portalLangResourcesMap.put(serviceReference, portalLangResources);

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", portalLangResources.getClass());

			ResourceBundle langResourceBundle = ResourceBundleUtil.getBundle(
				"content.Language",
				portalLangResources.getLangResourceBundleClass());

			_resourceBundleMap.put(
				resourceBundle.hashCode(), langResourceBundle);

			return portalLangResources;
		}

		@Override
		public void modifiedService(
			ServiceReference<PortalLangResources> serviceReference,
			PortalLangResources portalLangResources) {
		}

		@Override
		public void removedService(
			ServiceReference<PortalLangResources> serviceReference,
			PortalLangResources portalLangResources) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			_portalLangResourcesMap.remove(serviceReference);
		}

	}

}
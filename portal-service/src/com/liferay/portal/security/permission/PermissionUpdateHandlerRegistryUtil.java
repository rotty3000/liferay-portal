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

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;
import com.liferay.registry.util.StringPlus;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Map;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Gergely Mathe
 */
@ProviderType
public class PermissionUpdateHandlerRegistryUtil {

	public static PermissionUpdateHandler getPermissionUpdateHandler(
		String modelClassName) {

		return _instance._getPermissionUpdateHandler(modelClassName);
	}

	public static List<PermissionUpdateHandler> getPermissionUpdateHandlers() {
		return _instance._getPermissionUpdateHandlers();
	}

	private PermissionUpdateHandlerRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(&(model.class.name=*)(objectClass=" + 
				PermissionUpdateHandler.class.getName() + "))");

		_serviceTracker = registry.trackServices(
			filter, new PermissionUpdateHandlerServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private PermissionUpdateHandler _getPermissionUpdateHandler(
		String modelClassName) {

		return _permissionUpdateHandlers.get(modelClassName);
	}

	private List<PermissionUpdateHandler> _getPermissionUpdateHandlers() {
		Collection<PermissionUpdateHandler> values =
			_permissionUpdateHandlers.values();

		return ListUtil.fromCollection(values);
	}

	private static final PermissionUpdateHandlerRegistryUtil _instance =
		new PermissionUpdateHandlerRegistryUtil();

	private final Map<String, PermissionUpdateHandler>
		_permissionUpdateHandlers = new ConcurrentHashMap<>();
	private final ServiceTracker
		<PermissionUpdateHandler, PermissionUpdateHandler> _serviceTracker;

	private class PermissionUpdateHandlerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<PermissionUpdateHandler, PermissionUpdateHandler> {

		@Override
		public PermissionUpdateHandler addingService(
			ServiceReference<PermissionUpdateHandler> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			PermissionUpdateHandler permissionUpdateHandler =
				registry.getService(serviceReference);

			List<String> modelClassNames = StringPlus.asList(
				serviceReference.getProperty("model.class.name"));

			for (String modelClassName : modelClassNames) {
				_permissionUpdateHandlers.put(
					modelClassName, permissionUpdateHandler);
			}

			return permissionUpdateHandler;
		}

		@Override
		public void modifiedService(
			ServiceReference<PermissionUpdateHandler> serviceReference,
			PermissionUpdateHandler permissionUpdateHandler) {
		}

		@Override
		public void removedService(
			ServiceReference<PermissionUpdateHandler> serviceReference,
			PermissionUpdateHandler permissionUpdateHandler) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			List<String> modelClassNames = StringPlus.asList(
				serviceReference.getProperty("model.class.name"));

			for (String modelClassName : modelClassNames) {
				_permissionUpdateHandlers.remove(modelClassName);
			}
		}

	}

}
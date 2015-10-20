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

package com.liferay.portal.portlet.tracker.internal;

import com.liferay.portlet.CustomUserAttributes;
import com.liferay.portlet.PortletContextBag;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Peter Fellwock
 */
public class BundlePortletContextBag extends PortletContextBag {

	public BundlePortletContextBag(String servletContextName) {
		super(servletContextName);

		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(&(custom.user.attribute=*)" +
			"(javax.portlet.name=ALL)" +
			"(objectClass=" + CustomUserAttributes.class.getName() + "))");

		_customUserAttributesList = ServiceTrackerCollections.list(
			CustomUserAttributes.class, filter);
	}

	public Map<String, CustomUserAttributes> getCustomUserAttributes() {
		Map<String, CustomUserAttributes> customUserAttributesMap =
			new HashMap<>();

		for (CustomUserAttributes customUserAttributes :
				_customUserAttributesList) {

			customUserAttributesMap.put(
				customUserAttributes.getClass().getName(),
				customUserAttributes);
		}

		return customUserAttributesMap;
	}

	public Map<String, String> getCustomUserAttributesDefinitions() {
		Map<String, CustomUserAttributes> customUserAttributes =
			getCustomUserAttributes();

		Set<String> classNames = customUserAttributes.keySet();

		Map<String, String> customUserAttributesDefinitions = new HashMap<>();

		for (String className : classNames) {
			customUserAttributesDefinitions.put(className, className);
		}

		return customUserAttributesDefinitions;
	}

	private final ServiceTrackerList<CustomUserAttributes>
		_customUserAttributesList;

}
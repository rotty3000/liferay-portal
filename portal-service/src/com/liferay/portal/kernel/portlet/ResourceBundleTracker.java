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

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.collections.ServiceReferenceMapperFactory;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;

import java.io.Closeable;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Raymond Augé
 * @author Tomas Polesovsky
 */
public class ResourceBundleTracker implements Closeable {

	public ResourceBundleTracker(String portletId) {
		_portletId = portletId;

		_serviceTrackerMap = ServiceTrackerCollections.multiValueMap(
			ResourceBundle.class,
			"(&(javax.portlet.name=" + portletId + ")(language.id=*))",
			ServiceReferenceMapperFactory.<String, Object>create(
				"language.id"));

		_serviceTrackerMap.open();
	}

	@Override
	public void close() {
		_serviceTrackerMap.close();
	}

	public ResourceBundle getResourceBundle(String languageId) {
		//This method could be optimized and done on insertion
		//This should be easily done adding ServiceMapListener
		List<ResourceBundle> resourceBundles = new ArrayList<>();

		while (Validator.isNotNull(languageId)) {
			List<ResourceBundle> service = _serviceTrackerMap.getService(
				languageId);

			if (service != null) {
				resourceBundles.addAll(service);
			}

			int indexOfUnderline = languageId.indexOf(StringPool.UNDERLINE);

			if (indexOfUnderline > 0) {
				languageId = languageId.substring(0, indexOfUnderline);
			}
			else {
				break;
			}
		}

		return new AggregateResourceBundle(
			resourceBundles.toArray(new ResourceBundle[0]));
	}

	private final String _portletId;
	private final ServiceTrackerMap<String, List<ResourceBundle>>
		_serviceTrackerMap;

}
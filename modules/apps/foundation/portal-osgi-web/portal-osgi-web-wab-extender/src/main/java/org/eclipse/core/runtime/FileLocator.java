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

package org.eclipse.core.runtime;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.io.IOException;

import java.net.URL;

import org.eclipse.osgi.service.urlconversion.URLConverter;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * This is a dummy copy of what's needed to make spring annotation processing
 * work in OSGi.
 *
 * @author Raymond Augé
 */
@Component(immediate = true)
public class FileLocator {

	public static URL resolve(URL url) throws IOException {
		String protocol = url.getProtocol();

		URLConverter converter = _instance._serviceTrackerMap.getService(
			protocol);

		return converter == null ? url : converter.resolve(url);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, URLConverter.class, "protocol");

		_instance = this;
	}

	@Deactivate
	protected void deactivate() {
		_instance = null;

		_serviceTrackerMap.close();
	}

	private static FileLocator _instance;

	private ServiceTrackerMap<String,URLConverter> _serviceTrackerMap;

}
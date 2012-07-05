/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.moduleadmin.internal;

import aQute.libg.header.OSGiHeader;
import aQute.libg.version.Version;
import aQute.libg.version.VersionRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;

/**
 * @author Raymond Augé
 */
public class ModuleUtil {

	public BundleStartLevel getBundleStartLevel(Bundle bundle) {
		return bundle.adapt((BundleStartLevel.class));
	}

	public Map<String,Object> getHeaders(Bundle bundle, String languageId) {
		Map<String,Object> headerMap = new HashMap<String,Object>();

		Dictionary<String,String> headers = bundle.getHeaders(languageId);

		Enumeration<String> keys = headers.keys();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Object value = headers.get(key);

			headerMap.put(key, value);
		}

		return headerMap;
	}

	@SuppressWarnings("rawtypes")
	public List<ServiceReference> getRegisteredServices(Bundle bundle) {
		ServiceReference[] serviceReferences =
			bundle.getRegisteredServices();

		if (serviceReferences == null) {
			serviceReferences = new ServiceReference[0];
		}

		return Arrays.asList(serviceReferences);
	}

	@SuppressWarnings("rawtypes")
	public List<ServiceReference> getServicesInUse(Bundle bundle) {
		ServiceReference[] serviceReferences = bundle.getServicesInUse();

		if (serviceReferences == null) {
			serviceReferences = new ServiceReference[0];
		}

		return Arrays.asList(serviceReferences);
	}

	public boolean isPackageSatisfied(
		BundleContext bundleContext, String packageName, String versionString) {

		VersionRange versionRange = new VersionRange("0.0.0");

		if (versionString != null) {
			versionRange = new VersionRange(versionString);
		}

		List<Map<String,Map<String,String>>> packageList = _packages.get();

		if (packageList == null) {
			packageList = new ArrayList<Map<String,Map<String,String>>>();

			for (Bundle bundle : bundleContext.getBundles()) {
				Dictionary<String,String> headers = bundle.getHeaders();

				String exportPackage = headers.get(Constants.EXPORT_PACKAGE);

				Map<String, Map<String, String>> parsedHeader =
					OSGiHeader.parseHeader(exportPackage);

				packageList.add(parsedHeader);
			}

			_packages.set(packageList);
		}

		for (Map<String, Map<String, String>> map : packageList) {
			if (map.containsKey(packageName)) {
				String currentVersionParsed = map.get(
					packageName).get("version");

				Version curVersion = new Version();

				if (currentVersionParsed != null) {
					curVersion = new Version(currentVersionParsed);
				}

				if (versionRange.includes(curVersion)) {
					return true;
				}
			}
		}

		return false;
	}

	private ThreadLocal<List<Map<String, Map<String, String>>>> _packages =
		new ThreadLocal<List<Map<String, Map<String, String>>>>();

}
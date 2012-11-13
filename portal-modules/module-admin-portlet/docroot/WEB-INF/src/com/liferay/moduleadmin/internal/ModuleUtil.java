/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import aQute.libg.version.Version;
import aQute.libg.version.VersionRange;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class ModuleUtil {

	public ModuleUtil(
		ServiceTracker<PackageAdmin, PackageAdmin> packageAdminTracker) {

		_packageAdminTracker = packageAdminTracker;
	}

	public BundleStartLevel getBundleStartLevel(Bundle bundle) {
		return bundle.adapt((BundleStartLevel.class));
	}

	public Map<String, Object> getHeaders(Bundle bundle, String languageId) {
		Map<String, Object> headerMap = new HashMap<String, Object>();

		Dictionary<String, String> headers = bundle.getHeaders(languageId);

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
		ServiceReference[] serviceReferences = bundle.getRegisteredServices();

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

		VersionRange versionRange = new VersionRange("0");

		if (versionString != null) {
			versionRange = new VersionRange(versionString);
		}

		PackageAdmin packageAdmin = getPackageAdmin();

		ExportedPackage[] exportedPackages = packageAdmin.getExportedPackages(
			packageName);

		if (exportedPackages != null) {
			for (ExportedPackage exportedPackage : exportedPackages) {
				org.osgi.framework.Version version =
					exportedPackage.getVersion();

				Version curVersion = new Version(version.toString());

				if (versionRange.includes(curVersion)) {
					return true;
				}
			}
		}

		return false;
	}

	private PackageAdmin getPackageAdmin() {
		try {
			return _packageAdminTracker.waitForService(10000);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();

			return null;
		}
	}

	private ServiceTracker<PackageAdmin, PackageAdmin> _packageAdminTracker;

}
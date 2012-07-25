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

package com.liferay.portal.module.framework;

import java.io.InputStream;

import org.osgi.framework.launch.Framework;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class ModuleFrameworkUtil implements ModuleFrameworkConstants {

	public static Object addBundle(String location) throws PortalException {
		return addBundle(location, null);
	}

	public static Object addBundle(String location, InputStream inputStream)
		throws PortalException {

		return _moduleFrameworkService.addBundle(location, inputStream, true);
	}

	public static Framework getFramework() {
		return _moduleFrameworkService.getFramework();
	}

	public static String getState(long bundleId) throws PortalException {
		return _moduleFrameworkService.getState(bundleId);
	}

	public static void registerContext(Object context) {
		_moduleFrameworkService.registerContext(context);
	}

	public static void setBundleStartLevel(long bundleId, int startLevel)
		throws PortalException {

		_moduleFrameworkService.setBundleStartLevel(bundleId, startLevel);
	}

	public static void startBundle(long bundleId) throws PortalException {
		_moduleFrameworkService.startBundle(bundleId);
	}

	public static void startBundle(long bundleId, int options)
		throws PortalException {

		_moduleFrameworkService.startBundle(bundleId, options);
	}

	public static void startFramework() throws Exception {
		_moduleFrameworkService.startFramework();
	}

	public static void startRuntime() throws Exception {
		_moduleFrameworkService.startRuntime();
	}

	public static void stopBundle(long bundleId) throws PortalException {
		_moduleFrameworkService.stopBundle(bundleId);
	}

	public static void stopBundle(long bundleId, int options)
		throws PortalException {

		_moduleFrameworkService.stopBundle(bundleId, options);
	}

	public static void stopFramework() throws Exception {
		_moduleFrameworkService.stopFramework();
	}

	public static void stopRuntime() throws Exception {
		_moduleFrameworkService.stopRuntime();
	}

	public static void setModuleFrameworkService(
		ModuleFrameworkService moduleFrameworkService) {

		_moduleFrameworkService = moduleFrameworkService;
	}
	public static void uninstallBundle(long bundleId) throws PortalException {
		_moduleFrameworkService.uninstallBundle(bundleId);
	}

	public static void updateBundle(long bundleId) throws PortalException {
		_moduleFrameworkService.updateBundle(bundleId);
	}

	public static void updateBundle(long bundleId, InputStream inputStream)
		throws PortalException {

		_moduleFrameworkService.updateBundle(bundleId, inputStream);
	}

	private ModuleFrameworkUtil() {
	}

	private static ModuleFrameworkService _moduleFrameworkService = 
		new LiferayModuleFrameworkService();
}
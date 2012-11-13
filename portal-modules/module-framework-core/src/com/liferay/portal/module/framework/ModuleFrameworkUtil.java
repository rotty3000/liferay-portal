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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.module.framework.internal.ModuleFrameworkImpl;

import java.io.InputStream;

import org.osgi.framework.launch.Framework;

/**
 * @author Raymond Augé
 */
public class ModuleFrameworkUtil {

	public static Object addBundle(String location) throws PortalException {
		return getInstance().addBundle(location);
	}

	public static Object addBundle(String location, InputStream inputStream)
		throws PortalException {
		return getInstance().addBundle(location, inputStream);
	}

	public static Framework getFramework() {
		return getInstance().getFramework();
	}

	public static ModuleFramework getInstance() {
		if (_instance == null) {

			// This can't be injected by Spring since we're running before it,
			// but we're making it inject-able for testing.

			_instance = new ModuleFrameworkImpl();
		}

		return _instance;
	}

	public static String getState(long bundleId) throws PortalException {
		return getInstance().getState(bundleId);
	}

	public static void registerContext(Object context) {
		getInstance().registerContext(context);
	}

	public static void setBundleStartLevel(long bundleId, int startLevel)
		throws PortalException {
		getInstance().setBundleStartLevel(bundleId, startLevel);
	}

	public static void setInstance(ModuleFramework instance) {
		_instance = instance;
	}

	public static void startBundle(long bundleId) throws PortalException {
		getInstance().startBundle(bundleId);
	}

	public static void startBundle(long bundleId, int options)
		throws PortalException {

		getInstance().startBundle(bundleId, options);
	}

	public static void startFramework() throws Exception {
		getInstance().startFramework();
	}

	public static void startRuntime() throws Exception {
		getInstance().startRuntime();
	}

	public static void stopBundle(long bundleId) throws PortalException {
		getInstance().stopBundle(bundleId);
	}

	public static void stopBundle(long bundleId, int options)
		throws PortalException {

		getInstance().stopBundle(bundleId, options);
	}

	public static void stopFramework() throws Exception {
		getInstance().stopFramework();
	}

	public static void stopRuntime() throws Exception {
		getInstance().stopRuntime();
	}

	public static void uninstallBundle(long bundleId) throws PortalException {
		getInstance().uninstallBundle(bundleId);
	}

	public static void updateBundle(long bundleId) throws PortalException {
		getInstance().updateBundle(bundleId);
	}

	public static void updateBundle(long bundleId, InputStream inputStream)
		throws PortalException {

		getInstance().updateBundle(bundleId, inputStream);
	}

	private static ModuleFramework _instance;

}
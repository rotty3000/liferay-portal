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
public interface ModuleFrameworkService extends ModuleFrameworkConstants {

	Object addBundle(String location) throws PortalException;

	Object addBundle(String location, InputStream inputStream)
		throws PortalException;

	Object addBundle(
			String location, InputStream inputStream, boolean checkPermissions) 
		throws PortalException;

	Framework getFramework();

	String getState(long bundleId) throws PortalException;

	void registerContext(Object context);

	void setBundleStartLevel(long bundleId, int startLevel)
		throws PortalException;

	void startBundle(long bundleId) throws PortalException;

	void startBundle(long bundleId, int options) throws PortalException; 

	void startFramework() throws Exception;

	void startRuntime() throws Exception;

	void stopBundle(long bundleId) throws PortalException;

	void stopBundle(long bundleId, int options) throws PortalException;

	void stopFramework() throws Exception;

	void stopRuntime() throws Exception;

	void uninstallBundle(long bundleId) throws PortalException;

	void updateBundle(long bundleId) throws PortalException;

	void updateBundle(long bundleId, InputStream inputStream)
		throws PortalException;

}
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

package com.liferay.marketplace.util;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * @author Joan Kim
 */
public class BundleUtil {

	public static Manifest getManifest(File file) {
		InputStream inputStream = null;
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(file);

			ZipEntry zipEntry = zipFile.getEntry("META-INF/MANIFEST.MF");

			if (zipEntry == null) {
				return null;
			}

			inputStream = zipFile.getInputStream(zipEntry);

			return new Manifest(inputStream);
		}
		catch (Exception e) {
		}
		finally {
			StreamUtil.cleanUp(inputStream);

			if (zipFile != null) {
				try {
					zipFile.close();
				}
				catch (IOException ioe) {
				}
			}
		}

		return null;
	}

	public static boolean isActive(String symbolicName, String version) {
		Bundle[] installedBundles = _instance._bundleContext.getBundles();

		for (Bundle bundle : installedBundles) {
			String curSymbolicName = bundle.getSymbolicName();

			if (!symbolicName.equals(curSymbolicName)) {
				continue;
			}

			Version curVersion = bundle.getVersion();

			if (version.equals(curVersion.toString())) {
				return true;
			}
		}

		return false;
	}

	public static void uninstallBundle(String symbolicName, String version) {
		try {
			Bundle[] installedBundles = _instance._bundleContext.getBundles();

			for (Bundle bundle : installedBundles) {
				String curSymbolicName = bundle.getSymbolicName();

				if (!symbolicName.equals(curSymbolicName)) {
					continue;
				}

				Version curVersion = bundle.getVersion();

				if (version.equals(curVersion.toString())) {
					bundle.uninstall();
				}
			}
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	public BundleUtil() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = bundle.getBundleContext();
	}

	private static final BundleUtil _instance = new BundleUtil();

	private final BundleContext _bundleContext;

}
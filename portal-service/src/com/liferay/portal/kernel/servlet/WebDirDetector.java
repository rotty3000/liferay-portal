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

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.ClassUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class WebDirDetector {

	public static String getLibDir(ClassLoader classLoader) {
		String libDir = ClassUtil.getParentPath(
			classLoader, "com.liferay.util.bean.PortletBeanLocatorUtil");

		if (libDir.endsWith("/WEB-INF/classes/")) {
			libDir = libDir.substring(0, libDir.length() - 8) + "lib/";
		}
		else {
			int pos = libDir.indexOf("/WEB-INF/lib/");

			if (pos != -1) {
				libDir = libDir.substring(0, pos) + "/WEB-INF/lib/";
			}
		}

		return libDir;
	}

	public static String getOSGiDir(ClassLoader classLoader) {
		return getOSGiDir(getLibDir(classLoader));
	}

	public static String getOSGiDir(String libDir) {
		String osgiDir = libDir;

		if (osgiDir.endsWith("/WEB-INF/lib/")) {
			osgiDir = osgiDir.replaceAll("/WEB-INF/lib/", "/WEB-INF/osgi/");
		}

		return osgiDir;
	}

	public static String getRootDir(ClassLoader classLoader) {
		return getRootDir(getLibDir(classLoader));
	}

	public static String getRootDir(String libDir) {
		String rootDir = libDir;

		if (rootDir.endsWith("/WEB-INF/lib/")) {
			rootDir = rootDir.substring(0, rootDir.length() - 12);
		}

		return rootDir;
	}

}
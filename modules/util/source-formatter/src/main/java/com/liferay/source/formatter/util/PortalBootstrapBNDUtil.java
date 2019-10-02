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

package com.liferay.source.formatter.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class PortalBootstrapBNDUtil {

	public static String updateExportPackages(File portalDir, String content)
		throws IOException {

		Map<String, String> packageVersionsMap = _getPackageVersionsMap(
			portalDir, "lib/portal/", "lib/development/");

		Matcher matcher = _exportVersionPattern.matcher(content);

		while (matcher.find()) {
			String packageName = matcher.group(1);

			if (packageName.equals("org.hibernate.*")) {
				continue;
			}

			String version = packageVersionsMap.get(packageName);

			if ((version == null) && packageName.endsWith(".*")) {
				packageName = packageName.substring(
					0, packageName.length() - 2);

				for (Map.Entry<String, String> entry :
						packageVersionsMap.entrySet()) {

					String key = entry.getKey();

					if (key.startsWith(packageName)) {
						version = entry.getValue();

						break;
					}
				}
			}

			if (version == null) {
				continue;
			}

			String match = matcher.group();

			String replacement = StringUtil.replaceLast(
				match, matcher.group(2), version);

			content = StringUtil.replace(content, match, replacement);
		}

		return content;
	}

	private static Map<String, String> _getPackageVersionsMap(
			File portalDir, String... dirNames)
		throws IOException {

		Map<String, String> packageVerionsMap = new LinkedHashMap<>();

		for (String dirName : dirNames) {
			File dependenciesPropertiesFile = new File(
				portalDir, dirName + "dependencies.properties");

			if (!dependenciesPropertiesFile.exists()) {
				continue;
			}

			Properties properties = new Properties();

			properties.load(new FileInputStream(dependenciesPropertiesFile));

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String version = _getVersion((String)entry.getValue());

				if (version == null) {
					continue;
				}

				String key = (String)entry.getKey();

				File jarFile = new File(portalDir, dirName + key + ".jar");

				if (!jarFile.exists()) {
					continue;
				}

				JarInputStream jarInputStream = null;

				try {
					jarInputStream = new JarInputStream(
						new FileInputStream(jarFile));

					while (true) {
						JarEntry jarEntry = jarInputStream.getNextJarEntry();

						if (jarEntry == null) {
							break;
						}

						String name = jarEntry.getName();

						if (name.endsWith(StringPool.SLASH) ||
							!name.contains(StringPool.SLASH)) {

							continue;
						}

						String packageName = StringUtil.replace(
							name.substring(0, name.lastIndexOf(CharPool.SLASH)),
							CharPool.SLASH, CharPool.PERIOD);

						if (!packageVerionsMap.containsKey(packageName)) {
							packageVerionsMap.put(packageName, version);
						}
					}
				}
				finally {
					if (jarInputStream != null) {
						jarInputStream.close();
					}
				}
			}
		}

		return packageVerionsMap;
	}

	private static String _getVersion(String s) {
		int x = s.lastIndexOf(CharPool.COLON);

		if (x == -1) {
			return null;
		}

		Matcher matcher = _versionPattern.matcher(s.substring(x + 1));

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private static final Pattern _exportVersionPattern = Pattern.compile(
		"\t(.*);version='(.*)',");
	private static final Pattern _versionPattern = Pattern.compile(
		"^([0-9]+(\\.[0-9]+){0,2})(\\.[^.]+)?$");

}
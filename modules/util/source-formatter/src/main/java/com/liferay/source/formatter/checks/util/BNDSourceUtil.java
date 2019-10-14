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

package com.liferay.source.formatter.checks.util;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Domain;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class BNDSourceUtil {

	public static Map<String, String> getDefinitionKeysMap() {
		return _populateDefinitionKeysMap(
			ArrayUtil.append(
				Constants.BUNDLE_SPECIFIC_HEADERS, Constants.headers,
				Constants.options));
	}

	public static String getDefinitionValue(String content, String key) {
		Pattern pattern = Pattern.compile(
			"^" + key + ": (.*)(\n|\\Z)", Pattern.MULTILINE);

		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public static List<String> getDefinitionValues(String content, String key) {
		List<String> definitionValues = new ArrayList<>();

		if (!content.contains(key + ":")) {
			return definitionValues;
		}

		String definitionValue = getDefinitionValue(content, key);

		if (definitionValue != null) {
			definitionValues.add(definitionValue);

			return definitionValues;
		}

		int x = content.indexOf(key + ":\\\n");

		if (x == -1) {
			return definitionValues;
		}

		int lineNumber = SourceUtil.getLineNumber(content, x);

		for (int i = lineNumber + 1;; i++) {
			String line = StringUtil.trim(SourceUtil.getLine(content, i));

			if (line.endsWith(",\\")) {
				definitionValues.add(
					StringUtil.replaceLast(line, ",\\", StringPool.BLANK));
			}
			else if (!line.endsWith(StringPool.BACK_SLASH)) {
				definitionValues.add(line);

				return definitionValues;
			}
		}
	}

	public static Map<String, Map<String, String>>
		getFileSpecificDefinitionKeysMap() {

		Map<String, Map<String, String>> fileSpecificDefinitionKeysMap =
			new HashMap<>();

		fileSpecificDefinitionKeysMap.put(
			"app.bnd", _populateDefinitionKeysMap(_APP_BND_DEFINITION_KEYS));
		fileSpecificDefinitionKeysMap.put(
			"bnd.bnd", _populateDefinitionKeysMap(_BND_BND_DEFINITION_KEYS));
		fileSpecificDefinitionKeysMap.put(
			"common.bnd",
			_populateDefinitionKeysMap(_COMMON_BND_DEFINITION_KEYS));
		fileSpecificDefinitionKeysMap.put(
			"subsystem.bnd",
			_populateDefinitionKeysMap(_SUBSYSTEM_BND_DEFINITION_KEYS));
		fileSpecificDefinitionKeysMap.put(
			"suite.bnd",
			_populateDefinitionKeysMap(_SUITE_BND_DEFINITION_KEYS));

		return fileSpecificDefinitionKeysMap;
	}

	public static String getModuleName(String absolutePath) {
		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		int y = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

		return absolutePath.substring(y + 1, x);
	}

	public static String updateInstruction(
		String content, String header, String value) {

		String instruction = header + StringPool.COLON;

		if (Validator.isNotNull(value)) {
			instruction = instruction + StringPool.SPACE + value;
		}

		if (!content.contains(header)) {
			return content + StringPool.NEW_LINE + instruction;
		}

		String[] lines = StringUtil.splitLines(content);

		for (String line : lines) {
			if (line.contains(header)) {
				content = StringUtil.replaceFirst(content, line, instruction);
			}
		}

		return content;
	}

	public static void updateSystemPackagesExtraBND(File portalDir)
		throws IOException {

		File systemPackagesExtraBNDFile = new File(
			portalDir,
			"modules/core/portal-bootstrap/system.packages.extra.bnd");

		String oldContent = FileUtil.read(systemPackagesExtraBNDFile);

		String newContent = _getUpdatedSystemPackagesExtraBNDContent(
			portalDir, oldContent);

		if (!oldContent.equals(newContent)) {
			FileUtil.write(systemPackagesExtraBNDFile, newContent);

			System.out.println(
				"Updated 'modules/core/portal-bootstrap" +
					"/system.packages.extra.bnd'");
		}
	}

	private static Map<String, Set<String>> _getPackageVersionsMap(
			File portalDir, String... dirNames)
		throws IOException {

		Map<String, Set<String>> packageVersionsMap = new HashMap<>();

		for (String dirName : dirNames) {
			File dependenciesPropertiesFile = new File(
				portalDir, dirName + "dependencies.properties");

			if (!dependenciesPropertiesFile.exists()) {
				continue;
			}

			Properties properties = new Properties();

			properties.load(new FileInputStream(dependenciesPropertiesFile));

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				File jarFile = new File(
					portalDir, dirName + (String)entry.getKey() + ".jar");

				if (!jarFile.exists()) {
					continue;
				}

				Domain domain = Domain.domain(jarFile);

				if ((domain != null) &&
					(domain.getBundleSymbolicName() != null)) {

					Parameters exportPackageParameters =
						domain.getExportPackage();

					for (Map.Entry<String, Attrs> curEntry :
							exportPackageParameters.entrySet()) {

						Attrs attrs = curEntry.getValue();

						_addPackageVersion(
							packageVersionsMap, curEntry.getKey(),
							attrs.getVersion());
					}

					continue;
				}

				String version = _getVersion((String)entry.getValue());

				if (version == null) {
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

						if (!packageVersionsMap.containsKey(packageName)) {
							_addPackageVersion(
								packageVersionsMap, packageName, version);
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

		return packageVersionsMap;
	}

	private static void _addPackageVersion(
		Map<String, Set<String>> packageVersionsMap, String packageName,
		String version) {

		Set<String> versions = packageVersionsMap.get(packageName);

		if (versions == null) {
			versions = new HashSet<>();
		}

		versions.add(version);

		packageVersionsMap.put(packageName, versions);
	}

	private static String _getUpdatedSystemPackagesExtraBNDContent(
			File portalDir, String content)
		throws IOException {

		Map<String, Set<String>> packageVersionsMap = _getPackageVersionsMap(
			portalDir, "lib/development/", "lib/global/", "lib/portal/");

		Matcher matcher = _exportVersionPattern.matcher(content);

		while (matcher.find()) {
			String packageName = matcher.group(1);

			Set<String> versions = packageVersionsMap.get(packageName);

			if (versions == null) {
				if (!packageName.endsWith(".*")) {
					continue;
				}

				versions = new HashSet<>();

				packageName = packageName.substring(
					0, packageName.length() - 2);

				for (Map.Entry<String, Set<String>> entry :
						packageVersionsMap.entrySet()) {

					String key = entry.getKey();

					if (key.startsWith(packageName)) {
						versions.addAll(entry.getValue());
					}
				}
			}

			if (versions.isEmpty()) {
				continue;
			}

			String version = matcher.group(2);

			Iterator<String> iterator = versions.iterator();

			if (versions.size() == 1) {
				String match = matcher.group();

				String replacement = StringUtil.replaceLast(
					match, version, iterator.next());

				content = StringUtil.replace(content, match, replacement);

				continue;
			}

			if (versions.contains(version)) {
				continue;
			}

			StringBundler sb = new StringBundler();

			sb.append("Incorrect version '");
			sb.append(version);
			sb.append("' for '");
			sb.append(packageName);
			sb.append("'. Valid candidates are: '");

			while (iterator.hasNext()) {
				sb.append(iterator.next());
				sb.append("', '");
			}

			sb.setIndex(sb.index() - 1);

			sb.append("'. modules/core/portal-bootstrap/");
			sb.append("system.packages.extra.bnd");

			System.out.println(sb.toString());
		}

		return content;
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

	private static Map<String, String> _populateDefinitionKeysMap(
		String[] keys) {

		Map<String, String> definitionKeysMap = new HashMap<>();

		for (String key : keys) {
			definitionKeysMap.put(StringUtil.toLowerCase(key), key);
		}

		return definitionKeysMap;
	}

	private static final String[] _APP_BND_DEFINITION_KEYS = {
		"Liferay-Releng-App-Description", "Liferay-Releng-App-Title",
		"Liferay-Releng-Bundle", "Liferay-Releng-Category",
		"Liferay-Releng-Demo-Url", "Liferay-Releng-Deprecated",
		"Liferay-Releng-Fix-Delivery-Method", "Liferay-Releng-Labs",
		"Liferay-Releng-Marketplace", "Liferay-Releng-Portal-Required",
		"Liferay-Releng-Public", "Liferay-Releng-Restart-Required",
		"Liferay-Releng-Suite", "Liferay-Releng-Support-Url",
		"Liferay-Releng-Supported"
	};

	private static final String[] _BND_BND_DEFINITION_KEYS = {
		"-jsp", "-liferay-aggregate-resource-bundles", "-metatype",
		"-metatype-inherit", "-sass", "Bundle-ActivationPolicy",
		"Can-Redefine-Classes", "Can-Retransform-Classes",
		"Eclipse-PlatformFilter", "Implementation-Version", "JPM-Command",
		"Liferay-Configuration-Path", "Liferay-JS-Config",
		"Liferay-JS-Resources-Top-Head-Authenticated",
		"Liferay-JS-Resources-Top-Head", "Liferay-JS-Submodules-Bridge",
		"Liferay-JS-Submodules-Export", "Liferay-Modules-Compat-Adapters",
		"Liferay-Releng-App-Description",
		"Liferay-Releng-Module-Group-Description",
		"Liferay-Releng-Module-Group-Title", "Liferay-Require-SchemaVersion",
		"Liferay-RTL-Support-Required", "Liferay-Service",
		"Liferay-Theme-Contributor-Type", "Liferay-Theme-Contributor-Weight",
		"Liferay-Versions", "Main-Class", "Premain-Class", "Web-ContextPath"
	};

	private static final String[] _COMMON_BND_DEFINITION_KEYS = {
		"Git-Descriptor", "Git-SHA", "Javac-Compiler", "Javac-Debug",
		"Javac-Deprecation", "Javac-Encoding", "Liferay-Portal-Build-Date",
		"Liferay-Portal-Build-Number", "Liferay-Portal-Build-Time",
		"Liferay-Portal-Code-Name", "Liferay-Portal-Parent-Build-Number",
		"Liferay-Portal-Release-Info", "Liferay-Portal-Server-Info",
		"Liferay-Portal-Version"
	};

	private static final String[] _SUBSYSTEM_BND_DEFINITION_KEYS = {
		"Liferay-Releng-Marketplace", "Liferay-Releng-Subsystem-Title"
	};

	private static final String[] _SUITE_BND_DEFINITION_KEYS = {
		"Liferay-Releng-Suite-Description", "Liferay-Releng-Suite-Title"
	};

	private static final Pattern _exportVersionPattern = Pattern.compile(
		"\t(.*);version='(.*)',");
	private static final Pattern _versionPattern = Pattern.compile(
		"^([0-9]+(\\.[0-9]+){0,2})(\\.[^.]+)?$");

}
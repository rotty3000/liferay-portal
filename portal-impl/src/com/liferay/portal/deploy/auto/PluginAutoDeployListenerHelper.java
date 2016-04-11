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

package com.liferay.portal.deploy.auto;

import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Constants;

import com.liferay.portal.kernel.deploy.auto.AutoDeployException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

/**
 * @author Manuel de la Peña
 */
public class PluginAutoDeployListenerHelper {

	public PluginAutoDeployListenerHelper(File file) {
		_file = file;
	}

	public boolean isExtPlugin() {
		Matcher matcher = _extPluginPattern.matcher(_file.getName());

		return matcher.find();
	}

	public boolean isHookPlugin() throws AutoDeployException {
		Matcher matcher = _hookPluginPattern.matcher(_file.getName());

		if (matcher.find() &&
			isMatchingFile("WEB-INF/liferay-hook.xml", false) &&
			!isMatchingFile("WEB-INF/liferay-portlet.xml", false)) {

			return true;
		}

		return false;
	}

	public boolean isLayoutTemplatePlugin() throws AutoDeployException {
		if (isMatchingFile("WEB-INF/liferay-layout-templates.xml") &&
			!isThemePlugin()) {

			return true;
		}

		return false;
	}

	public boolean isLiferayPackage() {
		String fileName = _file.getName();

		if (fileName.endsWith(".lpkg")) {
			return true;
		}

		return false;
	}

	public boolean isMatchingFile(String checkXmlFile)
		throws AutoDeployException {

		return isMatchingFile(checkXmlFile, true);
	}

	public boolean isMatchingFile(
			String checkXmlFile, boolean checkFileExtension)
		throws AutoDeployException {

		if (checkFileExtension && !isWarOrZip()) {
			return false;
		}

		if (_file.isDirectory()) {
			File xmlFile = new File(_file, checkXmlFile);

			return xmlFile.exists();
		}
		else {
			ZipFile zipFile = null;

			try {
				zipFile = new ZipFile(_file);

				if (zipFile.getEntry(checkXmlFile) == null) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							_file.getPath() + " does not have " + checkXmlFile);
					}

					return false;
				}
				else {
					return true;
				}
			}
			catch (IOException ioe) {
				throw new AutoDeployException(ioe);
			}
			finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					}
					catch (IOException ioe) {
					}
				}
			}
		}
	}

	public boolean isMatchingFileExtension(String ... extensions) {
		String fileName = _file.getName();

		fileName = StringUtil.toLowerCase(fileName);

		for (String extension : extensions) {
			if (fileName.endsWith(extension)) {
				if (_log.isDebugEnabled()) {
					_log.debug(_file.getPath() + " has a matching extension");
				}

				return true;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(_file.getPath() + " does not have a matching extension");
		}

		return false;
	}

	public boolean isThemePlugin() throws AutoDeployException {
		if (isMatchingFile("WEB-INF/liferay-look-and-feel.xml", false)) {
			return true;
		}

		Matcher matcher = _themePluginPattern.matcher(_file.getName());

		if (matcher.find() &&
			isMatchingFile(
				"WEB-INF/liferay-plugin-package.properties", false)) {

			return true;
		}

		return false;
	}

	public boolean isWAB() throws AutoDeployException {
		if (!isMatchingFileExtension(".war")) {
			return false;
		}

		JarInputStream jarInputStream = null;

		Manifest manifest = null;

		try {
			jarInputStream = new JarInputStream(new FileInputStream(_file));

			manifest = jarInputStream.getManifest();
		}
		catch (IOException ioe) {
			throw new AutoDeployException(ioe);
		}
		finally {
			StreamUtil.cleanUp(jarInputStream);
		}

		if (manifest == null) {
			return false;
		}

		Attributes attributes = manifest.getMainAttributes();

		String bundleSymbolicNameAttributeValue = attributes.getValue(
			Constants.BUNDLE_SYMBOLICNAME);

		Parameters bundleSymbolicNameMap = OSGiHeader.parseHeader(
			bundleSymbolicNameAttributeValue);

		Set<String> bundleSymbolicNameSet = bundleSymbolicNameMap.keySet();

		if (bundleSymbolicNameSet.isEmpty()) {
			return false;
		}

		Iterator<String> bundleSymbolicNameIterator =
			bundleSymbolicNameSet.iterator();

		String bundleSymbolicName = bundleSymbolicNameIterator.next();

		return Validator.isNotNull(bundleSymbolicName);
	}

	public boolean isWarOrZip() {
		return isMatchingFileExtension(".war", ".zip");
	}

	public boolean isWebPlugin() throws AutoDeployException {
		Matcher matcher = _webPluginPattern.matcher(_file.getName());

		if (matcher.find() &&
			isMatchingFile(
				"WEB-INF/liferay-plugin-package.properties", false)) {

			return true;
		}

		return false;
	}

	protected boolean isJarFile() {
		return isMatchingFileExtension(".jar");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PluginAutoDeployListenerHelper.class);

	private static final Pattern _extPluginPattern = Pattern.compile(
		"-(E|e)xt[-0-9.]*\\+?\\.(war|zip)$");
	private static final Pattern _hookPluginPattern = Pattern.compile(
		"-(H|h)ook[-0-9.]*\\+?\\.(war|zip)$");
	private static final Pattern _themePluginPattern = Pattern.compile(
		"-(T|t)heme[-0-9.]*\\+?\\.(war|zip)$");
	private static final Pattern _webPluginPattern = Pattern.compile(
		"-(W|w)eb[-0-9.]*\\+?\\.(war|zip)$");

	private final File _file;

}
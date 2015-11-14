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

package com.liferay.gradle.plugins.extensions;

import aQute.bnd.osgi.Constants;
import aQute.lib.spring.SpringComponent;

import com.liferay.ant.bnd.bower.BowerAnalyzerPlugin;
import com.liferay.ant.bnd.jsp.JspAnalyzerPlugin;
import com.liferay.ant.bnd.sass.SassAnalyzerPlugin;
import com.liferay.ant.bnd.spring.SpringDependencyAnalyzerPlugin;
import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.StringUtil;
import com.liferay.gradle.util.Validator;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayOSGiExtension extends LiferayExtension {

	public LiferayOSGiExtension(Project project) {
		super(project);

		_bndFile = project.file("bnd.bnd");

		Map<String, String> bundleInstructions = new HashMap<>();

		Properties properties = null;

		try {
			properties = FileUtil.readProperties(_bndFile);
		}
		catch (IOException ioe) {
			throw new GradleException("Unable to read " + _bndFile, ioe);
		}

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);

			bundleInstructions.put(key, value);
		}

		_bundleInstructions = Collections.unmodifiableMap(bundleInstructions);
	}

	public File getBndFile() {
		return _bndFile;
	}

	public Map<String, Object> getBundleDefaultInstructions() {
		Map<String, Object> map = new HashMap<>();

		map.put(Constants.BUNDLE_SYMBOLICNAME, project.getName());
		map.put(Constants.BUNDLE_VENDOR, "Liferay, Inc.");
		map.put(Constants.DONOTCOPY, "(.touch)");
		map.put(Constants.DSANNOTATIONS, "*");
		map.put(Constants.METATYPE, "*");
		map.put(
			Constants.PLUGIN, StringUtil.merge(_BND_PLUGIN_CLASS_NAMES, ","));
		map.put(Constants.SOURCES, "false");

		map.put(
			"Git-Descriptor",
			"${system-allow-fail;git describe --dirty --always}");
		map.put("Git-SHA", "${system-allow-fail;git rev-list -1 HEAD}");

		JavaCompile javaCompile = (JavaCompile)GradleUtil.getTask(
			project, JavaPlugin.COMPILE_JAVA_TASK_NAME);

		final CompileOptions compileOptions = javaCompile.getOptions();

		map.put(
			"Javac-Debug",
			new Object() {

				@Override
				public String toString() {
					return _getOnOffValue(compileOptions.isDebug());
				}

			});

		map.put(
			"Javac-Deprecation",
			new Object() {

				@Override
				public String toString() {
					return _getOnOffValue(compileOptions.isDeprecation());
				}

			});

		map.put(
			"Javac-Encoding",
			new Object() {

				@Override
				public String toString() {
					String encoding = compileOptions.getEncoding();

					if (Validator.isNull(encoding)) {
						encoding = System.getProperty("file.encoding");
					}

					return encoding;
				}

			});

		map.put("-jsp", "*.jsp,*.jspf");
		map.put("-sass", "*");

		return map;
	}

	public Map<String, String> getBundleInstructions() {
		return _bundleInstructions;
	}

	public boolean isAutoUpdateXml() {
		return _autoUpdateXml;
	}

	public void setAutoUpdateXml(boolean autoUpdateXml) {
		_autoUpdateXml = autoUpdateXml;
	}

	private String _getOnOffValue(boolean b) {
		if (b) {
			return "on";
		}

		return "off";
	}

	private static final String[] _BND_PLUGIN_CLASS_NAMES = {
		BowerAnalyzerPlugin.class.getName(), JspAnalyzerPlugin.class.getName(),
		SassAnalyzerPlugin.class.getName(), SpringComponent.class.getName(),
		SpringDependencyAnalyzerPlugin.class.getName()
	};

	private boolean _autoUpdateXml = true;
	private final File _bndFile;
	private final Map<String, String> _bundleInstructions;

}
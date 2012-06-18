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

package com.liferay.artifact.listener;

import java.io.File;

import java.net.URL;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Raymond Augé
 */
public class WarArtifactListener
	implements ArtifactUrlTransformer, BundleActivator {

	public boolean canHandle(File artifact) {
		String name = artifact.getName();

		if (name.endsWith(".war.jar") ||
			name.endsWith(".war")) {

			return true;
		}

		return false;
	}

	public URL transform(URL artifact) throws Exception {
		String path = artifact.getPath();

		int x = path.lastIndexOf('/');
		int y = path.lastIndexOf(".war.jar");

		if (y == -1) {
			y = path.lastIndexOf(".war");
		}

		String contextName = path.substring(x + 1, y);

		Pattern pattern = Pattern.compile("(.*?)-\\d+\\.\\d+\\.\\d+\\.\\d+");

		Matcher matcher = pattern.matcher(contextName);

		if (matcher.matches()) {
			contextName = matcher.group(1);
		}

		String pathWithQueryString =
			artifact.getPath().concat("?Web-ContextPath=/").concat(contextName);

		URL newURL = new URL("file", null, pathWithQueryString);
		newURL = new URL("webbundle", null, newURL.toString());

		return newURL;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		_serviceRegistration = bundleContext.registerService(
			ArtifactUrlTransformer.class, this, properties);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		_serviceRegistration.unregister();

		_serviceRegistration = null;
	}

	private ServiceRegistration<ArtifactUrlTransformer> _serviceRegistration;

}
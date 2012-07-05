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

package com.liferay.web.extender.internal.artifact;

import java.io.File;

import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;

/**
 * @author Raymond Augé
 */
public class WarArtifactListener implements ArtifactUrlTransformer {

	public boolean canHandle(File artifact) {
		String name = artifact.getName();

		if (name.endsWith(".war")) {

			return true;
		}

		return false;
	}

	public URL transform(URL artifact) throws Exception {
		String path = artifact.getPath();

		int x = path.lastIndexOf('/');
		int y = path.lastIndexOf(".war");

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

}
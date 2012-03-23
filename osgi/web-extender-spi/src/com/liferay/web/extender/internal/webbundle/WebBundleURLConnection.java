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

package com.liferay.web.extender.internal.webbundle;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.osgi.OSGiConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Map;

/**
 * @author Raymond Augé
 */
public class WebBundleURLConnection extends URLConnection {

	public WebBundleURLConnection(
		ClassLoader systemBundleClassLoader, URL url) {

		super(url);

		_systemBundleClassLoader = systemBundleClassLoader;
	}

	@Override
	public void connect() throws IOException {
	}

	@Override
	public InputStream getInputStream() throws IOException {
		URL url = getURL();

		String path = url.getPath();
		String queryString = url.getQuery();

		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
			queryString);

		if (!parameterMap.containsKey(OSGiConstants.WEB_CONTEXTPATH)) {
			throw new IllegalArgumentException(
				OSGiConstants.WEB_CONTEXTPATH + " parameter is required");
		}

		URL innerURL = new URL(path);

		File tempFile = FileUtil.createTempFile("war");

		StreamUtil.transfer(
			innerURL.openStream(), new FileOutputStream(tempFile));

		try {
			WebBundleProcessor webBundleProcessor = new WebBundleProcessor(
				_systemBundleClassLoader, tempFile, parameterMap);

			webBundleProcessor.process();

			return webBundleProcessor.getInputStream();
		}
		finally {
			tempFile.delete();
		}
	}

	private ClassLoader _systemBundleClassLoader;

}
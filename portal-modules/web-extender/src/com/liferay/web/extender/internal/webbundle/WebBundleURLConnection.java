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

import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.HttpImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Map;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WebBundleURLConnection extends URLConnection
	implements ModuleFrameworkConstants {

	public WebBundleURLConnection(
		ClassLoader systemBundleClassLoader, URL url) {

		super(url);

		// Do this because we're starting before spring context has wired these
		// together

		if (FastDateFormatFactoryUtil.getFastDateFormatFactory() == null) {
			FastDateFormatFactoryUtil instance = new FastDateFormatFactoryUtil();

			instance.setFastDateFormatFactory(new FastDateFormatFactoryImpl());
		}

		if (FileUtil.getFile() == null) {
			FileUtil instance = new FileUtil();

			instance.setFile(new FileImpl());
		}

		if (HttpUtil.getHttp() == null) {
			HttpUtil instance = new HttpUtil();

			instance.setHttp(new HttpImpl());
		}

		_systemBundleClassLoader = systemBundleClassLoader;
	}

	@Override
	public void connect() throws IOException {
		// not needed
	}

	@Override
	public InputStream getInputStream() throws IOException {
		URL url = getURL();

		String path = url.getPath();
		String queryString = url.getQuery();

		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
			queryString);

		if (!parameterMap.containsKey(WEB_CONTEXTPATH)) {
			throw new IllegalArgumentException(
				WEB_CONTEXTPATH + " parameter is required");
		}

		URL innerURL = new URL(path);

		File tempFile = transferToTempFolder(innerURL);

		try {
			WebBundleProcessor webBundleProcessor = new WebBundleProcessor(
				_systemBundleClassLoader, tempFile, parameterMap);

			webBundleProcessor.process();

			return webBundleProcessor.getInputStream();
		}
		finally {
			FileUtil.deltree(tempFile.getParentFile());
		}
	}

	/**
	 * This methods tries to keep the behaviour of the current deployment
	 * mechanism. So the name of the file is being kept as it is.
	 *
	 * <br />
	 *
	 * TODO We need to improve the way the plugins are detected
	 */
	protected File transferToTempFolder(URL url) throws IOException {
		File tempFolder = FileUtil.createTempFolder();

		int start = url.getPath().lastIndexOf(StringPool.SLASH);

		String fileName = url.getPath().substring(start + 1);

		File tempFile = new File(tempFolder, fileName);

		StreamUtil.transfer(url.openStream(), new FileOutputStream(tempFile));

		return tempFile;
	}

	private ClassLoader _systemBundleClassLoader;

}
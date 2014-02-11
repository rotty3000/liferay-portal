/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.servlet.filters.aggregate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletContextUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContext;

/**
 * @author Raymond Augé
 * @author Eduardo Lundgren
 */
public class ServletAggregateContext extends BaseAggregateContext {

	public ServletAggregateContext(
			ServletContext servletContext, String resourcePath)
		throws IOException {

		if (Validator.isNull(resourcePath)) {
			throw new IllegalArgumentException("ResourcePath is null");
		}

		_servletContext = servletContext;

		String rootPath = ServletContextUtil.getRootPath(_servletContext);

		int pos = resourcePath.lastIndexOf(CharPool.SLASH);

		if (pos == (resourcePath.length() - 1)) {
			int newPos = resourcePath.lastIndexOf(CharPool.SLASH, pos - 1);

			if (newPos >= 0) {
				pos = newPos;
			}
		}

		if (pos >= 0) {
			resourcePath = resourcePath.substring(0, pos);
		}

		if (resourcePath.startsWith(rootPath)) {
			resourcePath = resourcePath.substring(rootPath.length());
		}

		pushPath(resourcePath);
	}

	@Override
	public String getContent(String path) {
		try {
			String fullPath = getFullPath(StringPool.BLANK);

			URL resourceURL = _servletContext.getResource(
				fullPath.concat(path));

			if (resourceURL == null) {
				return null;
			}

			URLConnection urlConnection = resourceURL.openConnection();

			return StringUtil.read(urlConnection.getInputStream());
		}
		catch (IOException ioe) {
			_log.error(ioe, ioe);
		}

		return null;
	}

	private static Log _log = LogFactoryUtil.getLog(
		ServletAggregateContext.class);

	private ServletContext _servletContext;

}
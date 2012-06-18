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

package com.liferay.web.extender.servlet;

import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.util.PortalUtil;

import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * @author Raymond Augé
 */
public class ResourceServlet extends HttpServlet {

	public ResourceServlet(String name, HttpContext httpContext) {
		_name = name;

		if (_name.equals(StringPool.SLASH)) {
			_name = StringPool.BLANK;
		}

		_httpContext = httpContext;
	}

	@Override
	public void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		String pathInfo = request.getPathInfo();

		String fileName = pathInfo;

		int pos = fileName.lastIndexOf(StringPool.SLASH);

		if (pos != -1) {
			fileName = fileName.substring(pos + 1);
		}

		try {
			URL resourceURL = _httpContext.getResource(
				_name.concat(pathInfo));

			if (resourceURL == null) {
				throw new ServletException(pathInfo + " not found");
			}

			URLConnection connection = resourceURL.openConnection();

			int contentLength = connection.getContentLength();

			String contentType = _httpContext.getMimeType(fileName);

			ServletResponseUtil.sendFile(
				request, response, fileName, connection.getInputStream(),
				contentLength, contentType);
		}
		catch (Exception e) {
			PortalUtil.sendError(
				HttpServletResponse.SC_NOT_FOUND, e, request, response);
		}
	}

	private HttpContext _httpContext;
	private String _name;

}
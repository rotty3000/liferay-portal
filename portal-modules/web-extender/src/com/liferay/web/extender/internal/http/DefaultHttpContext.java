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

package com.liferay.web.extender.internal.http;

import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.web.extender.internal.servlet.BundleServletContext;

import java.io.IOException;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

/**
 * @author Raymond Augé
 */
public class DefaultHttpContext implements HttpContext {

	public DefaultHttpContext(BundleServletContext bundleServletContext) {
		_bundleServletContext = bundleServletContext;
	}

	public String getMimeType(String name) {
		String mimeType = _bundleServletContext.getMimeType(name);

		if (mimeType == null) {
			mimeType = MimeTypesUtil.getContentType(name);
		}

		return mimeType;
	}

	public URL getResource(String name) {
		Bundle bundle = (Bundle)_bundleServletContext.getAttribute(
			ModuleFrameworkConstants.OSGI_BUNDLE);

		return bundle.getEntry(name);
	}

	public BundleServletContext getBundleServletContext() {
		return _bundleServletContext;
	}

	public boolean handleSecurity(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		return true;
	}

	private BundleServletContext _bundleServletContext;

}

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

package com.liferay.portlet.security;

import com.liferay.portal.kernel.portlet.security.EmbeddedPortletRenderingContext;
import com.liferay.portal.kernel.portlet.security.EmbeddedPortletRenderingContextHandlerBase;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Theme;
import com.liferay.portal.model.impl.ThemeImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class EmbeddedPortletRenderingContextThemeHandler extends
	EmbeddedPortletRenderingContextHandlerBase<Theme> {

	public static String SUPPORTED_CLASS_NAME = ThemeImpl.class.getName();

	public EmbeddedPortletRenderingContext createContext(
		HttpServletRequest request, Theme parent) {

		EmbeddedPortletRenderingContext
			context = new EmbeddedPortletRenderingContext();

		String themeId = parent.getThemeId();
		long timestamp = parent.getTimestamp();

		context.setId(themeId);
		context.setTimestamp(timestamp);
		context.setParentClassName(SUPPORTED_CLASS_NAME);

		return context;
	}

	@Override
	public boolean doIsValid(
		HttpServletRequest request, EmbeddedPortletRenderingContext ctx) {

		Theme theme = (Theme)request.getAttribute(WebKeys.THEME);

		String themeId = ctx.getId();
		long timestamp = ctx.getTimestamp();

		if (theme.getThemeId().equals(themeId) &&
			(theme.getTimestamp() == timestamp)) {

			return true;
		}

		return false;
	}

	@Override
	public String[] getSupportedParentClassNames() {
		return new String[]{SUPPORTED_CLASS_NAME};
	}

}
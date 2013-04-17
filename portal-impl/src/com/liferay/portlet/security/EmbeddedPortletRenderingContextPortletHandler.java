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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.security.EmbeddedPortletRenderingContext;
import com.liferay.portal.kernel.portlet.security.EmbeddedPortletRenderingContextHandlerBase;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutType;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class EmbeddedPortletRenderingContextPortletHandler extends
	EmbeddedPortletRenderingContextHandlerBase<Portlet> {

	public static String SUPPORTED_CLASS_NAME = PortletImpl.class.getName();

	public EmbeddedPortletRenderingContext createContext(
		HttpServletRequest request, Portlet parent) {

		EmbeddedPortletRenderingContext
			context = new EmbeddedPortletRenderingContext();

		String portletId = parent.getPortletId();
		long timestamp = parent.getTimestamp();

		context.setId(portletId);
		context.setTimestamp(timestamp);
		context.setParentClassName(SUPPORTED_CLASS_NAME);

		return context;
	}

	@Override
	public boolean doIsValid(
			HttpServletRequest request, EmbeddedPortletRenderingContext ctx)
		throws PortalException, SystemException {

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		LayoutType layoutType = layout.getLayoutType();

		if ((layoutType == null) ||
			!(layoutType instanceof LayoutTypePortlet)) {

			return false;
		}

		LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)layoutType;

		String parentPortletId = ctx.getId();

		if (layoutTypePortlet.hasPortletId(parentPortletId)) {
			if (isTimestampValid(
					request, parentPortletId, ctx.getTimestamp())) {

				return true;
			}

			return false;
		}

		if (layoutTypePortlet.hasEmbeddedPortletId(parentPortletId)) {
			return layoutTypePortlet.checkEmbeddedPortletId(
				request, parentPortletId);
		}

		return false;
	}

	@Override
	public String[] getSupportedParentClassNames() {
		return new String[]{SUPPORTED_CLASS_NAME};
	}

	protected boolean isTimestampValid(
			HttpServletRequest request, String portletId, long timestamp)
		throws SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), portletId);

		if (portlet == null) {
			return false;
		}

		return timestamp == portlet.getTimestamp();
	}

}
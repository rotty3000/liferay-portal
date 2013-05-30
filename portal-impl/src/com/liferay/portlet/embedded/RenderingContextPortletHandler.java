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

package com.liferay.portlet.embedded;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.embedded.RenderingContext;
import com.liferay.portal.kernel.portlet.embedded.RenderingContextHandlerBase;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutType;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class RenderingContextPortletHandler
	extends RenderingContextHandlerBase<Portlet> {

	public static String SUPPORTED_CLASS_NAME = PortletImpl.class.getName();

	@Override
	public RenderingContext createContext(
		HttpServletRequest request, Portlet parent) {

		RenderingContext
			context = new RenderingContext();

		String portletId = parent.getPortletId();
		long timestamp = parent.getTimestamp();

		context.setId(portletId);
		context.setTimestamp(timestamp);
		context.setParentClassName(SUPPORTED_CLASS_NAME);

		return context;
	}

	@Override
	public RenderingContext fetchContext(List<RenderingContext> stack) {
		if ((stack == null) || (stack.size() == 0)) {
			return null;
		}

		RenderingContext ctx = stack.get(0);

		if (ctx.getParentClassName().equals(SUPPORTED_CLASS_NAME)) {
			return ctx;
		}

		// Portlets may also use layout templates. It's important to return and
		// check the portlet, which is deeper in the stack

		if (ctx.getParentClassName().equals(
				RenderingContextLayoutTemplateHandler.SUPPORTED_CLASS_NAME)) {

			for (RenderingContext context : stack) {
				if (context.getParentClassName().equals(SUPPORTED_CLASS_NAME)) {
					return context;
				}
			}
		}

		return null;
	}

	@Override
	protected boolean doIsValid(
			HttpServletRequest request, RenderingContext ctx)
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
			return layoutTypePortlet.isEmbeddedPortletIdValid(
				request, parentPortletId);
		}

		return false;
	}

	@Override
	protected String[] getSupportedParentClassNames() {
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
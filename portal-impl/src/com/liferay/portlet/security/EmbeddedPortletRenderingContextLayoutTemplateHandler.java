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
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTemplate;
import com.liferay.portal.model.LayoutType;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.impl.LayoutTemplateImpl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class EmbeddedPortletRenderingContextLayoutTemplateHandler
	extends EmbeddedPortletRenderingContextHandlerBase<LayoutTemplate> {

	public static String SUPPORTED_CLASS_NAME =
		LayoutTemplateImpl.class.getName();

	public EmbeddedPortletRenderingContext createContext(
		HttpServletRequest request, LayoutTemplate parent) {

		EmbeddedPortletRenderingContext
			context = new EmbeddedPortletRenderingContext();

		String portletId = parent.getLayoutTemplateId();
		long timestamp = parent.getTimestamp();

		context.setId(portletId);
		context.setTimestamp(timestamp);
		context.setParentClassName(SUPPORTED_CLASS_NAME);

		return context;
	}

	@Override
	public boolean doIsValid(
		HttpServletRequest request, EmbeddedPortletRenderingContext ctx) {

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);
		LayoutType layoutType = layout.getLayoutType();

		if ((layoutType == null) ||
			!(layoutType instanceof LayoutTypePortlet)) {

			return false;
		}

		LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)layoutType;
		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		String layoutTemplateId = ctx.getId();
		long timestamp = ctx.getTimestamp();

		if (layoutTemplate.getLayoutTemplateId().equals(layoutTemplateId) &&
			(layoutTemplate.getTimestamp() == timestamp)) {

			return true;
		}

		return false;
	}

	@Override
	public EmbeddedPortletRenderingContext fetchContext(
		List<EmbeddedPortletRenderingContext> stack) {

		// Portlets may also use layout templates. It's important to return and
		// check such portlet, not the template itself

		String portletClassName =
			EmbeddedPortletRenderingContextPortletHandler.SUPPORTED_CLASS_NAME;

		for (EmbeddedPortletRenderingContext context : stack) {
			if (context.getParentClassName().equals(portletClassName)) {
				return context;
			}
		}

		return stack.get(0);
	}

	@Override
	public String[] getSupportedParentClassNames() {
		return new String[]{SUPPORTED_CLASS_NAME};
	}

}
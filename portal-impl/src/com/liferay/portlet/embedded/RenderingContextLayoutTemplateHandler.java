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

import com.liferay.portal.kernel.portlet.embedded.RenderingContext;
import com.liferay.portal.kernel.portlet.embedded.RenderingContextHandlerBase;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTemplate;
import com.liferay.portal.model.LayoutType;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.impl.LayoutTemplateImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class RenderingContextLayoutTemplateHandler
	extends RenderingContextHandlerBase<LayoutTemplate> {

	public static String SUPPORTED_CLASS_NAME =
		LayoutTemplateImpl.class.getName();

	@Override
	public RenderingContext createContext(
		HttpServletRequest request, LayoutTemplate parent) {

		RenderingContext
			context = new RenderingContext();

		String portletId = parent.getLayoutTemplateId();
		long timestamp = parent.getTimestamp();

		context.setId(portletId);
		context.setTimestamp(timestamp);
		context.setParentClassName(SUPPORTED_CLASS_NAME);

		return context;
	}

	@Override
	protected boolean doIsValid(
		HttpServletRequest request, RenderingContext ctx) {

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
	protected String[] getSupportedParentClassNames() {
		return new String[]{SUPPORTED_CLASS_NAME};
	}

}
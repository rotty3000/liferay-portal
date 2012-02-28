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

package com.liferay.portlet.layoutprototypes.action;

import com.liferay.portal.NoSuchLayoutPrototypeException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.LayoutPrototype;
import com.liferay.portal.service.LayoutPrototypeServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jorge Ferrer
 */
public class ActionUtil {

	public static void getLayoutPrototype(HttpServletRequest request)
		throws Exception {

		long layoutPrototypeId = ParamUtil.getLong(
			request, "layoutPrototypeId");

		LayoutPrototype layoutPrototype = null;

		try {
			layoutPrototype = LayoutPrototypeServiceUtil.getLayoutPrototype(
				layoutPrototypeId);
		}
		catch (NoSuchLayoutPrototypeException nslpe) {
		}

		request.setAttribute(WebKeys.LAYOUT_PROTOTYPE, layoutPrototype);
	}

	public static void getLayoutPrototype(PortletRequest portletRequest)
		throws Exception {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			portletRequest);

		getLayoutPrototype(request);
	}

}
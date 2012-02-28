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

package com.liferay.portlet.layoutsetprototypes.action;

import com.liferay.portal.NoSuchLayoutSetPrototypeException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.LayoutSetPrototype;
import com.liferay.portal.service.LayoutSetPrototypeServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class ActionUtil {

	public static void getLayoutSetPrototype(HttpServletRequest request)
		throws Exception {

		long layoutSetPrototypeId = ParamUtil.getLong(
			request, "layoutSetPrototypeId");

		LayoutSetPrototype layoutSetPrototype = null;

		try {
			layoutSetPrototype =
				LayoutSetPrototypeServiceUtil.getLayoutSetPrototype(
					layoutSetPrototypeId);
		}
		catch (NoSuchLayoutSetPrototypeException nslspe) {
		}

		request.setAttribute(WebKeys.LAYOUT_PROTOTYPE, layoutSetPrototype);
	}

	public static void getLayoutSetPrototype(PortletRequest portletRequest)
		throws Exception {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			portletRequest);

		getLayoutSetPrototype(request);
	}

}
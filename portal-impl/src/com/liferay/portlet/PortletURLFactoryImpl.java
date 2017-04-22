/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
@DoPrivileged
public class PortletURLFactoryImpl implements PortletURLFactory {

	@Override
	public LiferayPortletURL create(
		HttpServletRequest request, Portlet portlet, Layout layout,
		String lifecycle) {

		return new PortletURLImpl(request, portlet, layout, lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		HttpServletRequest request, Portlet portlet, String lifecycle) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			layout = _getLayout(
				(Layout)request.getAttribute(WebKeys.LAYOUT),
				themeDisplay.getPlid());
		}

		return new PortletURLImpl(request, portlet, layout, lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		HttpServletRequest request, String portletId, Layout layout,
		String lifecycle) {

		return new PortletURLImpl(
			request,
			PortletLocalServiceUtil.getPortletById(
				PortalUtil.getCompanyId(request), portletId),
			layout, lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		HttpServletRequest request, String portletId, long plid,
		String lifecycle) {

		return create(
			request, portletId,
			_getLayout((Layout)request.getAttribute(WebKeys.LAYOUT), plid),
			lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		HttpServletRequest request, String portletId, String lifecycle) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			layout = _getLayout(
				(Layout)request.getAttribute(WebKeys.LAYOUT),
				themeDisplay.getPlid());
		}

		return create(request, portletId, layout, lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		PortletRequest portletRequest, Portlet portlet, Layout layout,
		String lifecycle) {

		return new PortletURLImpl(portletRequest, portlet, layout, lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, Layout layout,
		String lifecycle) {

		return new PortletURLImpl(
			portletRequest,
			PortletLocalServiceUtil.getPortletById(
				PortalUtil.getCompanyId(portletRequest), portletId),
			layout, lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, long plid,
		String lifecycle) {

		return create(
			portletRequest, portletId,
			_getLayout(
				(Layout)portletRequest.getAttribute(WebKeys.LAYOUT), plid),
			lifecycle);
	}

	@Override
	public LiferayPortletURL create(
		PortletRequest portletRequest, String portletId, String lifecycle) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			layout = _getLayout(
				(Layout)portletRequest.getAttribute(WebKeys.LAYOUT),
				themeDisplay.getPlid());
		}

		return create(portletRequest, portletId, layout, lifecycle);
	}

	private Layout _getLayout(Layout layout, long plid) {
		if ((layout != null) && (layout.getPlid() == plid)) {
			return layout;
		}

		if (plid > 0) {
			return LayoutLocalServiceUtil.fetchLayout(plid);
		}

		return null;
	}

}
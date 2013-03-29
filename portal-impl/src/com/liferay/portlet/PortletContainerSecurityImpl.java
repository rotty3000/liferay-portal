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

package com.liferay.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletContainerSecurity;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import javax.portlet.PortletMode;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
@DoPrivileged
public class PortletContainerSecurityImpl implements PortletContainerSecurity {

	public void check(HttpServletRequest request, Portlet portlet)
		throws PrincipalException {

		try {
			doCheck(request, portlet);
		} catch (PrincipalException e) {
			throw e;
		} catch (Exception e) {
			throw new PrincipalException(e);
		}
	}

	protected void doCheck(HttpServletRequest request, Portlet portlet)
		throws Exception {

		if (portlet == null) {
			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		long scopeGroupId = themeDisplay.getScopeGroupId();
		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		if (layout.isTypeControlPanel()) {
			if (portlet.isSystem()) {
				return;
			}

			if (PortletPermissionUtil.hasControlPanelAccessPermission(
					permissionChecker, scopeGroupId, portlet)) {

				return;
			}

			if (PortalUtil.isAllowAddPortletDefaultResource(request, portlet)) {
				return;
			}

			throw new PrincipalException();
		}

		if (PortalUtil.isAllowAddPortletDefaultResource(request, portlet)) {

			PortalUtil.addPortletDefaultResource(request, portlet);

			PortletMode portletMode = PortletModeFactory.getPortletMode(
				ParamUtil.getString(request, "p_p_mode"));

			boolean access = PortletPermissionUtil.hasAccessPermission(
				permissionChecker, scopeGroupId, layout, portlet, portletMode);

			if (access) {
				return;
			}
		}

		throw new PrincipalException();
	}

	private static Log _log = LogFactoryUtil.getLog(
		PortletContainerSecurityImpl.class);

}
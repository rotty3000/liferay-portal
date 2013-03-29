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

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.security.auth.PrincipalException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class PortletContainerSecurityUtil {

	public static void checkAction(HttpServletRequest request, Portlet portlet)
		throws PrincipalException {

		getPortletContainerSecurity().checkAction(request, portlet);
	}

	public static void checkRender(HttpServletRequest request, Portlet portlet)
		throws PrincipalException {

		getPortletContainerSecurity().checkRender(request, portlet);
	}

	public static void checkResource(
			HttpServletRequest request, Portlet portlet)
		throws PrincipalException {

		getPortletContainerSecurity().checkResource(request, portlet);
	}

	public static PortletContainerSecurity getPortletContainerSecurity() {
		PortalRuntimePermission.checkGetBeanProperty(
			PortletContainerSecurityUtil.class);

		return _portletContainerSecurity;
	}

	public void setPortletContainerSecurity(
		PortletContainerSecurity portletContainerSecurity) {

		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_portletContainerSecurity = portletContainerSecurity;
	}

	private static PortletContainerSecurity _portletContainerSecurity;

}
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

import com.liferay.portal.model.Portlet;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public interface PortletContainerSecurity {

	public PortletContainerSecurityCheckResult checkAction(
			HttpServletRequest request, Portlet portlet)
		throws Exception;

	public PortletContainerSecurityCheckResult checkRender(
			HttpServletRequest request, Portlet portlet)
		throws Exception;

	public PortletContainerSecurityCheckResult checkResource(
			HttpServletRequest request, Portlet portlet)
		throws Exception;

	public Set<String> getPortletAddDefaultResourceCheckWhitelist();

	public Set<String> getPortletAddDefaultResourceCheckWhitelistActions();

	public Set<String> resetPortletAddDefaultResourceCheckWhitelist();

	public Set<String> resetPortletAddDefaultResourceCheckWhitelistActions();

}
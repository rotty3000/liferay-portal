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

package com.liferay.portal.kernel.portlet.embedded;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public interface RenderingContextHandler<T> {

	public boolean canCreateContext(Object parent);

	public RenderingContext createContext(HttpServletRequest request, T parent);

	public RenderingContext fetchContext(List<RenderingContext> stack);

	public boolean isValid(
			HttpServletRequest request,
			RenderingContext embeddedPortletRenderingContext)
		throws PortalException, SystemException;

}
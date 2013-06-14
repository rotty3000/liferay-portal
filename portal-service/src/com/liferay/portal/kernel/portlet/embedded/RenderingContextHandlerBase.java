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
public abstract class RenderingContextHandlerBase<T>
	implements RenderingContextHandler<T> {

	@Override
	public boolean canCreateContext(Object parent) {
		if (parent == null) {
			return false;
		}

		String parentClassName = parent.getClass().getName();

		if (parent instanceof String) {
			parentClassName = parent.toString();
		}

		for (String supportedParentClassName : getSupportedParentClassNames()) {
			if (supportedParentClassName.equals(parentClassName)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RenderingContext fetchContext(List<RenderingContext> stack) {
		if ((stack == null) || (stack.size() == 0)) {
			return null;
		}

		RenderingContext ctx = stack.get(0);

		for (String supportedParentClassName : getSupportedParentClassNames()) {
			if (ctx.getParentClassName().equals(supportedParentClassName)) {
				return ctx;
			}
		}

		return null;
	}

	@Override
	public boolean isValid(HttpServletRequest request, RenderingContext ctx)
		throws PortalException, SystemException {

		if (ctx == null) {
			return false;
		}

		if (request == null) {
			return false;
		}

		for (String supportedParentClassName : getSupportedParentClassNames()) {
			if (ctx.getParentClassName().equals(supportedParentClassName)) {
				return doIsValid(request, ctx);
			}
		}

		return false;
	}

	protected abstract boolean doIsValid(
			HttpServletRequest request,
			RenderingContext embeddedPortletRenderingContext)
		throws PortalException, SystemException;

	protected abstract String[] getSupportedParentClassNames();

}
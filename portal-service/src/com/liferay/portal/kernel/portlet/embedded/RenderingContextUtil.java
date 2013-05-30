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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class RenderingContextUtil {

	public static void cloneContext(HttpServletRequest request) {
		List<RenderingContext> contextStackCloned =
			new ArrayList<RenderingContext>(getStack(request));

		request.setAttribute(_KEY, contextStackCloned);
	}

	public static RenderingContext getActualContext(HttpServletRequest request)
		throws PortalException, SystemException {

		List<RenderingContext> stack = getStack(request);

		if (stack.size() == 0) {
			return null;
		}

		for (RenderingContextHandler handler : _HANDLERS) {
			RenderingContext result = handler.fetchContext(stack);

			if (result != null) {
				return result;
			}
		}

		return null;
	}

	public static List<RenderingContext> getStack(HttpServletRequest request) {
		List<RenderingContext> contextStack =
			(List<RenderingContext>)request.getAttribute(_KEY);

		if (contextStack == null) {
			contextStack = new ArrayList<RenderingContext>();
			request.setAttribute(_KEY, contextStack);
		}

		return contextStack;
	}

	public static boolean isValid(
			HttpServletRequest request,
			RenderingContext embeddedPortletRenderingContext)
		throws PortalException, SystemException {

		if (embeddedPortletRenderingContext == null) {
			return false;
		}

		for (RenderingContextHandler handler : _HANDLERS) {
			if (handler.isValid(request, embeddedPortletRenderingContext)) {
				return true;
			}
		}

		return false;
	}

	public static void pop(HttpServletRequest request) {
		List<RenderingContext> contextStack = getStack(request);

		if (contextStack.size() > 0) {
			contextStack.remove(0);
		}
	}

	public static void push(
		HttpServletRequest request, RenderingContext context) {

		List<RenderingContext> contextStack = getStack(request);

		contextStack.add(0, context);
	}

	public static void pushParent(HttpServletRequest request, Object parent) {
		RenderingContext context = null;

		for (RenderingContextHandler handler : _HANDLERS) {
			if (handler.canCreateContext(parent)) {
				context = handler.createContext(request, parent);
				break;
			}
		}

		if (context != null) {
			push(request, context);
		}
	}

	public void setHandlers(List<RenderingContextHandler> handlers) {
		_HANDLERS = handlers;
	}

	private static final String _KEY = "EmbeddedPortletRenderingContext";
	private static List<RenderingContextHandler> _HANDLERS =
		new ArrayList<RenderingContextHandler>();

}
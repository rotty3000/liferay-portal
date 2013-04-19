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

package com.liferay.portal.kernel.portlet.security;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class EmbeddedPortletRenderingContextUtil {

	public static void cloneContext(HttpServletRequest request) {
		List<EmbeddedPortletRenderingContext> contextStackCloned =
			new ArrayList<EmbeddedPortletRenderingContext>(getStack(request));

		request.setAttribute(KEY, contextStackCloned);
	}

	public static EmbeddedPortletRenderingContext getActualContext(
			HttpServletRequest request)
		throws PortalException, SystemException {

		List<EmbeddedPortletRenderingContext> stack = getStack(request);

		if (stack.size() == 0) {
			return null;
		}

		for (EmbeddedPortletRenderingContextHandler handler : _HANDLERS) {
			EmbeddedPortletRenderingContext result = handler.fetchContext(
				stack);

			if (result != null) {
				return result;
			}
		}

		return null;
	}

	public static List<EmbeddedPortletRenderingContext> getStack(
		HttpServletRequest request) {

		List<EmbeddedPortletRenderingContext> contextStack =
			(List<EmbeddedPortletRenderingContext>)request.getAttribute(KEY);

		if (contextStack == null) {
			contextStack = new ArrayList<EmbeddedPortletRenderingContext>();
			request.setAttribute(KEY, contextStack);
		}

		return contextStack;
	}

	public static boolean isValid(
			HttpServletRequest request,
			EmbeddedPortletRenderingContext embeddedPortletRenderingContext)
		throws PortalException, SystemException {

		if (embeddedPortletRenderingContext == null) {
			return false;
		}

		for (EmbeddedPortletRenderingContextHandler handler : _HANDLERS) {
			if (handler.isValid(request, embeddedPortletRenderingContext)) {
				return true;
			}
		}

		return false;
	}

	public static void pop(HttpServletRequest request) {
		List<EmbeddedPortletRenderingContext> contextStack = getStack(request);

		if (contextStack.size() > 0) {
			contextStack.remove(0);
		}
	}

	public static void push(
		HttpServletRequest request, EmbeddedPortletRenderingContext context) {

		List<EmbeddedPortletRenderingContext> contextStack = getStack(request);

		contextStack.add(0, context);
	}

	public static void pushParent(HttpServletRequest request, Object parent) {
		EmbeddedPortletRenderingContext context = null;

		for (EmbeddedPortletRenderingContextHandler handler : _HANDLERS) {
			if (handler.canCreateContext(parent)) {
				context = handler.createContext(request, parent);
				break;
			}
		}

		if (context != null) {
			push(request, context);
		}
	}

	public void setHandlers(
		List<EmbeddedPortletRenderingContextHandler> handlers) {

		_HANDLERS = handlers;
	}

	private static final String KEY = "EmbeddedPortletRenderingContext";
	private static List<EmbeddedPortletRenderingContextHandler> _HANDLERS =
		new ArrayList<EmbeddedPortletRenderingContextHandler>();

}
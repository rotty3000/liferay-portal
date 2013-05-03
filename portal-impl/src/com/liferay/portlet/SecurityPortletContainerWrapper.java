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
import com.liferay.portal.kernel.portlet.ActionResult;
import com.liferay.portal.kernel.portlet.PortletContainer;
import com.liferay.portal.kernel.portlet.PortletContainerException;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.TempAttributesServletRequest;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsValues;

import java.util.List;

import javax.portlet.Event;
import javax.portlet.PortletMode;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tomas Polesovsky
 */
public class SecurityPortletContainerWrapper implements PortletContainer {

	public SecurityPortletContainerWrapper(PortletContainer portletContainer) {
		_portletContainer = portletContainer;
	}

	public void preparePortlet(HttpServletRequest request, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.preparePortlet(request, portlet);
	}

	public ActionResult processAction(
			HttpServletRequest request, HttpServletResponse response,
			Portlet portlet)
		throws PortletContainerException {

		try {
			HttpServletRequest ownerLayoutRequest =
				getOwnerLayoutRequestWrapper(request, portlet);

			check(ownerLayoutRequest, portlet);

			return _portletContainer.processAction(request, response, portlet);
		}
		catch (PrincipalException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e);
			}

			String url = null;

			LastPath lastPath = (LastPath)request.getAttribute(
				WebKeys.LAST_PATH);

			if (lastPath != null) {
				StringBundler sb = new StringBundler(3);

				sb.append(PortalUtil.getPortalURL(request));
				sb.append(lastPath.getContextPath());
				sb.append(lastPath.getPath());

				url = sb.toString();
			}
			else {
				url = String.valueOf(request.getRequestURI());
			}

			_log.warn(
				"Reject processAction for " + url + " on " +
					portlet.getPortletId());

			return ActionResult.EMPTY_ACTION_RESULT;
		}
		catch (PortletContainerException e) {
			throw e;
		}
		catch (Exception e) {
			throw new PortletContainerException(e);
		}
	}

	public List<Event> processEvent(
			HttpServletRequest request, HttpServletResponse response,
			Portlet portlet, Layout layout, Event event)
		throws PortletContainerException {

		return _portletContainer.processEvent(
			request, response, portlet, layout, event);
	}

	public void render(
			HttpServletRequest request, HttpServletResponse response,
			Portlet portlet)
		throws PortletContainerException {

		try {
			check(request, portlet);

			_portletContainer.render(request, response, portlet);
		}
		catch (PrincipalException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e);
			}

			renderPortletError(request, response, portlet);
		}
		catch (PortletContainerException e) {
			throw e;
		}
		catch (Exception e) {
			throw new PortletContainerException(e);
		}
	}

	public void serveResource(
			HttpServletRequest request, HttpServletResponse response,
			Portlet portlet)
		throws PortletContainerException {

		try {
			HttpServletRequest ownerLayoutRequest =
				getOwnerLayoutRequestWrapper(request, portlet);

			check(ownerLayoutRequest, portlet);

			_portletContainer.serveResource(request, response, portlet);
		}
		catch (PrincipalException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e);
			}

			String url = null;

			LastPath lastPath = (LastPath)request.getAttribute(
				WebKeys.LAST_PATH);

			if (lastPath != null) {
				StringBundler sb = new StringBundler(3);

				sb.append(PortalUtil.getPortalURL(request));
				sb.append(lastPath.getContextPath());
				sb.append(lastPath.getPath());

				url = sb.toString();
			}
			else {
				url = String.valueOf(request.getRequestURI());
			}

			response.setHeader(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			_log.warn(
				"Reject serveResource for " + url + " on " +
					portlet.getPortletId());

			return;
		}
		catch (PortletContainerException e) {
			throw e;
		}
		catch (Exception e) {
			throw new PortletContainerException(e);
		}
	}

	protected void check(HttpServletRequest request, Portlet portlet)
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

	protected HttpServletRequest getOwnerLayoutRequestWrapper(
			HttpServletRequest request, Portlet portlet)
		throws Exception {

		if (!PropsValues.PORTLET_EVENT_DISTRIBUTION_LAYOUT_SET) {
			return request;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout currentLayout = themeDisplay.getLayout();

		Layout requestLayout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		List<LayoutTypePortlet> layoutTypePortlets =
			PortletContainerUtil.getLayoutTypePortlets(requestLayout);

		Layout ownerLayout = null;
		LayoutTypePortlet ownerLayoutTypePortlet = null;

		for (LayoutTypePortlet layoutTypePortlet : layoutTypePortlets) {
			if (layoutTypePortlet.hasPortletId(portlet.getPortletId())) {
				ownerLayoutTypePortlet = layoutTypePortlet;

				ownerLayout = layoutTypePortlet.getLayout();

				break;
			}
		}

		if ((ownerLayout != null) && !currentLayout.equals(ownerLayout)) {
			ThemeDisplay themeDisplayClone = (ThemeDisplay)themeDisplay.clone();

			themeDisplayClone.setLayout(ownerLayout);
			themeDisplayClone.setLayoutTypePortlet(ownerLayoutTypePortlet);

			TempAttributesServletRequest tempAttributesServletRequest =
				new TempAttributesServletRequest(request);

			tempAttributesServletRequest.setTempAttribute(
				WebKeys.THEME_DISPLAY, themeDisplayClone);
			tempAttributesServletRequest.setTempAttribute(
				WebKeys.LAYOUT, ownerLayout);

			return tempAttributesServletRequest;
		}

		return request;
	}

	protected void renderPortletError(
			HttpServletRequest request, HttpServletResponse response,
			Portlet portlet)
		throws PortletContainerException {

		String portletContent = null;

		if (portlet.isShowPortletAccessDenied()) {
			portletContent = "/html/portal/portlet_access_denied.jsp";
		}

		try {
			if (portletContent != null) {
				RequestDispatcher requestDispatcher =
					request.getRequestDispatcher(portletContent);

				requestDispatcher.include(request, response);
			}
		} catch (Exception ex) {
			throw new PortletContainerException(ex);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		SecurityPortletContainerWrapper.class);

	private PortletContainer _portletContainer;

}
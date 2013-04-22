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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletContainerSecurity;
import com.liferay.portal.kernel.portlet.PortletContainerSecurityCheckResult;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.security.auth.AuthTokenUtil;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.permission.GroupPermissionUtil;
import com.liferay.portal.service.permission.LayoutPermissionUtil;
import com.liferay.portal.service.permission.LayoutPrototypePermissionUtil;
import com.liferay.portal.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsValues;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
@DoPrivileged
public class PortletContainerSecurityImpl implements PortletContainerSecurity {

	public PortletContainerSecurityImpl() {

		// Portlet add default resource check white list

		resetPortletAddDefaultResourceCheckWhitelist();
		resetPortletAddDefaultResourceCheckWhitelistActions();
	}

	public PortletContainerSecurityCheckResult checkAction(
			HttpServletRequest request, Portlet portlet)
		throws Exception {

		PortletContainerSecurityCheckResult result =
			new PortletContainerSecurityCheckResult();

		checkPageSecurity(request, portlet, result);

		Map<String, String> initParams = portlet.getInitParams();

		boolean checkAuthToken = GetterUtil.getBoolean(
			initParams.get("check-auth-token"), true);

		if (PropsValues.AUTH_TOKEN_CHECK_ENABLED && checkAuthToken) {
			AuthTokenUtil.check(request);
		}

		return result;
	}

	public PortletContainerSecurityCheckResult checkRender(
			HttpServletRequest request, Portlet portlet)
		throws Exception {

		PortletContainerSecurityCheckResult result =
			new PortletContainerSecurityCheckResult();

		checkPageSecurity(request, portlet, result);

		return result;
	}

	public PortletContainerSecurityCheckResult checkResource(
			HttpServletRequest request, Portlet portlet)
		throws Exception {

		PortletContainerSecurityCheckResult result =
			new PortletContainerSecurityCheckResult();

		checkPageSecurity(request, portlet, result);

		return result;
	}

	public Set<String> getPortletAddDefaultResourceCheckWhitelist() {
		return _portletAddDefaultResourceCheckWhitelist;
	}

	public Set<String> getPortletAddDefaultResourceCheckWhitelistActions() {
		return _portletAddDefaultResourceCheckWhitelistActions;
	}

	public Set<String> resetPortletAddDefaultResourceCheckWhitelist() {
		_portletAddDefaultResourceCheckWhitelist = SetUtil.fromArray(
			PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST);

		_portletAddDefaultResourceCheckWhitelist = Collections.unmodifiableSet(
			_portletAddDefaultResourceCheckWhitelist);

		return _portletAddDefaultResourceCheckWhitelist;
	}

	public Set<String> resetPortletAddDefaultResourceCheckWhitelistActions() {
		_portletAddDefaultResourceCheckWhitelistActions = SetUtil.fromArray(
			PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS);

		_portletAddDefaultResourceCheckWhitelistActions =
			Collections.unmodifiableSet(
				_portletAddDefaultResourceCheckWhitelistActions);

		return _portletAddDefaultResourceCheckWhitelistActions;
	}

	protected void checkControlPanelPageSecurity(
			HttpServletRequest request, Portlet portlet,
			PortletContainerSecurityCheckResult result)
		throws PortalException, SystemException {

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		if (!layout.isTypeControlPanel()) {
			return;
		}

		if (portlet.isSystem()) {
			result.setExecutingControlPanelSystemPortlet();
			result.setHasPermissions();
		}

		if (!result.isExecutionAllowed() &&
			hasControlPanelAccessPermission(request, portlet)) {

			result.setExecutingControlPanelPortlet();
			result.setHasPermissions();
		}
	}

	protected boolean checkEmbeddedPortlet(
			HttpServletRequest request, Portlet portlet)
		throws PortalException, SystemException {

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		String peAuth = request.getParameter("p_e_auth");
		if (Validator.isNull(peAuth)) {
			return false;
		}

		String embeddedPortletToken =
			AuthTokenUtil.generateEmbeddedPortletToken(
				request, layout.getPlid(), portlet.getPortletId());

		if (!embeddedPortletToken.equals(peAuth)) {
			return false;
		}

		if (layoutTypePortlet.checkEmbeddedPortletId(
				request, portlet.getPortletId())) {

			return true;
		}

		return false;
	}

	protected void checkPageSecurity(
			HttpServletRequest request, Portlet portlet,
			PortletContainerSecurityCheckResult result)
		throws Exception {

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		if (layout.isTypeControlPanel()) {
			checkControlPanelPageSecurity(request, portlet, result);

			if (result.isExecutionAllowed() && result.hasPermission()) {
				return;
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletId = portlet.getPortletId();

		if (themeDisplay.isSignedIn() &&
			portletId.equals(PortletKeys.LAYOUTS_ADMIN) &&
			isLayoutConfigurationAllowed(request, portlet)) {

			result.setExecutingPortletConfiguration();
		}

		if (!result.isExecutionAllowed() &&
			checkEmbeddedPortlet(request, portlet)) {

			result.setExecutingEmbeddedPortlet();
		}

		if (!result.isExecutionAllowed() &&
			PortletContainerUtil.isRuntimePortlet(request)) {

			result.setExecutingRuntimePortlet();
		}

		if (!result.isExecutionAllowed() && isPortletOnPage(request, portlet)) {
			result.setExecutingPortletOnPage();
		}

		if (!result.isExecutionAllowed() &&
			isGrantedByPPAUTH(request, portlet)) {

			result.setExecutingOnDemandPortlet();
		}

		if (result.isExecutionAllowed()) {
			PortalUtil.addPortletDefaultResource(request, portlet);

			if (hasAccessPermission(request, portlet)) {
				result.setHasPermissions();
			}
		}
	}

	protected boolean isGrantedByPPAUTH(
		HttpServletRequest request, Portlet portlet) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletId = portlet.getPortletId();

		if (!portlet.isAddDefaultResource()) {
			return false;
		}

		if (!PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED) {
			return true;
		}

		if (_portletAddDefaultResourceCheckWhitelist.contains(portletId)) {
			return true;
		}

		String namespace = PortalUtil.getPortletNamespace(portletId);

		String strutsAction = ParamUtil.getString(
			request, namespace + "struts_action");

		if (Validator.isNull(strutsAction)) {
			strutsAction = ParamUtil.getString(request, "struts_action");
		}

		if (_portletAddDefaultResourceCheckWhitelistActions.contains(
				strutsAction)) {

			return true;
		}

		String requestPortletAuthenticationToken = ParamUtil.getString(
			request, "p_p_auth");

		if (Validator.isNull(requestPortletAuthenticationToken)) {
			HttpServletRequest originalRequest =
				PortalUtil.getOriginalServletRequest(request);

			requestPortletAuthenticationToken = ParamUtil.getString(
				originalRequest, "p_p_auth");
		}

		if (Validator.isNotNull(requestPortletAuthenticationToken)) {
			String actualPortletAuthenticationToken = AuthTokenUtil.getToken(
				request, themeDisplay.getPlid(), portletId);

			if (requestPortletAuthenticationToken.equals(
					actualPortletAuthenticationToken)) {

				return true;
			}
		}

		return false;
	}

	protected boolean isLayoutConfigurationAllowed(
			HttpServletRequest request, Portlet portlet)
		throws PortalException, SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return false;
		}

		Layout layout = themeDisplay.getLayout();

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		Group group = layout.getGroup();

		if (group.isSite()) {
			if (LayoutPermissionUtil.contains(
					permissionChecker, layout, ActionKeys.CUSTOMIZE) ||
				LayoutPermissionUtil.contains(
					permissionChecker, layout, ActionKeys.UPDATE)) {

				return true;
			}
		}

		if (group.isCompany()) {
			if (permissionChecker.isCompanyAdmin()) {
				return true;
			}
		}
		else if (group.isLayoutPrototype()) {
			long layoutPrototypeId = group.getClassPK();

			if (LayoutPrototypePermissionUtil.contains(
					permissionChecker, layoutPrototypeId,
				ActionKeys.UPDATE)) {

				return true;
			}
		}
		else if (group.isLayoutSetPrototype()) {
			long layoutSetPrototypeId = group.getClassPK();

			if (LayoutSetPrototypePermissionUtil.contains(
					permissionChecker, layoutSetPrototypeId,
					ActionKeys.UPDATE)) {

				return true;
			}
		}
		else if (group.isOrganization()) {
			long organizationId = group.getOrganizationId();

			if (OrganizationPermissionUtil.contains(
					permissionChecker, organizationId, ActionKeys.UPDATE)) {

				return true;
			}
		}
		else if (group.isUserGroup()) {
			long scopeGroupId = themeDisplay.getScopeGroupId();

			if (GroupPermissionUtil.contains(
					permissionChecker, scopeGroupId, ActionKeys.UPDATE)) {

				return true;
			}
		}
		else if (group.isUser()) {
			return true;
		}

		return false;
	}

	protected boolean isPanelSelectedPortlet(
		ThemeDisplay themeDisplay, String portletId) {

		Layout layout = themeDisplay.getLayout();

		String panelSelectedPortlets = layout.getTypeSettingsProperty(
			"panelSelectedPortlets");

		if (Validator.isNotNull(panelSelectedPortlets)) {
			String[] panelSelectedPortletsArray = StringUtil.split(
				panelSelectedPortlets);

			return ArrayUtil.contains(panelSelectedPortletsArray, portletId);
		}

		return false;
	}

	protected boolean isPortletOnPage(
			HttpServletRequest request, Portlet portlet)
		throws PortalException, SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();
		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		String portletId = portlet.getPortletId();

		if (layout.isTypePanel() &&
				isPanelSelectedPortlet(themeDisplay, portletId)) {

			return true;
		}

		if ((layoutTypePortlet != null) &&
			layoutTypePortlet.hasPortletId(portletId)) {

			return true;
		}

		return false;
	}

	private boolean hasAccessPermission(
			HttpServletRequest request, Portlet portlet)
		throws PortalException, SystemException {

		PortletMode portletMode = PortletModeFactory.getPortletMode(
			ParamUtil.getString(request, "p_p_mode"));

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);
		long scopeGroupId = themeDisplay.getScopeGroupId();
		boolean access = PortletPermissionUtil.hasAccessPermission(
			permissionChecker, scopeGroupId, layout, portlet, portletMode);

		return access;
	}

	private boolean hasControlPanelAccessPermission(
			HttpServletRequest request, Portlet portlet)
		throws PortalException, SystemException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		long scopeGroupId = themeDisplay.getScopeGroupId();

		if (PortletPermissionUtil.hasControlPanelAccessPermission(
				permissionChecker, scopeGroupId, portlet)) {

			return true;
		}

		return false;
	}

	private static Log _log = LogFactoryUtil.getLog(
		PortletContainerSecurityImpl.class);

	private Set<String> _portletAddDefaultResourceCheckWhitelist;
	private Set<String> _portletAddDefaultResourceCheckWhitelistActions;

}
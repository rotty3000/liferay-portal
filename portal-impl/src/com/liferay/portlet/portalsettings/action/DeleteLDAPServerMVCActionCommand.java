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

package com.liferay.portlet.portalsettings.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.security.ldap.LDAPSettingsUtil;
import com.liferay.portal.service.CompanyServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

/**
 * @author Philip Jones
 */
@OSGiBeanProperties(
	property = {
		"javax.portlet.name=" + PortletKeys.PORTAL_SETTINGS,
		"mvc.command.name=/portal_settings/delete_ldap_server"
	}
)
public class DeleteLDAPServerMVCActionCommand extends BaseMVCActionCommand {

	public void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ldapServerId = ParamUtil.getLong(actionRequest, "ldapServerId");

		// Remove portletPreferences

		String postfix = LDAPSettingsUtil.getPropertyPostfix(ldapServerId);

		String[] keys = new String[LDAPKeys.KEYS.length];

		for (int i = 0; i < LDAPKeys.KEYS.length; i++) {
			keys[i] = LDAPKeys.KEYS[i] + postfix;
		}

		CompanyServiceUtil.removePreferences(themeDisplay.getCompanyId(), keys);

		// Update portletPreferences

		PortletPreferences portletPreferences = PrefsPropsUtil.getPreferences(
			themeDisplay.getCompanyId(), true);

		UnicodeProperties properties = new UnicodeProperties();

		String ldapServerIds = portletPreferences.getValue(
			"ldap.server.ids", StringPool.BLANK);

		ldapServerIds = StringUtil.removeFromList(
			ldapServerIds, String.valueOf(ldapServerId));

		properties.put("ldap.server.ids", ldapServerIds);

		CompanyServiceUtil.updatePreferences(
			themeDisplay.getCompanyId(), properties);
	}

}
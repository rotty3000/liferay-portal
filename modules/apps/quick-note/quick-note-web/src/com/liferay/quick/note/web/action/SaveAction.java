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

package com.liferay.quick.note.web.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.ActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alexander Chow
 * @author Peter Fellwock
 */

@Component(
	immediate = true,
	property = {
		"action.command.name=save",
		"javax.portlet.name=com_liferay_quick_note_web_portlet_QuickNotePortlet"
	},
	service = ActionCommand.class
)
public class SaveAction implements ActionCommand {

		@Override
		public boolean processCommand(
				PortletRequest portletRequest, PortletResponse portletResponse)
			throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletId = ParamUtil.getString(portletRequest, "portletId");

		try {
			PortletPermissionUtil.check(
				themeDisplay.getPermissionChecker(), themeDisplay.getLayout(),
				portletId, ActionKeys.CONFIGURATION);
		}
		catch (Exception e) {
			_log.error(e);
			throw new PortletException(e);
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				themeDisplay.getLayout(), portletId);

		String color = ParamUtil.getString(portletRequest, "color");
		String data = ParamUtil.getString(portletRequest, "data");

		if (Validator.isNotNull(color)) {
			portletPreferences.setValue("color", color);
		}

		if (Validator.isNotNull(data)) {
			portletPreferences.setValue("data", data);
		}

		try {
			portletPreferences.store();
		}
		catch (IOException ioe) {
			_log.error("Unable to store portlet preference", ioe);
		}

		return true;
	}

	private static Log _log = LogFactoryUtil.getLog(SaveAction.class);

}
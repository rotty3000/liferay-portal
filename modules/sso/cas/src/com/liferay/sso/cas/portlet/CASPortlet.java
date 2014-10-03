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
package com.liferay.sso.cas.portlet;

import com.liferay.sso.cas.autologin.CASAutoLogin;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.control-panel-entry-category=configuration", //auth.sso",
		"com.liferay.portlet.control-panel-entry-weight=5.0",
		"com.liferay.portlet.css-class-wrapper=portlet-cas",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.struts-path=dictionary",
		"javax.portlet.display-name=CAS SSO Portlet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/cas.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=admin"
	},
	service = Portlet.class
)
public class CASPortlet extends MVCPortlet {

	public void saveSettings(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {


	}

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute("casAutoLogin", _casAutoLogin);

		super.doView(renderRequest, renderResponse);
	}

	@Reference
	public void setCasAutoLogin(CASAutoLogin casAutoLogin) {
		_casAutoLogin = casAutoLogin;
	}

	public void unsetCasAutoLogin() {
		_casAutoLogin = null;
	}

	private CASAutoLogin _casAutoLogin;

}
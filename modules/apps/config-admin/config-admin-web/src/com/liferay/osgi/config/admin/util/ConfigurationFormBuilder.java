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

package com.liferay.osgi.config.admin.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.io.DDMFormJSONSerializerUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldRenderingContext;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormRendererUtil;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.metatype.ObjectClassDefinition;
public class ConfigurationFormBuilder {

	public ConfigurationFormBuilder(
		ObjectClassDefinition objectClassDefinition,
		ConfigurationAdmin configurationAdmin) {

		_objectClassDefinition = objectClassDefinition;
		_configurationAdmin = configurationAdmin;
	}

	public String renderServiceConfigurationForm(
		String servicePID, PortletRequest portletRequest,
		PortletResponse portletResponse) throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Configuration configuration = _getConfiguration(servicePID);

		DDMForm ddmForm = MetaTypeFormUtil.attributeForm(
			_objectClassDefinition, configuration);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			PortalUtil.getHttpServletRequest(portletRequest));

		ddmFormFieldRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(portletResponse));

		ddmFormFieldRenderingContext.setPortletNamespace(
			portletResponse.getNamespace());

		ddmFormFieldRenderingContext.setLocale(themeDisplay.getLocale());

		String configFieldJSON = DDMFormJSONSerializerUtil.serialize(ddmForm);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"DDMForm: " + configFieldJSON);
		}

		portletRequest.setAttribute("configFieldJSON", configFieldJSON);

		portletRequest.setAttribute("scopeGroupId",
			themeDisplay.getScopeGroupId());

		portletRequest.setAttribute("plId",
			themeDisplay.getPlid());

		return DDMFormRendererUtil.render(
			ddmForm, ddmFormFieldRenderingContext);
	}

	private Configuration _getConfiguration(String servicePID) {
		StringBuilder filter = new StringBuilder();
		filter.append("(");
		filter.append(Constants.SERVICE_PID);
		filter.append("=");
		filter.append(servicePID);
		filter.append(")");

		Configuration configuration = null;

		try {
			Configuration[] configurations =
							_configurationAdmin.listConfigurations(
								filter.toString());

			//Check if there exists some configuration

			if (configurations!= null && configurations.length >0) {
				configuration = configurations[0];
			}
		}
		catch (IOException e) {
			_log.error(e);
		}
		catch (InvalidSyntaxException e) {
			_log.error(e);
		}

		return configuration;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationFormBuilder.class);

	private final ConfigurationAdmin _configurationAdmin;
	private final ObjectClassDefinition _objectClassDefinition;

}
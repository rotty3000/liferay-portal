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

package com.liferay.osgi.config.admin.portlet.action;

import com.liferay.osgi.config.admin.util.MetaTypeInfoUtil;
import com.liferay.osgi.config.admin.util.ObjectClassDefinitonsIterator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.ActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormField;
import com.liferay.portlet.dynamicdatamapping.storage.FieldConstants;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 */

@Component(
	immediate = true, service = ActionCommand.class,
	property = {
		"action.command.name=bindAttributes",
		"javax.portlet.name=com_liferay_osgi_config_admin_portlet_" +
			"ConfigAdminPortlet"
	}
)
public class BindAttributesAction implements ActionCommand {

	@Override
	public boolean processCommand(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException {

		String servicePID = ParamUtil.getString(portletRequest, "servicePID");

		if (_log.isDebugEnabled()) {
			_log.debug("Saving service with PID:" + servicePID);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		ObjectClassDefinitonsIterator objectClassDefinitonsIterator =
			new ObjectClassDefinitonsIterator(
				_bundleContext, _metaTypeService,
				themeDisplay.getLanguageId());

		ObjectClassDefinition objectClassDefinition =
			objectClassDefinitonsIterator.getObjectClassDefinition(servicePID);

		DDMForm ddmForm = MetaTypeInfoUtil.attributeForm(objectClassDefinition);

		Dictionary<String, Object> properties =
			new Hashtable<String, Object>();

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			String id = ddmFormField.getName();

			String paramName = _ddmFormFieldParamName (portletRequest, id);

			Object paramValue = _toTypedParamValue(
				ddmFormField, portletRequest, paramName);

			if (paramValue != null) {
				properties.put(id, paramValue);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Service Properties to be updated:" + properties);
		}

		if (!properties.isEmpty()) {
			try {
				Configuration configuration =
					_configurationAdmin.getConfiguration(servicePID, "?");

				if (configuration != null) {
					configuration.update(properties);
				}
			}
			catch (IOException e) {
				_log.error(e);
			}
		}

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Reference
	protected void setConfigAdminService(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Reference
	protected void setMetaTypeService(MetaTypeService metaTypeService) {
		_metaTypeService = metaTypeService;
	}

	private static Object _toTypedParamValue(
		DDMFormField ddmFormField, PortletRequest portletRequest,
		String paramName) {

		boolean isRepeatable = ddmFormField.isRepeatable();

		String type = ddmFormField.getDataType();

		Object paramValue = null;

		if (FieldConstants.BOOLEAN == type) {
			if (isRepeatable) {
				paramValue = ParamUtil.getBooleanValues(
					portletRequest, paramName);
			}
			else {
				paramValue = ParamUtil.getBoolean(portletRequest, paramName);
			}
		}
		else {
			if (isRepeatable) {
				paramValue = ParamUtil.getBooleanValues(
					portletRequest, paramName);
			}
			else {
				paramValue = ParamUtil.getBoolean(portletRequest, paramName);
			}
		}

		return paramValue;
	}

	private String _ddmFormFieldParamName(
		PortletRequest portletRequest, String id) {

		Enumeration<String> paramNames = portletRequest.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();

			if (StringUtil.matches(paramName, id)) {
				return paramName;
			}
		}

		return null;
	}

	private static Log _log = LogFactoryUtil.getLog(BindAttributesAction.class);

	private BundleContext _bundleContext;
	private ConfigurationAdmin _configurationAdmin;
	private MetaTypeService _metaTypeService;

}
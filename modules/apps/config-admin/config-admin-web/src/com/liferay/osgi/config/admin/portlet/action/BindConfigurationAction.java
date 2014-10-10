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

import com.liferay.osgi.config.admin.util.ObjectClassDefinitionsHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.ActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Enumeration;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
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
		"action.command.name=bindConfiguration",
		"javax.portlet.name=com_liferay_osgi_config_admin_portlet_" +
		"ConfigAdminPortlet"
	}
)
public class BindConfigurationAction implements ActionCommand {

	@Override
	public boolean processCommand(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		ObjectClassDefinitionsHelper ocdHelper =
						new ObjectClassDefinitionsHelper(
							_bundleContext, languageId);

		String pid = ParamUtil.getString(portletRequest, "pid");

		String factoryPid = ParamUtil.getString(portletRequest, "factoryPid");

		boolean createFactoryConfig = false;

		//New factory configuration instance

		if (Validator.isNull(pid) && Validator.isNotNull(factoryPid)) {
			createFactoryConfig = true;
			pid = factoryPid;
		} // updating existing factory instance
		else if (Validator.isNotNull(pid)&&
				 Validator.isNotNull(factoryPid) &&
				 !StringUtil.equalsIgnoreCase(pid, factoryPid)) {

			createFactoryConfig = false;
		}

		String ddmFormfieldNamespace = ParamUtil.getString(
			portletRequest, "fieldNamespace");

		if (_log.isDebugEnabled()) {
			_log.debug("Binding attributes for service:"+pid);
		}

		ObjectClassDefinition objectClassDefinition = null;

		/*
		 * When instance pid and factoryPid are there then get OCD
		 * from factoryPid
		 */
		if (Validator.isNotNull(factoryPid)) {
			objectClassDefinition = ocdHelper.getObjectClassDefinition(
							factoryPid);
		}
		else {
			objectClassDefinition = ocdHelper.getObjectClassDefinition(pid);
		}

		Dictionary<String, Object> bindProperties =
						AttributeActionUtil.bindPropertiesFromRequest(
							portletRequest, objectClassDefinition,
							ddmFormfieldNamespace);

		bindProperties.put(Constants.SERVICE_PID, pid);

		if (Validator.isNotNull(factoryPid)) {
			bindProperties.put(
				ConfigurationAdmin.SERVICE_FACTORYPID, factoryPid);
		}

		String configuredPid = configureTargetService(
							pid, createFactoryConfig, bindProperties);

		//TODO Just a sample message placeholder need to more perfect
		SessionMessages.add(
			portletRequest, "configurationSuccessful",configuredPid);

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	protected String configureTargetService(
		String pid, boolean createFactoryConfig,
		Dictionary<String, Object> properties) throws PortletException {

		if (_log.isDebugEnabled()) {
			_log.debug("Props to be bound:"+properties);
		}

		try {
			Configuration configuration = null;

			if (createFactoryConfig) {
				if (_log.isDebugEnabled()) {
					_log.debug("Creating factory pid");
				}

				configuration = _configurationAdmin.createFactoryConfiguration(
									pid, null);
			}
			else {
				configuration = _configurationAdmin.getConfiguration(pid, null);

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Config Properties:"+ configuration.getProperties());
				}

				Dictionary<String, Object> configuredProperties =
								configuration.getProperties();

				if (configuredProperties != null) {
					Enumeration<String> keys = configuredProperties.keys();

					while (keys.hasMoreElements()) {
						String key = keys.nextElement();

						Object value = properties.get(key);

						configuredProperties.put(key, value);
					}

					configuration.update(configuredProperties);

					return configuration.getPid();
				}
			}

			if (configuration!= null) {
				configuration.update(properties);
				return configuration.getPid();
			}
		}
		catch (IOException e) {
			_log.error(e);
			throw new PortletException(e);
		}

		return null;
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

	protected BundleContext _bundleContext;
	protected ConfigurationAdmin _configurationAdmin;
	protected MetaTypeService _metaTypeService;

	private static Log _log = LogFactoryUtil.getLog(
		BindConfigurationAction.class);

}
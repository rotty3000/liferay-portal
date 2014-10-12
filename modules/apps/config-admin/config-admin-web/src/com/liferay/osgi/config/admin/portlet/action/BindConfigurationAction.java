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

import com.liferay.osgi.config.admin.util.ObjectClassDefinitionsIterator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.ActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
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

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public boolean processCommand(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException {

		ThemeDisplay themeDisplay  =
						(ThemeDisplay)portletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		String servicePID = ParamUtil.getString(
			portletRequest, "servicePID");

		String ddmFormfieldNamespace = ParamUtil.getString(
			portletRequest, "fieldNamespace");

		if(_log.isDebugEnabled()){
			_log.debug("Binding attributes for service:"+servicePID);
		}

		ObjectClassDefinitionsIterator objectClassDefinitionsIterator =
						new ObjectClassDefinitionsIterator(
							_bundleContext, _metaTypeService, languageId);

		ObjectClassDefinition objectClassDefinition =
						objectClassDefinitionsIterator.getObjectClassDefinition(
							servicePID);

		Dictionary<String, Object> bindProperties =
						AttributeActionUtil.bindPropertiesFromRequest(
							portletRequest, objectClassDefinition,
							ddmFormfieldNamespace);

		bindProperties.put(Constants.SERVICE_PID, servicePID);

		configureTargetService(servicePID,bindProperties);

		return true;
	}

	protected void configureTargetService(String servicePID,
		Dictionary<String, Object> properties) 	throws PortletException {

		if(_log.isDebugEnabled()){
			_log.debug("Props to be bound:"+properties);
		}

		try {

			//TODO handle Factory PIDS
			Configuration configuration =
							_configurationAdmin.getConfiguration(
								servicePID,null);

			if(_log.isDebugEnabled()){

				_log.debug("Config Properties:"+configuration.getProperties());
			}

			Dictionary<String, Object> existingConfigProps =
							configuration.getProperties();

			if(existingConfigProps == null){

				configuration.update(properties);
			}
			else{

				Enumeration<String> keys =  existingConfigProps.keys();

				while(keys.hasMoreElements()){

					String key = keys.nextElement();

					Object value = properties.get(key);

					existingConfigProps.put(key, value);
				}

				configuration.update(existingConfigProps);
			}
		}
		catch (IOException e) {
			_log.error(e);
			throw new PortletException(e);
		}
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

	private String _extractBundleLocation(String targetPID){

		return StringUtil.extractLast(targetPID, StringPool.PIPE);
	}

	private static Log _log = LogFactoryUtil.getLog(BindConfigurationAction.class);

	protected BundleContext _bundleContext;
	protected ConfigurationAdmin _configurationAdmin;
	protected MetaTypeService _metaTypeService;


}
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

package com.liferay.portal.soap.extender.configuration;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(immediate = true)
public class DefaultConfigurationCreator {

	public static final String CXF_FACTORY_PID =
		"com.liferay.portal.cxf.common.configuration." +
			"CXFEndpointPublisherConfiguration";

	@Reference
	public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	@Activate
	protected void activate() throws InvalidSyntaxException, IOException {
		Dictionary<String, Object> properties;

		Configuration[] existingConfigurations =
			_configurationAdmin.listConfigurations(
				"(service.factoryPid=" + SoapExtenderConfiguration.CONFIG_PID +
					")");

		if (ArrayUtil.isEmpty(existingConfigurations)) {
			_soapConfiguration = _configurationAdmin.createFactoryConfiguration(
				SoapExtenderConfiguration.CONFIG_PID, null);

			properties = new Hashtable<>();

			properties.put("contextPaths", new String[] {"/soap"});
			properties.put(
				"jaxWsServiceFilterStrings",
				new String[] {"(jaxws.endpoint=true)"});

			_soapConfiguration.update(properties);

			Configuration jaxWsApiConfiguration =
				_configurationAdmin.getConfiguration(
					JaxWsApiConfiguration.CONFIG_PID);

			properties = new Hashtable<>();

			properties.put("contextPath", "/soap");

			jaxWsApiConfiguration.update(properties);
		}
		else {
			//Do not take any further action if a configuration already exists
			return;
		}

		existingConfigurations = _configurationAdmin.listConfigurations(
			"(&(service.factoryPid=" + CXF_FACTORY_PID + ")" +
				"(contextPath=/soap))");

		if (ArrayUtil.isEmpty(existingConfigurations)) {
			_cxfConfiguration = _configurationAdmin.createFactoryConfiguration(
				CXF_FACTORY_PID, null);

			properties = new Hashtable<>();

			properties.put("contextPath", "/soap");

			_cxfConfiguration.update(properties);
		}
	}

	private ConfigurationAdmin _configurationAdmin;
	private Configuration _cxfConfiguration;
	private Configuration _soapConfiguration;

}
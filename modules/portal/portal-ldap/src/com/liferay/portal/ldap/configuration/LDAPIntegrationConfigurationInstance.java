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

package com.liferay.portal.ldap.configuration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.liferay.portal.configuration.PKConfiguration;
import com.liferay.portal.configuration.PKConfigurationConstants;

import aQute.bnd.annotation.metatype.Configurable;

/**
 * 
 * @author Milen Dyankov
 *
 */
@Component (
	configurationPid = "com.liferay.portal.ldap.configuration.LDAPIntegrationConfiguration",
	immediate = true,
	service = LDAPIntegrationConfigurationInstance.class)
public class LDAPIntegrationConfigurationInstance extends PKConfiguration {

	
	public LDAPIntegrationConfigurationInstance() {
		super(PKConfigurationConstants.COMPANY_ID);
	}
	
	public LDAPIntegrationConfiguration getConfiguration() {
		return _ldapConfiguration;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		buildKey(properties);
		_ldapConfiguration = Configurable.createConfigurable(
			LDAPIntegrationConfiguration.class, properties);
	}
	
	private volatile LDAPIntegrationConfiguration _ldapConfiguration;

}

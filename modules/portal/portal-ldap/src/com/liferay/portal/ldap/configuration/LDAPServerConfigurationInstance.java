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

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.portal.configuration.PKConfiguration;
import com.liferay.portal.configuration.PKConfigurationConstants;

/**
 * 
 * @author Milen Dyankov
 *
 */
@Component (
	configurationPid = "com.liferay.portal.ldap.configuration.LDAPServerConfiguration",
	immediate = true,
	service = LDAPServerConfigurationInstance.class)
public class LDAPServerConfigurationInstance extends PKConfiguration {

	
	public LDAPServerConfigurationInstance() {
		super(PKConfigurationConstants.COMPANY_ID, PKConfigurationConstants.LDAP_SERVER_ID);
	}
	
	public LDAPServerConfiguration getConfiguration() {
		return _ldapConfiguration;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		buildKey(properties);
		_ldapConfiguration = Configurable.createConfigurable(
			LDAPServerConfiguration.class, properties);
	}
	
	private volatile LDAPServerConfiguration _ldapConfiguration;

}

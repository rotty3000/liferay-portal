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

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.liferay.portal.configuration.PKConfiguration.PK;
import com.liferay.portal.configuration.PKConfigurationConstants;
import com.liferay.portal.configuration.PKConfigurationRegistry;


/**
 * @author Milen Dyankov
 */
@Component(
	immediate = true, 
	service = {LDAPServerConfigurationRegistry.class})
public class LDAPServerConfigurationRegistry extends PKConfigurationRegistry<LDAPServerConfigurationInstance> {


	public LDAPServerConfiguration getConfiguration(long companyId, long serverId) {
		Map<String, String> pk = new HashMap<String, String>();
		pk.put(PKConfigurationConstants.COMPANY_ID, String.valueOf(companyId));
		pk.put(PKConfigurationConstants.LDAP_SERVER_ID, String.valueOf(serverId));
		LDAPServerConfigurationInstance configurationInstance = super.getConfiguration(new PK(pk));
		if (configurationInstance == null) {
			return _defaultConfig.getConfiguration();
		}
		return configurationInstance.getConfiguration();
	}
	
	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC)
	protected void addConfiguration(LDAPServerConfigurationInstance configurationInstance) {
		super.addConfiguration(configurationInstance);
	}


	protected void removeConfiguration(LDAPServerConfigurationInstance configurationInstance) {
		super.removeConfigurationInstance(configurationInstance);
	}


	protected void updatedConfiguration(LDAPServerConfigurationInstance configurationInstance) {
		super.updatedConfigurationInstance(configurationInstance);
	}
	
	@Override
	public void setDefault(LDAPServerConfigurationInstance defaultConfig) {
		this._defaultConfig = defaultConfig;
	}
	
	@Override
	protected LDAPServerConfigurationValidator getValidator() {
		return _validator;
	}

	private LDAPServerConfigurationInstance _defaultConfig;
	private LDAPServerConfigurationValidator _validator = new LDAPServerConfigurationValidator();
	

}

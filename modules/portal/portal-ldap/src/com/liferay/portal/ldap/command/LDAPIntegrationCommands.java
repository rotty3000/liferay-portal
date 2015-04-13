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

package com.liferay.portal.ldap.command;

import static com.liferay.portal.configuration.PKConfigurationConstants.COMPANY_ID;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.configuration.PKConfiguration.PK;
import com.liferay.portal.ldap.configuration.LDAPIntegrationConfiguration;
import com.liferay.portal.ldap.configuration.LDAPIntegrationConfigurationInstance;
import com.liferay.portal.ldap.configuration.LDAPIntegrationConfigurationRegistry;

/**
 * @author Milen Dyankov
 */
@Component(immediate = true, service = Object.class, property = {
	"osgi.command.function=ldapActiveCompanyConfigs", 
	"osgi.command.function=ldapAllCompanyConfigs",
	"osgi.command.function=ldapCompanyConfigValue", 
	"osgi.command.scope=liferay"
})
public class LDAPIntegrationCommands {

	public void ldapActiveCompanyConfigs() {

		Set<PK> keys = _ldapIntegrationConfigurationRegistry.getKeys();
		System.out.format("%-7s | %s\n", "=======", "==================================================");
		System.out.format("%-7s | %s\n", "COMPANY", "CONFIGURATION PID");
		System.out.format("%-7s | %s\n", "=======", "==================================================");
		for (PK key : keys) {
			String pid = _ldapIntegrationConfigurationRegistry.getConfiguration(key).getPid();
			System.out.format("%-7s | %s\n", key.get(COMPANY_ID), pid);
		}
	}

	public void ldapAllCompanyConfigs() {

		try {
			Configuration[] allConfigs;
			allConfigs =
				_configurationAdmin.listConfigurations("(service.factoryPid=com.liferay.portal.ldap.configuration.LDAPIntegrationConfiguration)");
			Map<String, Set<String>> allPids = new HashMap<String, Set<String>>();

			if (allConfigs != null) {
				for (int i = 0; i < allConfigs.length; i++) {
					String companyId = (String) allConfigs[i].getProperties().get(COMPANY_ID);
					Set<String> pids = allPids.get(companyId);
					if (pids == null) {
						pids = new HashSet<String>();
						allPids.put(companyId, pids);
					}
					pids.add(allConfigs[i].getPid());
				}
			}

			Set<PK> keys = _ldapIntegrationConfigurationRegistry.getKeys();
			System.out.format(
				"%-7s | %7s | %s\n", "=======", "=======", "==================================================");
			System.out.format("%-7s | %7s | %s\n", "COMPANY", "STATUS", "CONFIGURATION PID");
			System.out.format(
				"%-7s | %7s | %s\n", "=======", "=======", "==================================================");
			for (PK key : keys) {
				String companyId = key.get(COMPANY_ID);
				Set<LDAPIntegrationConfigurationInstance> configs =
					_ldapIntegrationConfigurationRegistry.getAllConfigs(key);
				LDAPIntegrationConfigurationInstance conf = _ldapIntegrationConfigurationRegistry.getConfiguration(key);
				String activePid = conf == null ? null : conf.getPid();
				for (LDAPIntegrationConfigurationInstance ldapIntegrationConfiguration : configs) {
					String pid = ldapIntegrationConfiguration.getPid();
					String active = ldapIntegrationConfiguration.getPid().equals(activePid) ? "IN USE" : "";
					Set<String> pids = allPids.get(companyId);
					if (pids != null) pids.remove(pid);
					System.out.format("%-7s | %7s | %s\n", companyId, active, pid);
				}
				System.out.format(
					"%-7s | %7s | %s\n", "-------", "-------", "--------------------------------------------------");
			}

			for (String companyId : allPids.keySet()) {
				Set<String> pids = allPids.get(companyId);
				if (pids != null && !pids.isEmpty()) {
					for (String pid : pids) {
						System.out.format("%-7s | %7s | %s\n", companyId, "INVALID", pid);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void ldapCompanyConfigValue(int companyId, String key) {

		LDAPIntegrationConfiguration config = _ldapIntegrationConfigurationRegistry.getConfiguration(companyId);
		String result;
		try {
			Method m = config.getClass().getMethod(key);
			result = String.valueOf(m.invoke(config));
		}
		catch (Exception e) {
			result = e.getMessage();
		}
		System.out.println(result);
	}

	@Reference
	protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Reference
	protected void setLDAPIntegrationConfigurationRegistry(LDAPIntegrationConfigurationRegistry configurationRegistry) {

		_ldapIntegrationConfigurationRegistry = configurationRegistry;
	}

	private volatile ConfigurationAdmin _configurationAdmin;
	private volatile LDAPIntegrationConfigurationRegistry _ldapIntegrationConfigurationRegistry;
}

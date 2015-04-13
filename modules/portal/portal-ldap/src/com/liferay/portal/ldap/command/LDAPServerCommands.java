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

import static com.liferay.portal.configuration.PKConfigurationConstants.*;

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
import com.liferay.portal.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.ldap.configuration.LDAPServerConfigurationInstance;
import com.liferay.portal.ldap.configuration.LDAPServerConfigurationRegistry;

/**
 * @author Milen Dyankov
 */
@Component(immediate = true, service = Object.class, property = {
	"osgi.command.function=ldapActiveServerConfigs", "osgi.command.function=ldapAllServerConfigs",
	"osgi.command.function=ldapServerConfigValue", "osgi.command.scope=liferay"
})
public class LDAPServerCommands {

	public void ldapActiveServerConfigs() {

		Set<PK> keys = _ldapServerConfigurationRegistry.getKeys();
		System.out.println();
		System.out.format("%-13s | %s\n", "=============", "==================================================");
		System.out.format("%-7s / %3s | %s\n", "COMPANY", "SVR", "CONFIGURATION PID");
		System.out.format("%-13s | %s\n", "=============", "==================================================");
		for (PK key : keys) {
			System.out.format(
				"%-7s / %3s | %s\n", key.get(COMPANY_ID), key.get(LDAP_SERVER_ID),
				_ldapServerConfigurationRegistry.getConfiguration(key).getPid());
			System.out.format("%-13s | %s\n", "-------------", "--------------------------------------------------");
		}
	}

	public void ldapAllServerConfigs() {

		try {
			Configuration[] allConfigs;
			allConfigs =
				_configurationAdmin.listConfigurations("(service.factoryPid=com.liferay.portal.ldap.configuration.LDAPServerConfiguration)");

			Map<String, Map<String, Set<String>>> allPids = new HashMap<String, Map<String, Set<String>>>();
			if (allConfigs != null) {
				for (int i = 0; i < allConfigs.length; i++) {
					String comanyId = (String) allConfigs[i].getProperties().get(COMPANY_ID);
					Map<String, Set<String>> servers = allPids.get(comanyId);
					if (servers == null) {
						servers = new HashMap<String, Set<String>>();
						allPids.put(comanyId, servers);
					}
					String serverId = (String) allConfigs[i].getProperties().get(LDAP_SERVER_ID);
					Set<String> pids = servers.get(serverId);
					if (pids == null) {
						pids = new HashSet<String>();
						servers.put(serverId, pids);
					}
					pids.add(allConfigs[i].getPid());
				}
			}

			Set<PK> keys = _ldapServerConfigurationRegistry.getKeys();
			System.out.format(
				"%-13s | %7s | %s\n", "=============", "=======", "==================================================");
			System.out.format("%-7s / %3s | %7s | %s\n", "COMPANY", "SVR", "STATUS", "CONFIGURATION PID");
			System.out.format(
				"%-13s | %7s | %s\n", "=============", "=======", "==================================================");

			for (PK key : keys) {
				String companyId = key.get(COMPANY_ID);
				String serverId = key.get(LDAP_SERVER_ID);
				Set<LDAPServerConfigurationInstance> configs = _ldapServerConfigurationRegistry.getAllConfigs(key);
				String activePid = _ldapServerConfigurationRegistry.getConfiguration(key).getPid();
				for (LDAPServerConfigurationInstance ldapIntegrationConfiguration : configs) {
					String pid = ldapIntegrationConfiguration.getPid();
					String active = ldapIntegrationConfiguration.getPid().equals(activePid) ? "IN USE" : "";
					System.out.format("%-7s / %3s | %7s | %s\n", companyId, serverId, active, pid);
					allPids.get(companyId).get(serverId).remove(pid);
				}
				System.out.format(
					"%-13s | %7s | %s\n", "-------------", "-------",
					"--------------------------------------------------");
			}

			for (String companyId : allPids.keySet()) {
				Map<String, Set<String>> servers = allPids.get(companyId);
				for (String serverId : servers.keySet()) {
					Set<String> pids = servers.get(serverId);
					if (pids != null && !pids.isEmpty()) {
						for (String pid : pids) {
							System.out.format("%-7s / %3s | %7s | %s\n", companyId, serverId, "INVALID", pid);
						}
					}
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	public void ldapServerConfigValue(int companyId, int serverId, String key) {

		LDAPServerConfiguration config = _ldapServerConfigurationRegistry.getConfiguration(companyId, serverId);
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
	protected void setLDAPIntegrationConfigurationRegistry(LDAPServerConfigurationRegistry configurationRegistry) {

		_ldapServerConfigurationRegistry = configurationRegistry;
	}

	private volatile ConfigurationAdmin _configurationAdmin;
	private volatile LDAPServerConfigurationRegistry _ldapServerConfigurationRegistry;
}

## Task overview

 1. move LDAP configuration from portal.properties to ConfigAdmin 
 1. split configuration into 2 pieces 
	1. Portal instance (company) specific - 1 per company 
	1. Both portal instance (company) and LDAP instance specific - many per company
 1. replace current implementation to use the new configuration 
 1. provide migration/upgrade path 

## Assumptions

 1. UI will be taken care of separately 
 1. Configuration persistence will be taken care of separately 

## Design decisions

### Portal instance (company) specific configuration

 1. PID: `com.liferay.portal.ldap.configuration.LDAPIntegrationConfiguration`
 1. A OSGi service (let's say `LDAPIntegrationRegistry`) will keep track of all configurations (notified on create/modify/remove configuration)!
 1. All other components must access Company specific LDAP configuration properties exclusively through that service! 
 1. A configuration validation component (let's say `LDAPIntegrationConfigurationValidator`) will be able to validate the consistency and correctness of the configuration. In particular it will 
	1. report error if value for `companyId` property is missing in the configuration
	1. report warning if `companyId` refers to nonexistent instance 
	1. report warning if new configuration has the same `companyId` as existing one
 1. `LDAPIntegrationRegistry` will verify each configuration change it gets notified about against `LDAPIntegrationConfigurationValidator` and 
	1. log error/warning message for each error/warning
	1. ignore any new configuration for which validation reports error
	1. ignore existing configuration for which validation reports error after modification
 

### Portal instance (company) specific configuration

 1. PID: `com.liferay.portal.ldap.configuration.LDAPServerConfiguration`
 1. A OSGi service (let's say `LDAPServerRegistry`) will keep track of all configurations (notified on create/modify/remove configuration)!
 1. All other components must access LDAP server specific configuration properties exclusively through that service! 
 1. A configuration validation component (let's say `LDAPServerConfigurationValidator`) will be able to validate the consistency and correctness of the configuration. In particular it will 
	1. report error if value for `ldapServerId` property is missing in the configuration
	1. report warning if new configuration has the same `ldapServerId` as existing one
	1. report warning if any of the values (comma separated list) of `companyIds` refers to nonexistent instance 
 1. `LDAPServerRegistry` will verify each configuration change it gets notified about against `LDAPServerConfigurationValidator` and 
	1. log error/warning message for each error/warning
	1. ignore any new configuration for which validation reports error
	1. ignore existing configuration for which validation reports error after modification

### Shell commands

 **liferay:ldapActiveCompanyConfigs** - list active (in use by registry) configurations per company id. It will lists configurations even if there is no company with such id!
 
 **liferay:ldapAllCompanyConfigs** - list all configurations per company id and their status. It will lists configurations even if there is no company with such id! Status can be
 
  * IN USE - the `LDAPIntegrationConfiguration` currently being used for given company id! 
  * INVALID - the `LDAPIntegrationConfiguration` is ignored by the registry due to errors (invalid PK for example)! 
  * "" - the `LDAPIntegrationConfiguration` is tracked by the registry! It may be used if the one currently IN USE goes away! 

 **liferay:ldapCompanyConfigValue <companyId> <property>** - prints the value of the configuration `property` from active configuration for given `companyId`

 **liferay:ldapActiveServerConfigs**, **liferay:ldapAllServerConfigs** and **liferay:ldapServerConfigValue <companyId> <property>** do the exact same thing but for `LDAPServerConfiguration` (server specific configuration) 


## Known/discovered issues

 1. Config Admin UI can not handle list types ('optionValues' in metatype). **Temporary commented out those attributes in `LDAPIntegrationConfiguration`** 
 1. Deleting configuration from UI does not remove the `cfg` file (if such exists). Configuration will be restored when 
	1. Server is restarted with clean OSGi bundle cache
	1. `cfg` file is _touched_
 1. Config Admin UI can not handle array types (for example `isbns` in `AmazonRankingsConfiguration` or `hiddenVariables` in `IFrameConfiguration`) After first save from UI those are displayed as `[Ljava.lang.String;@3727c6c` and the configuration can not be saved anymore! **To work around that all fields in `LDAPServerConfiguration` as defined as `String` for now!**
 1. Sometimes updating configuration file does not trigger notifications **TODO: investigate why** 
 1. If more than one config with the same PK is available it is not clear which one will be picked by `ConfigAdmin` first (and thus become active in the registry) after restart!
 1. UI currently display configurations directly from ConfigAdmin. Thus it displays ALL configurations and it's not able to tell
	* which one is invalid (from PK perspective) - have to manually check one by one
	* which one is for which company / server - have to manually check one by one
	* which one is use (if there are many with the same PK) 

	The above mentioned gogo shell commands were created to overcome the issue. They use `PKConfigurationRegistry` instead of `ConfigAdmin` directly! 


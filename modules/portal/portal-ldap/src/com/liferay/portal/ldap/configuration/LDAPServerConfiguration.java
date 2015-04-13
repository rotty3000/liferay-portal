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

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Milen Dyankov
 */
@Meta.OCD(
	factory=true,
	id = "com.liferay.portal.ldap.configuration.LDAPServerConfiguration",
	localization = "content.Language"
)
public interface LDAPServerConfiguration {

	@Meta.AD(deflt = "-1", required = true)
	public int companyId();

	@Meta.AD(deflt = "0", required = true)
	public int ldapServerId();
	
	@Meta.AD(deflt = "(mail=@email_address@)", required = false)
	public String filter();

	@Meta.AD(deflt = "", required = false)
	public String contactCustomMappings();

	@Meta.AD(deflt = "aimSn=\nbirthday=\nfacebookSn=\nicqSn=\njabberSn=\njobTitle=\nmsnSn=\nmySpaceSn=\nskypeSn=\nsmsSn=\ntwitterSn=\nymSn=", required = false)
	public String contactMappings();

	@Meta.AD(deflt = "(objectClass=groupOfUniqueNames)", required = false)
	public String groupSearchFilter();
	
	@Meta.AD(deflt = "", required = false)
	public String groupMappings();

	@Meta.AD(deflt = "ou=groups,dc=example,dc=com", required = false)
	public String groupsDN();
	
	@Meta.AD(deflt = "", required = false)
	public String userCustomMappings();

	@Meta.AD(deflt = "emailAddress=mail\nfirstName=givenName\ngroup=groupMembership\njobTitle=title\nlastName=sn\npassword=userPassword\nscreenName=cn\nuuid=uuid\n", required = false)
	public String userMappings();
	
	@Meta.AD(deflt = "(objectClass=inetOrgPerson)", required = false)
	public String userSearchFilter();

	@Meta.AD(deflt = "ou=users,dc=example,dc=com", required = false)
	public String usersDN();

	@Meta.AD(deflt = "ldap://localhost:10389", required = false)
	public String baseProviderUrl();

	@Meta.AD(deflt = "dc=example,dc=com", required = false)
	public String baseDN();

	@Meta.AD(deflt = "uid=admin,ou=system", required = false)
	public String securityPrincipal();

	@Meta.AD(deflt = "secret", required = false)
	public String credentials();
}
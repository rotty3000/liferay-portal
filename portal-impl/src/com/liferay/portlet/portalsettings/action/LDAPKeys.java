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

package com.liferay.portlet.portalsettings.action;

import com.liferay.portal.kernel.util.PropsKeys;

/**
 * @author Philip Jones
 */
public class LDAPKeys {

	public static final String[] _KEYS = {
		PropsKeys.LDAP_AUTH_SEARCH_FILTER, PropsKeys.LDAP_BASE_DN,
		PropsKeys.LDAP_BASE_PROVIDER_URL,
		PropsKeys.LDAP_CONTACT_CUSTOM_MAPPINGS, PropsKeys.LDAP_CONTACT_MAPPINGS,
		PropsKeys.LDAP_GROUP_DEFAULT_OBJECT_CLASSES,
		PropsKeys.LDAP_GROUP_MAPPINGS, PropsKeys.LDAP_GROUPS_DN,
		PropsKeys.LDAP_IMPORT_GROUP_SEARCH_FILTER,
		PropsKeys.LDAP_IMPORT_USER_SEARCH_FILTER,
		PropsKeys.LDAP_SECURITY_CREDENTIALS, PropsKeys.LDAP_SECURITY_PRINCIPAL,
		PropsKeys.LDAP_SERVER_NAME, PropsKeys.LDAP_USER_CUSTOM_MAPPINGS,
		PropsKeys.LDAP_USER_DEFAULT_OBJECT_CLASSES,
		PropsKeys.LDAP_USER_MAPPINGS, PropsKeys.LDAP_USERS_DN
	};

}
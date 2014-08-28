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

package com.liferay.portal.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.LayoutPrototype;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.service.LayoutPrototypeServiceUtil;
import com.liferay.portal.service.PasswordPolicyLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class PasswordPolicyPermissionImpl implements PasswordPolicyPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, long passwordPolicyId,
			String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, passwordPolicyId, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long passwordPolicyId,
		String actionId) {

		PasswordPolicy passwordPolicy = null;

		if (passwordPolicyId != 0) {
			passwordPolicy = PasswordPolicyLocalServiceUtil.fetchPasswordPolicy(
				passwordPolicyId);
		}

		return contains(permissionChecker, passwordPolicy, actionId);
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, PasswordPolicy passwordPolicy,
			String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, passwordPolicy, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, PasswordPolicy passwordPolicy,
		String actionId) {

		long passwordPolicyId = 0;

		if (passwordPolicy != null) {
			passwordPolicyId = passwordPolicy.getPasswordPolicyId();
		}

		long companyGroupId = getCompanyGroupId(
			permissionChecker, passwordPolicy);

		return permissionChecker.hasPermission(
			companyGroupId, PasswordPolicy.class.getName(), passwordPolicyId,
			actionId);
	}


	protected long getCompanyGroupId(
		PermissionChecker permissionChecker, PasswordPolicy passwordPolicy) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (passwordPolicy == null) {
			return companyGroupId;
		}

		long companyId = passwordPolicy.getCompanyId();

		if (companyId == permissionChecker.getCompanyId()) {
			return companyGroupId;
		}

		try {
			Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
				companyId);

			return companyGroup.getGroupId();
		} catch (PortalException e) {
			_log.error(
				"Unable to load default group for company " + companyId, e);
		}

		return 0;
	}

	private static Log _log = LogFactoryUtil.getLog(
		PasswordPolicyPermissionImpl.class);
}
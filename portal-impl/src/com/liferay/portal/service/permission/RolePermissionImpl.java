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
import com.liferay.portal.model.Role;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;

/**
 * @author Charles May
 */
public class RolePermissionImpl implements RolePermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, long roleId, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, roleId, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, Role role, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, role, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long groupId, long roleId,
		String actionId) {

		Role role = null;

		if (roleId != 0) {
			role = RoleLocalServiceUtil.fetchRole(roleId);
		}

		return contains(permissionChecker, groupId, role, actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long groupId, Role role,
		String actionId) {

		if (groupId == 0) {
			groupId = getCompanyGroupId(permissionChecker, role);
		}

		long roleId = 0;

		if (role != null) {
			roleId = role.getRoleId();
		}

		return permissionChecker.hasPermission(
			groupId, Role.class.getName(), roleId, actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long roleId, String actionId) {

		return contains(permissionChecker, 0, roleId, actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, Role role, String actionId) {

		return contains(permissionChecker, 0, role, actionId);
	}

	protected long getCompanyGroupId(
		PermissionChecker permissionChecker, Role role) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (role == null) {
			return companyGroupId;
		}

		long companyId = role.getCompanyId();

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
		RolePermissionImpl.class);

}
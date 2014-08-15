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
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;

/**
 * @author Charles May
 */
public class UserGroupPermissionImpl implements UserGroupPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, long userGroupId,
			String actionId)
		throws PrincipalException {

		check(permissionChecker, userGroupId, actionId);
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, UserGroup userGroup,
			String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, userGroup, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long userGroupId,
		String actionId) {

		UserGroup userGroup = null;

		if (userGroupId != 0) {
			userGroup = UserGroupLocalServiceUtil.fetchUserGroup(userGroupId);
		}

		return contains(permissionChecker, userGroup, actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, UserGroup userGroup,
		String actionId) {

		long userGroupId = 0;

		if (userGroup != null) {
			userGroupId = userGroup.getUserGroupId();
		}

		long companyGroupId = getCompanyGroupId(permissionChecker, userGroup);

		return permissionChecker.hasPermission(
			companyGroupId, UserGroup.class.getName(), userGroupId, actionId);
	}

	protected long getCompanyGroupId(
		PermissionChecker permissionChecker, UserGroup userGroup) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (userGroup == null) {
			return companyGroupId;
		}

		long companyId = userGroup.getCompanyId();

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
		UserGroupPermissionImpl.class);
}
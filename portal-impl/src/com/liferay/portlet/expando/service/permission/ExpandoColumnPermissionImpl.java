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

package com.liferay.portlet.expando.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.permission.UserPermissionImpl;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;

/**
 * @author Raymond Augé
 */
public class ExpandoColumnPermissionImpl implements ExpandoColumnPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, ExpandoColumn column,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, column, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long columnId, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, columnId, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long companyId,
			String className, String tableName, String columnName,
			String actionId)
		throws PortalException {

		if (!contains(
				permissionChecker, companyId, className, tableName, columnName,
				actionId)) {

			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, ExpandoColumn column,
		String actionId) {

		long columnId = 0;

		if (column != null) {
			columnId = column.getColumnId();
		}

		long companyGroupId = getCompanyGroupId(
			permissionChecker, column);


		return permissionChecker.hasPermission(
			companyGroupId, ExpandoColumn.class.getName(), columnId, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long columnId, String actionId)
		throws PortalException {

		ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(
			columnId);

		return contains(permissionChecker, column, actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long companyId, String className,
		String tableName, String columnName, String actionId) {

		ExpandoColumn column = ExpandoColumnLocalServiceUtil.getColumn(
			companyId, className, tableName, columnName);

		return contains(permissionChecker, column, actionId);
	}

	protected long getCompanyGroupId(
		PermissionChecker permissionChecker, ExpandoColumn column) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (column == null) {
			return companyGroupId;
		}

		long companyId = column.getCompanyId();

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
		ExpandoColumnPermissionImpl.class);
}
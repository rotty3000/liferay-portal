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
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.service.LayoutPrototypeServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;

/**
 * @author Jorge Ferrer
 */
public class LayoutPrototypePermissionImpl
	implements LayoutPrototypePermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, long layoutPrototypeId,
			String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, layoutPrototypeId, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker,
			LayoutPrototype layoutPrototype, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, layoutPrototype, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long layoutPrototypeId,
		String actionId) {

		LayoutPrototype layoutPrototype = null;

		if (layoutPrototypeId != 0) {
			layoutPrototype =
				LayoutPrototypeLocalServiceUtil.fetchLayoutPrototype(
					layoutPrototypeId);
		}

		return contains(permissionChecker, layoutPrototype, actionId);
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, LayoutPrototype layoutPrototype,
		String actionId) {

		long layoutPrototypeId = 0;

		if (layoutPrototype != null) {
			layoutPrototypeId = layoutPrototype.getLayoutPrototypeId();
		}

		long companyGroupId = getCompanyGroupId(
			permissionChecker, layoutPrototype);

		if (permissionChecker.hasPermission(
				companyGroupId, LayoutPrototype.class.getName(),
				layoutPrototypeId, actionId)) {

			return true;
		}

		return false;
	}

	protected long getCompanyGroupId(
		PermissionChecker permissionChecker, LayoutPrototype layoutPrototype) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (layoutPrototype == null) {
			return companyGroupId;
		}

		long companyId = layoutPrototype.getCompanyId();

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
		LayoutPrototypePermissionImpl.class);
}
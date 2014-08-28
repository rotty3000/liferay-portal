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
import com.liferay.portal.model.LayoutSetPrototype;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.service.PasswordPolicyLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class LayoutSetPrototypePermissionImpl
	implements LayoutSetPrototypePermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, long layoutSetPrototypeId,
			String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, layoutSetPrototypeId, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, long layoutSetPrototypeId,
		String actionId) {

		LayoutSetPrototype layoutSetPrototype = null;

		if (layoutSetPrototypeId != 0) {
			layoutSetPrototype =
				LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(
					layoutSetPrototypeId);
		}

		return contains(permissionChecker, layoutSetPrototype, actionId);
	}


	@Override
	public void check(
			PermissionChecker permissionChecker,
			LayoutSetPrototype layoutSetPrototype, String actionId)
		throws PrincipalException {

		if (!contains(permissionChecker, layoutSetPrototype, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker,
		LayoutSetPrototype layoutSetPrototype, String actionId) {

		long layoutSetPrototypeId = 0;

		if (layoutSetPrototype != null) {
			layoutSetPrototypeId = layoutSetPrototype.getLayoutSetPrototypeId();
		}

		long companyGroupId = getCompanyGroupId(
			permissionChecker, layoutSetPrototype);

		if (permissionChecker.hasPermission(
			companyGroupId, LayoutSetPrototype.class.getName(),
			layoutSetPrototypeId, actionId)) {

			return true;
		}

		return false;
	}


	protected long getCompanyGroupId(
		PermissionChecker permissionChecker,
		LayoutSetPrototype layoutSetPrototype) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (layoutSetPrototype == null) {
			return companyGroupId;
		}

		long companyId = layoutSetPrototype.getCompanyId();

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
		LayoutSetPrototypePermissionImpl.class);
}
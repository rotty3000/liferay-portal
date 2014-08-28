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
import com.liferay.portal.model.Account;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.AccountLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class AccountPermissionImpl implements AccountPermission {

	@Override
	public void check(
			PermissionChecker permissionChecker, Account account,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, account, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long accountId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, accountId, actionId)) {
			throw new PrincipalException();
		}
	}

	@Override
	public boolean contains(
		PermissionChecker permissionChecker, Account account, String actionId) {

		long companyGroupId = getCompanyGroupId(permissionChecker, account);

		return permissionChecker.hasPermission(
			companyGroupId, Account.class.getName(), account.getAccountId(),
			actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long accountId,
			String actionId)
		throws PortalException {

		Account account = AccountLocalServiceUtil.getAccount(accountId);

		return contains(permissionChecker, account, actionId);
	}

	protected long getCompanyGroupId(
		PermissionChecker permissionChecker, Account account) {

		long companyGroupId = permissionChecker.getCompanyGroupId();

		if (account == null) {
			return companyGroupId;
		}

		long companyId = account.getCompanyId();

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

	private static Log _log = LogFactoryUtil.getLog(AccountPermissionImpl.class);
}
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.announcements.service.permission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portlet.announcements.model.AnnouncementsEntry;

/**
 * @author Raymond Augé
 */
public class AlertsEntryPermission extends AnnouncementsEntryPermission {

	public static boolean contains(
			PermissionChecker permissionChecker, AnnouncementsEntry entry,
			String actionId)
		throws PortalException, SystemException {

		if (permissionChecker.hasOwnerPermission(
				entry.getCompanyId(), AnnouncementsEntry.class.getName(),
				entry.getEntryId(), entry.getUserId(), actionId)) {

			return true;
		}

		if (entry.getAlert()) {
			return permissionChecker.hasPermission(
				entry.getGroupId(), AnnouncementsEntry.class.getName(),
				entry.getEntryId(), actionId);
		}

		return false;
	}

}
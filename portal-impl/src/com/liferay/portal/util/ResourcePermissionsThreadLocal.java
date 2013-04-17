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

package com.liferay.portal.util;

import com.liferay.portal.kernel.util.InitialThreadLocal;
import com.liferay.portal.model.ResourcePermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 */
public class ResourcePermissionsThreadLocal {

	public static Map<Long, ResourcePermission> getResourcePermissions() {
		return _resourcePermissions.get();
	}

	public static Boolean getSkipPermissionCheck() {
		return _skipPermissionCheck.get();
	}

	public static void setResourcePermissions(
		List<ResourcePermission> resourcePermissions) {

		if (resourcePermissions != null) {
			Map<Long, ResourcePermission> resourcePermissionMap =
				new HashMap<Long, ResourcePermission>();

			for (ResourcePermission resourcePermission : resourcePermissions) {
				resourcePermissionMap.put(
					resourcePermission.getRoleId(), resourcePermission);
			}
		}
		else {
			_resourcePermissions.remove();
		}
	}

	public static boolean setSkipPermissionCheck(Boolean skipPermissionCheck) {
		if ((skipPermissionCheck != null ) &&
			(_skipPermissionCheck.get() != null)) {

			return false;
		}

		_skipPermissionCheck.set(skipPermissionCheck);

		return true;
	}

	private static ThreadLocal<Map<Long, ResourcePermission>>
		_resourcePermissions = new InitialThreadLocal
			<Map<Long, ResourcePermission>>(
				ResourcePermissionsThreadLocal.class + "._resourcePermissions",
				null);

	private static ThreadLocal<Boolean> _skipPermissionCheck =
		new InitialThreadLocal<Boolean>(
			ResourcePermissionsThreadLocal.class + "._skipPermissionCheck",
			null);

}
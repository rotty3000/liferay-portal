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

package com.liferay.portal.service.impl;

/**
 * @author Tomas Polesovsky
 */
public class UserGroupRoleLocalServiceStagingAdvice
	extends LiveGroupStagingAdvice {

	@Override
	public void replaceStagingGroupIds(String methodName, Object[] arguments) {
		if (methodName.equals("addUserGroupRoles")) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("deleteUserGroupRoles")) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("deleteUserGroupRolesByGroupId")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getUserGroupRoles")) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("getUserGroupRoles") &&
				 ((arguments.length == 4) ||
				  ((arguments.length == 2) &&
				   (arguments[1] instanceof Long)))) {

			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("getUserGroupRolesByGroup")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getUserGroupRolesByGroupAndRole")) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("getUserGroupRolesCount") &&
				 (arguments.length == 2)) {

			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("hasUserGroupRole")) {
			replaceGroupId(arguments, 1);
		}
	}

}
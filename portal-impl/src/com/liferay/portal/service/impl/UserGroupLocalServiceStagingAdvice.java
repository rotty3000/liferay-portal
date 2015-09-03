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
public class UserGroupLocalServiceStagingAdvice extends LiveGroupStagingAdvice {

	@Override
	public void replaceStagingGroupIds(String methodName, Object[] arguments) {
		if (methodName.equals("addGroupUserGroup")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("addGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("clearGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("deleteGroupUserGroup")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("deleteGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUserGroupsCount")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUserUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("hasGroupUserGroup")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("hasGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("setGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("unsetGroupUserGroups")) {
			replaceGroupId(arguments, 0);
		}
	}

}
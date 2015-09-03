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
public class UserLocalServiceStagingAdvice extends LiveGroupStagingAdvice {

	@Override
	public void replaceStagingGroupIds(String methodName, Object[] arguments) {
		if (methodName.equals("addGroupUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("addUser")) {
			replaceGroupId(arguments, 21);
		}
		else if (methodName.equals("addUserWithWorkflow")) {
			replaceGroupId(arguments, 21);
		}
		else if (methodName.equals("clearGroupUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("deleteGroupUser")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("deleteGroupUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUserIds")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUsersCount")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("hasGroupUser")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("searchSocial") && (arguments.length == 5)) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("searchSocial") && (arguments.length == 6)) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("setGroupUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("unsetGroupTeamsUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("unsetGroupUsers")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("updateUser")) {
			replaceGroupId(arguments, 0);
		}
	}

}
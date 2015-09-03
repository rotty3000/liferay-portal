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
public class OrganizationLocalServiceStagingAdvice
	extends LiveGroupStagingAdvice {

	@Override
	public void replaceStagingGroupIds(String methodName, Object[] arguments) {
		if (methodName.equals("addGroupOrganization")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("addGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("clearGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("deleteGroupOrganization")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("deleteGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupOrganizationsCount")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getGroupUserOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("hasGroupOrganization")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("hasGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("setGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("unsetGroupOrganizations")) {
			replaceGroupId(arguments, 0);
		}
	}

}
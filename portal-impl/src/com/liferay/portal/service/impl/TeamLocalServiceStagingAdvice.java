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
public class TeamLocalServiceStagingAdvice extends LiveGroupStagingAdvice {

	@Override
	public void replaceStagingGroupIds(String methodName, Object[] arguments) {
		if (methodName.equals("addTeam") && (arguments.length > 1)) {
			replace(arguments, 1);
		}
		else if (methodName.equals("deleteTeams")) {
			replace(arguments, 0);
		}
		else if (methodName.equals("fetchTeamByUuidAndGroupId")) {
			replace(arguments, 1);
		}
		else if (methodName.equals("getGroupTeams")) {
			replace(arguments, 0);
		}
		else if (methodName.equals("getTeam") && (arguments.length == 2)) {
			replace(arguments, 0);
		}
		else if (methodName.equals("getTeamByUuidAndGroupId")) {
			replace(arguments, 1);
		}
		else if (methodName.equals("getUserTeams") && (arguments.length == 2)) {
			replace(arguments, 1);
		}
		else if (methodName.equals("search")) {
			replace(arguments, 0);
		}
		else if (methodName.equals("searchCount")) {
			replace(arguments, 0);
		}
	}

}
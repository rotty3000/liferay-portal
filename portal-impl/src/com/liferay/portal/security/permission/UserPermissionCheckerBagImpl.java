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

package com.liferay.portal.security.permission;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author László Csontos
 */
public class UserPermissionCheckerBagImpl implements UserPermissionCheckerBag {

	public UserPermissionCheckerBagImpl() {
	}

	public UserPermissionCheckerBagImpl(
		long userId, List<Group> userGroups, List<Organization> userOrgs,
		List<Group> userOrgGroups, List<Group> userUserGroupGroups) {

		this.userGroups = userGroups;
		this.userId = userId;
		this.userOrgs = userOrgs;
		this.userOrgGroups = userOrgGroups;
		this.userUserGroupGroups = userUserGroupGroups;
	}

	public UserPermissionCheckerBagImpl(
		UserPermissionCheckerBag userPermissionCheckerBag) {

		this(
			userPermissionCheckerBag.getUserId(),
			userPermissionCheckerBag.getUserGroups(),
			userPermissionCheckerBag.getUserOrgs(),
			userPermissionCheckerBag.getUserOrgGroups(),
			userPermissionCheckerBag.getUserUserGroupGroups());
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Group> getGroups() {
		if (groups == null) {
			Collection[] groupsArray = new Collection[3];

			int groupsSize = 0;

			if ((userGroups != null) && !userGroups.isEmpty()) {
				groupsArray[0] = userGroups;
				groupsSize += userGroups.size();
			}

			if ((userOrgGroups != null) && !userOrgGroups.isEmpty()) {
				groupsArray[1] = userOrgGroups;
				groupsSize += userOrgGroups.size();
			}

			if ((userUserGroupGroups != null) &&
				!userUserGroupGroups.isEmpty()) {

				groupsArray[2] = userUserGroupGroups;
				groupsSize += userUserGroupGroups.size();
			}

			groups = new ArrayList<Group>(groupsSize);

			for (Collection<Group> groupsItem : groupsArray) {
				if (groupsItem != null) {
					groups.addAll(groupsItem);
				}
			}
		}

		return groups;
	}

	@Override
	public List<Group> getUserGroups() {
		return userGroups;
	}

	@Override
	public long getUserId() {
		return userId;
	}

	@Override
	public List<Group> getUserOrgGroups() {
		return userOrgGroups;
	}

	@Override
	public List<Organization> getUserOrgs() {
		return userOrgs;
	}

	@Override
	public List<Group> getUserUserGroupGroups() {
		return userUserGroupGroups;
	}

	protected List<Group> groups;
	protected List<Group> userGroups;
	protected long userId;
	protected List<Group> userOrgGroups;
	protected List<Organization> userOrgs;
	protected List<Group> userUserGroupGroups;

}
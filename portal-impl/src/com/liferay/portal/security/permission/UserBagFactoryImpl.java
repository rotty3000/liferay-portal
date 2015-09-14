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

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.Team;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.TeamLocalServiceUtil;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class UserBagFactoryImpl implements UserBagFactory {

	@Override
	public UserPermissionCheckerBag create(long userId) throws PortalException {
		UserPermissionCheckerBag userPermissionCheckerBag =
			PermissionCacheUtil.getUserBag(userId);

		if (userPermissionCheckerBag != null) {
			return userPermissionCheckerBag;
		}

		User user = UserLocalServiceUtil.getUser(userId);

		if (user.isDefaultUser()) {
			return createGuestUserBag(user);
		}

		try {
			List<Group> userGroups = GroupLocalServiceUtil.getUserGroups(
				userId, true);

			List<Organization> userOrgs = getUserOrgs(userId);

			Set<Group> userOrgGroups = SetUtil.fromList(
				GroupLocalServiceUtil.getOrganizationsGroups(userOrgs));

			List<UserGroup> userUserGroups =
				UserGroupLocalServiceUtil.getUserUserGroups(userId);

			List<Group> userUserGroupGroups =
				GroupLocalServiceUtil.getUserGroupsGroups(userUserGroups);

			Set<Role> userRoles = new HashSet<>();

			if (!userGroups.isEmpty()) {
				List<Role> userRelatedRoles =
					RoleLocalServiceUtil.getUserRelatedRoles(
						userId, userGroups);

				userRoles.addAll(userRelatedRoles);
			}
			else {
				userRoles.addAll(RoleLocalServiceUtil.getUserRoles(userId));
			}

			userPermissionCheckerBag = new UserPermissionCheckerBagImpl(
				userId, SetUtil.fromList(userGroups), userOrgs, userOrgGroups,
				userUserGroupGroups, userRoles);

			return userPermissionCheckerBag;
		}
		finally {
			if (userPermissionCheckerBag == null) {
				userPermissionCheckerBag = new UserPermissionCheckerBagImpl(
					userId);
			}

			PermissionCacheUtil.putUserBag(userId, userPermissionCheckerBag);
		}
	}

	@Override
	public PermissionCheckerBag create(long userId, long groupId)
		throws PortalException {

		PermissionCheckerBag bag = PermissionCacheUtil.getBag(userId, groupId);

		if (bag != null) {
			return bag;
		}

		User user = UserLocalServiceUtil.getUser(userId);

		if (user.isDefaultUser()) {
			return createGuestUserBag(user);
		}

		try {
			Group group = null;

			long parentGroupId = 0;

			if (groupId > 0) {
				group = GroupLocalServiceUtil.getGroup(groupId);

				if (group.isLayout()) {
					parentGroupId = group.getParentGroupId();

					if (parentGroupId > 0) {
						group = GroupLocalServiceUtil.getGroup(parentGroupId);
					}
				}
			}

			UserPermissionCheckerBag userPermissionCheckerBag = create(userId);

			Set<Role> roles = new HashSet<>();

			roles.addAll(userPermissionCheckerBag.getRoles());

			List<Role> userGroupRoles = RoleLocalServiceUtil.getUserGroupRoles(
				userId, groupId);

			roles.addAll(userGroupRoles);

			if (parentGroupId > 0) {
				userGroupRoles = RoleLocalServiceUtil.getUserGroupRoles(
					userId, parentGroupId);

				roles.addAll(userGroupRoles);
			}

			List<Role> userGroupGroupRoles =
				RoleLocalServiceUtil.getUserGroupGroupRoles(userId, groupId);

			roles.addAll(userGroupGroupRoles);

			if (parentGroupId > 0) {
				userGroupGroupRoles =
					RoleLocalServiceUtil.getUserGroupGroupRoles(
						userId, parentGroupId);

				roles.addAll(userGroupGroupRoles);
			}

			if (group != null) {
				Set<Group> userOrgGroups =
					userPermissionCheckerBag.getUserOrgGroups();

				if (group.isOrganization() && userOrgGroups.contains(group)) {
					Role organizationUserRole = RoleLocalServiceUtil.getRole(
						group.getCompanyId(), RoleConstants.ORGANIZATION_USER);

					roles.add(organizationUserRole);
				}

				Set<Group> userGroups =
					userPermissionCheckerBag.getUserGroups();

				if ((group.isSite() &&
					 (userGroups.contains(group) ||
					  userOrgGroups.contains(group))) ||
					group.isUserPersonalSite()) {

					Role siteMemberRole = RoleLocalServiceUtil.getRole(
						group.getCompanyId(), RoleConstants.SITE_MEMBER);

					roles.add(siteMemberRole);
				}

				if ((group.isOrganization() && userOrgGroups.contains(group)) ||
					(group.isSite() && userGroups.contains(group))) {

					addTeamRoles(userId, group, roles);
				}
			}

			bag = new PermissionCheckerBagImpl(userPermissionCheckerBag, roles);

			return bag;
		}
		finally {
			if (bag == null) {
				bag = new PermissionCheckerBagImpl(userId);
			}

			PermissionCacheUtil.putBag(userId, groupId, bag);
		}
	}

	protected void addTeamRoles(long userId, Group group, Set<Role> roles)
		throws PortalException {

		List<Team> userTeams = TeamLocalServiceUtil.getUserTeams(
			userId, group.getGroupId());

		for (Team team : userTeams) {
			roles.add(team.getRole());
		}

		LinkedHashMap<String, Object> teamParams = new LinkedHashMap<>();

		teamParams.put("usersUserGroups", userId);

		List<Team> userGroupTeams = TeamLocalServiceUtil.search(
			group.getGroupId(), null, null, teamParams, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		for (Team team : userGroupTeams) {
			roles.add(team.getRole());
		}
	}

	protected PermissionCheckerBag createGuestUserBag(User guestUser)
		throws PortalException {

		Group guestGroup = GroupLocalServiceUtil.getGroup(
			guestUser.getCompanyId(), GroupConstants.GUEST);

		PermissionCheckerBag bag = PermissionCacheUtil.getBag(
			guestUser.getUserId(), guestGroup.getGroupId());

		if (bag != null) {
			return bag;
		}

		try {
			List<Role> roles = RoleLocalServiceUtil.getUserRelatedRoles(
				guestUser.getUserId(), Collections.singletonList(guestGroup));

			// Only use the guest group for deriving the roles for
			// unauthenticated users. Do not add the group to the permission bag
			// as this implies group membership which is incorrect in the case
			// of unauthenticated users.

			bag = new PermissionCheckerBagImpl(
				guestUser.getUserId(), SetUtil.fromList(roles));
		}
		finally {
			if (bag == null) {
				bag = new PermissionCheckerBagImpl(guestUser.getUserId());
			}

			PermissionCacheUtil.putBag(
				guestUser.getUserId(), guestGroup.getGroupId(), bag);
		}

		return bag;
	}

	/**
	 * Returns all of the organizations that the user is a member of, including
	 * their parent organizations.
	 *
	 * @param  userId the primary key of the user
	 * @return all of the organizations that the user is a member of, including
	 *         their parent organizations
	 * @throws PortalException if a user with the primary key could not be found
	 */
	protected List<Organization> getUserOrgs(long userId)
		throws PortalException {

		List<Organization> userOrgs =
			OrganizationLocalServiceUtil.getUserOrganizations(userId);

		if (userOrgs.isEmpty()) {
			return userOrgs;
		}

		Set<Organization> organizations = new LinkedHashSet<>();

		for (Organization organization : userOrgs) {
			if (organizations.add(organization)) {
				organizations.addAll(organization.getAncestors());
			}
		}

		return new ArrayList<>(organizations);
	}

}
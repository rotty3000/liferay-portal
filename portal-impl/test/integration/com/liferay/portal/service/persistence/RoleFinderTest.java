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

package com.liferay.portal.service.persistence;

import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.UnmodifiableList;
import com.liferay.portal.model.ResourceAction;
import com.liferay.portal.model.ResourceBlock;
import com.liferay.portal.model.ResourceBlockPermission;
import com.liferay.portal.model.ResourcePermission;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.service.ResourceBlockLocalServiceUtil;
import com.liferay.portal.service.ResourceBlockPermissionLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.test.EnvironmentExecutionTestListener;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.test.TransactionalExecutionTestListener;
import com.liferay.portal.util.ResourceBlockPermissionTestUtil;
import com.liferay.portal.util.ResourceBlockTestUtil;
import com.liferay.portal.util.ResourcePermissionTestUtil;
import com.liferay.portal.util.comparator.RoleNameComparator;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 * @author László Csontos
 */
@ExecutionTestListeners(
	listeners = {
		EnvironmentExecutionTestListener.class,
		TransactionalExecutionTestListener.class
	})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
@Transactional
public class RoleFinderTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		List<Role> roles = RoleLocalServiceUtil.getRoles(
			RoleConstants.TYPE_REGULAR, StringPool.BLANK);

		_arbitraryRole = roles.get(0);

		List<ResourceAction> resourceActions =
			ResourceActionLocalServiceUtil.getResourceActions(0, 1);

		_arbitraryResourceAction = resourceActions.get(0);

		_resourcePermission = ResourcePermissionTestUtil.addResourcePermission(
			_arbitraryResourceAction.getBitwiseValue(),
			_arbitraryResourceAction.getName(), _arbitraryRole.getRoleId());

		_bookmarkFolderResourceAction =
			ResourceActionLocalServiceUtil.getResourceAction(
				BookmarksFolder.class.getName(), ActionKeys.VIEW);

		_resourceBlock = ResourceBlockTestUtil.addResourceBlock(
			_bookmarkFolderResourceAction.getName());

		_resourceBlockPermission =
			ResourceBlockPermissionTestUtil.addResourceBlockPermission(
				_resourceBlock.getResourceBlockId(), _arbitraryRole.getRoleId(),
				_bookmarkFolderResourceAction.getBitwiseValue());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ResourcePermissionLocalServiceUtil.deleteResourcePermission(
			_resourcePermission);

		ResourceBlockLocalServiceUtil.deleteResourceBlock(_resourceBlock);

		ResourceBlockPermissionLocalServiceUtil.deleteResourceBlockPermission(
			_resourceBlockPermission);
	}

	@Test
	public void testFindByC_N_S_P_A() throws Exception {
		List<Role> roles = RoleFinderUtil.findByC_N_S_P_A(
			_resourcePermission.getCompanyId(), _resourcePermission.getName(),
			_resourcePermission.getScope(), _resourcePermission.getPrimKey(),
			_arbitraryResourceAction.getActionId());

		for (Role role : roles) {
			if (role.getRoleId() == _arbitraryRole.getRoleId()) {
				return;
			}
		}

		Assert.fail(
			"The method findByC_N_S_P_A should have returned the role " +
				_arbitraryRole.getRoleId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindByC_T() throws Exception {
		FinderCacheUtil.clearCache(
			RolePersistenceImpl.FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		List<Role> roles = RoleFinderUtil.findByC_T(
			_resourcePermission.getCompanyId(),
			new Integer[] {RoleConstants.TYPE_REGULAR});

		// Served from DB

		Assert.assertTrue(roles.contains(_arbitraryRole));
		Assert.assertFalse(roles instanceof UnmodifiableList<?>);

		roles = RoleFinderUtil.findByC_T(
			_resourcePermission.getCompanyId(),
			new Integer[] {RoleConstants.TYPE_REGULAR});

		// Served from cache

		Assert.assertTrue(roles.contains(_arbitraryRole));
		Assert.assertTrue(roles instanceof UnmodifiableList<?>);

		roles = RoleFinderUtil.findByC_T(
			_resourcePermission.getCompanyId(),
			new Integer[] {RoleConstants.TYPE_SITE});

		Assert.assertFalse(roles.contains(_arbitraryRole));

		roles = RoleFinderUtil.findByC_T(
			_resourcePermission.getCompanyId(), new Integer[0]);

		List<Role> expectedRoles = RoleLocalServiceUtil.getRoles(
			_resourcePermission.getCompanyId());

		Assert.assertEquals(expectedRoles.size(), roles.size());

		Comparator<Role> comparator = new RoleNameComparator();

		expectedRoles = ListUtil.copy(expectedRoles);
		roles = ListUtil.copy(roles);

		Collections.sort(expectedRoles, comparator);
		Collections.sort(roles, comparator);

		Assert.assertEquals(expectedRoles, roles);
	}

	@Test
	public void testFindByR_N_A() throws Exception {
		List<Role> roles = RoleFinderUtil.findByR_N_A(
			_resourceBlock.getResourceBlockId(), _resourceBlock.getName(),
			_bookmarkFolderResourceAction.getActionId());

		if (roles.contains(_arbitraryRole)) {
			return;
		}

		Assert.fail(
			"The method findByR_N_A should have returned the role " +
				_arbitraryRole.getRoleId());
	}

	private static ResourceAction _arbitraryResourceAction;
	private static Role _arbitraryRole;
	private static ResourceAction _bookmarkFolderResourceAction;
	private static ResourceBlock _resourceBlock;
	private static ResourceBlockPermission _resourceBlockPermission;
	private static ResourcePermission _resourcePermission;

}
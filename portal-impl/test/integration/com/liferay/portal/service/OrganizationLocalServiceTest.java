/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.service;

import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.OrganizationConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.test.EnvironmentExecutionTestListener;
import com.liferay.portal.test.ExecutionTestListeners;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.test.TransactionalExecutionTestListener;
import com.liferay.portal.util.TestPropsValues;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Ferrer
 */
@ExecutionTestListeners(
	listeners = {
		EnvironmentExecutionTestListener.class,
		TransactionalExecutionTestListener.class
	})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class OrganizationLocalServiceTest {

	@Test
	public void testAddOrganizationWithIndirectAssociation() throws Exception {
		User user = ServiceTestUtil.addUser(
			"testAddOrganizationWithIndirectAssociation", false, null);

		Organization organization =
			OrganizationLocalServiceUtil.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				"testAddOrganizationWithIndirectAssociation",
				OrganizationConstants.TYPE_REGULAR_ORGANIZATION, false, 0, 0,
				ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, StringPool.BLANK,
				false, null);

		try {
			List<Organization> organizations = user.getOrganizations(true);

			Assert.assertTrue(organizations.contains(organization));
		}
		finally {
			OrganizationLocalServiceUtil.deleteOrganization(
				organization.getOrganizationId());
		}
	}

	@Test
	@Transactional
	public void testAddOrganizationWithoutDirectAssociation() throws Exception {
		Organization organization =
			OrganizationLocalServiceUtil.addOrganization(
				TestPropsValues.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				"testAddOrganizationWithoutDirectAssociation",
				OrganizationConstants.TYPE_REGULAR_ORGANIZATION, false, 0, 0,
				ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, StringPool.BLANK,
				false, null);

		Assert.assertFalse(
			OrganizationLocalServiceUtil.hasUserOrganization(
				TestPropsValues.getUserId(), organization.getOrganizationId()));
	}

}
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

package com.liferay.portal.model.impl;

import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTemplate;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.test.MainServletExecutionTestListener;
import com.liferay.portal.test.TransactionalCallbackAwareExecutionTestListener;
import com.liferay.portal.util.GroupTestUtil;
import com.liferay.portal.util.LayoutTestUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.TestPropsValues;
import com.liferay.portal.util.UserTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Raymond Augé
 */
@ExecutionTestListeners(
	listeners = {
		MainServletExecutionTestListener.class,
		TransactionalCallbackAwareExecutionTestListener.class
	})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class LayoutTypePortletTest {

	@Before
	public void setUp() {
		FinderCacheUtil.clearCache();
	}

	@Test
	@Transactional
	public void testAddModeAboutPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(layoutTypePortlet.hasModeAboutPortletId(portletId));

		layoutTypePortlet.addModeAboutPortletId(portletId);

		Assert.assertTrue(layoutTypePortlet.hasModeAboutPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModeConfigPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(layoutTypePortlet.hasModeConfigPortletId(portletId));

		layoutTypePortlet.addModeConfigPortletId(portletId);

		Assert.assertTrue(layoutTypePortlet.hasModeConfigPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModeEditDefaultsPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(
			layoutTypePortlet.hasModeEditDefaultsPortletId(portletId));

		layoutTypePortlet.addModeEditDefaultsPortletId(portletId);

		Assert.assertTrue(
			layoutTypePortlet.hasModeEditDefaultsPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModeEditGuestPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(
			layoutTypePortlet.hasModeEditGuestPortletId(portletId));

		layoutTypePortlet.addModeEditGuestPortletId(portletId);

		Assert.assertTrue(
			layoutTypePortlet.hasModeEditGuestPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModeEditPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(layoutTypePortlet.hasModeEditPortletId(portletId));

		layoutTypePortlet.addModeEditPortletId(portletId);

		Assert.assertTrue(layoutTypePortlet.hasModeEditPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModeHelpPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(layoutTypePortlet.hasModeHelpPortletId(portletId));

		layoutTypePortlet.addModeHelpPortletId(portletId);

		Assert.assertTrue(layoutTypePortlet.hasModeHelpPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModePreviewPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(
			layoutTypePortlet.hasModePreviewPortletId(portletId));

		layoutTypePortlet.addModePreviewPortletId(portletId);

		Assert.assertTrue(layoutTypePortlet.hasModePreviewPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddModePrintPortletId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		String portletId = PortletKeys.JOURNAL_CONTENT;

		Assert.assertFalse(layoutTypePortlet.hasModePrintPortletId(portletId));

		layoutTypePortlet.addModePrintPortletId(portletId);

		Assert.assertTrue(layoutTypePortlet.hasModePrintPortletId(portletId));
	}

	@Test
	@Transactional
	public void testAddPortletIdCheckColumn() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		Layout layout = layoutTypePortlet.getLayout();

		User user = UserTestUtil.addUser(
			ServiceTestUtil.randomString(), layout.getGroupId());

		String portletId = PortletKeys.JOURNAL_CONTENT;

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		List<String> columns = layoutTemplate.getColumns();

		String column1 = columns.get(0);

		Assert.assertEquals(2, columns.size());

		portletId = layoutTypePortlet.addPortletId(user.getUserId(), portletId);

		Assert.assertNotNull(portletId);

		List<Portlet> portlets = layoutTypePortlet.getAllPortlets(column1);

		Assert.assertEquals(1, portlets.size());
	}

	@Test
	@Transactional
	public void testAddPortletIdColumn2() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		Layout layout = layoutTypePortlet.getLayout();

		User user = UserTestUtil.addUser(
			ServiceTestUtil.randomString(), layout.getGroupId());

		String portletId = PortletKeys.JOURNAL_CONTENT;

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		List<String> columns = layoutTemplate.getColumns();

		Assert.assertEquals(2, columns.size());

		String column1 = columns.get(0);
		String column2 = columns.get(1);

		portletId = layoutTypePortlet.addPortletId(
			user.getUserId(), portletId, column2, -1);

		Assert.assertNotNull(portletId);

		List<Portlet> portlets = layoutTypePortlet.getAllPortlets(column1);

		Assert.assertEquals(0, portlets.size());

		portlets = layoutTypePortlet.getAllPortlets(column2);

		Assert.assertEquals(1, portlets.size());
	}

	@Test
	@Transactional
	public void testAddPortletIdWithInvalidId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		Layout layout = layoutTypePortlet.getLayout();

		User user = UserTestUtil.addUser(
			ServiceTestUtil.randomString(), layout.getGroupId());

		String portletId = ServiceTestUtil.randomString();

		portletId = layoutTypePortlet.addPortletId(user.getUserId(), portletId);

		Assert.assertNull(portletId);
	}

	@Test
	@Transactional
	public void testAddPortletIdWithInvalidIdWithoutPermission()
		throws Exception {

		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		Layout layout = layoutTypePortlet.getLayout();

		User user = UserTestUtil.addUser(
			ServiceTestUtil.randomString(), layout.getGroupId());

		String portletId = ServiceTestUtil.randomString();

		portletId = layoutTypePortlet.addPortletId(user.getUserId(), portletId);

		Assert.assertNull(portletId);
	}

	@Test
	@Transactional
	public void testAddPortletIdWithValidId() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		Layout layout = layoutTypePortlet.getLayout();

		User user = UserTestUtil.addUser(
			ServiceTestUtil.randomString(), layout.getGroupId());

		String portletId = PortletKeys.JOURNAL_CONTENT;

		portletId = layoutTypePortlet.addPortletId(user.getUserId(), portletId);

		Assert.assertNotNull(portletId);
	}

	@Test
	@Transactional
	public void testGetPortlets() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		Layout layout = layoutTypePortlet.getLayout();

		User user = UserTestUtil.addUser(
			ServiceTestUtil.randomString(), layout.getGroupId());

		// Add normal portlet

		String portletId = StringPool.BLANK;

		portletId = layoutTypePortlet.addPortletId(
			user.getUserId(), PortletKeys.JOURNAL_CONTENT);

		// Add static portlet

		String[] originalPropertyValues =
			PropsValues.LAYOUT_STATIC_PORTLETS_ALL;

		PropsUtil.set(
			PropsKeys.LAYOUT_STATIC_PORTLETS_ALL, PortletKeys.BOOKMARKS);

		// Add embedded

		PortletPreferencesLocalServiceUtil.getPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			PortletKeys.BLOGS, PortletConstants.DEFAULT_PREFERENCES);

		// Test only manually added

		List<Portlet> portlets = layoutTypePortlet.getPortlets();

		Assert.assertEquals(1, portlets.size());

		portletId = portlets.get(0).getPortletId();

		Assert.assertTrue(portletId.startsWith(PortletKeys.JOURNAL_CONTENT));

		// Test static

		portlets = layoutTypePortlet.getPortlets(
			LayoutTypePortlet.PORTLET_INCLUDE_STATIC);

		Assert.assertEquals(2, portlets.size());

		for (Portlet portlet : portlets) {
			String tempPortletId = portlet.getPortletId();

			if (!tempPortletId.startsWith(PortletKeys.JOURNAL_CONTENT) &&
				!tempPortletId.equals(PortletKeys.BOOKMARKS)) {

				Assert.fail("Invalid portlet has been returned");
			}
		}

		// Test embedded

		portlets = layoutTypePortlet.getPortlets(
			LayoutTypePortlet.PORTLET_INCLUDE_EMBEDDED |
				LayoutTypePortlet.PORTLET_INCLUDE_STATIC);

		Assert.assertEquals(3, portlets.size());

		for (Portlet portlet : portlets) {
			String tempPortletId = portlet.getPortletId();

			if (!tempPortletId.startsWith(PortletKeys.JOURNAL_CONTENT) &&
				!tempPortletId.equals(PortletKeys.BOOKMARKS) &&
				!tempPortletId.equals(PortletKeys.BLOGS)) {

				Assert.fail("Invalid portlet has been returned");
			}
		}

		// Cleanup property value

		PropsUtil.set(
			PropsKeys.LAYOUT_STATIC_PORTLETS_ALL,
			ArrayUtil.toString(originalPropertyValues, (String)null));
	}

	@Test
	@Transactional
	public void testNoPortlets() throws Exception {
		LayoutTypePortlet layoutTypePortlet = getLayoutTypePortlet();

		List<Portlet> portlets = layoutTypePortlet.getAllPortlets();

		Assert.assertEquals(0, portlets.size());
	}

	protected LayoutTypePortlet getLayoutTypePortlet() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addLayout(
			group.getGroupId(), ServiceTestUtil.randomString(), false);

		return (LayoutTypePortlet)layout.getLayoutType();
	}

}
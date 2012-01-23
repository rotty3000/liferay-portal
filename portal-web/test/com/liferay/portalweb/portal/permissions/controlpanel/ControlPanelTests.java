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

package com.liferay.portalweb.portal.permissions.controlpanel;

import com.liferay.portalweb.portal.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class ControlPanelTests extends BaseTestSuite {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(ControlPanelTest.class);
		testSuite.addTestSuite(CreateRolesTest.class);
		testSuite.addTestSuite(DefineCARolesTest.class);
		testSuite.addTestSuite(CA_PortalRolesTest.class);
		testSuite.addTestSuite(CA_AnnouncementsRolesTest.class);
		testSuite.addTestSuite(CA_BlogsRolesTest.class);
		testSuite.addTestSuite(CA_DocumentLibraryRolesTest.class);
		testSuite.addTestSuite(CA_ImageGalleryRolesTest.class);
		testSuite.addTestSuite(CA_MessageBoardsRolesTest.class);
		testSuite.addTestSuite(CA_WebContentDisplayRolesTest.class);
		testSuite.addTestSuite(CA_WebContentListRolesTest.class);
		testSuite.addTestSuite(CA_WebContentRolesTest.class);
		testSuite.addTestSuite(CA_WebContentSearchRolesTest.class);
		testSuite.addTestSuite(DefineMemberRolesTest.class);
		testSuite.addTestSuite(Member_AnnouncementsRolesTest.class);
		testSuite.addTestSuite(Member_BlogsRolesTest.class);
		testSuite.addTestSuite(Member_DocumentLibraryRolesTest.class);
		testSuite.addTestSuite(Member_ImageGalleryRolesTest.class);
		testSuite.addTestSuite(Member_MessageBoardsRolesTest.class);
		testSuite.addTestSuite(Member_WebContentRolesTest.class);
		testSuite.addTestSuite(DefinePublisherRolesTest.class);
		testSuite.addTestSuite(Publisher_PortalRolesTest.class);
		testSuite.addTestSuite(Publisher_WebContentRolesTest.class);
		testSuite.addTestSuite(DefineWriterRolesTest.class);
		testSuite.addTestSuite(Writer_PortalRolesTest.class);
		testSuite.addTestSuite(Writer_WebContentDisplayRolesTest.class);
		testSuite.addTestSuite(Writer_WebContentListRolesTest.class);
		testSuite.addTestSuite(Writer_WebContentRolesTest.class);
		testSuite.addTestSuite(Writer_WebContentSearchRolesTest.class);
		testSuite.addTestSuite(AddCATest.class);
		testSuite.addTestSuite(AddMemberTest.class);
		testSuite.addTestSuite(AddPortletMemberTest.class);
		testSuite.addTestSuite(AddPublisherTest.class);
		testSuite.addTestSuite(AddScopeTest.class);
		testSuite.addTestSuite(AddWriterTest.class);
		testSuite.addTestSuite(AssignMembersTest.class);
		testSuite.addTestSuite(AddScopeCommunityTest.class);
		testSuite.addTestSuite(AddScopeCommunityPageTest.class);
		testSuite.addTestSuite(AssignScopeMemberToScopeCommunityTest.class);
		testSuite.addTestSuite(LoginUsersTest.class);
		testSuite.addTestSuite(LogoutTest.class);

		return testSuite;
	}
}
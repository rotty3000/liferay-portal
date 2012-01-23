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

package com.liferay.portalweb.portal.controlpanel.virtualhosting;

import com.liferay.portalweb.portal.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class VirtualHostingTests extends BaseTestSuite {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(AddVirtualHostingCommunityTest.class);
		testSuite.addTestSuite(AddVirtualHostingPublicPageTest.class);
		testSuite.addTestSuite(AddVirtualHostingPrivatePageTest.class);
		testSuite.addTestSuite(EditFriendlyURLSlashesTest.class);
		testSuite.addTestSuite(EditFriendlyURLTest.class);
		testSuite.addTestSuite(AssertPublicPageFriendlyURLTest.class);
		testSuite.addTestSuite(AssertPrivatePageFriendlyURLTest.class);
		testSuite.addTestSuite(RestoreFriendlyURLTest.class);
		testSuite.addTestSuite(EditPublicPageURLTest.class);
		testSuite.addTestSuite(AssertPublicPageHostURLTest.class);
		testSuite.addTestSuite(RestorePublicPageURLTest.class);
		testSuite.addTestSuite(EditPrivatePageURLTest.class);
		testSuite.addTestSuite(AssertPrivatePageHostURLTest.class);
		testSuite.addTestSuite(RestorePrivatePageURLTest.class);
		testSuite.addTestSuite(TearDownCommunityTest.class);

		return testSuite;
	}
}
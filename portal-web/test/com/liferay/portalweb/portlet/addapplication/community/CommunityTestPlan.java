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

package com.liferay.portalweb.portlet.addapplication.community;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.addapplication.community.searchbookmarks.SearchBookmarksTests;
import com.liferay.portalweb.portlet.addapplication.community.searchdirectory.SearchDirectoryTests;
import com.liferay.portalweb.portlet.addapplication.community.searchinvitation.SearchInvitationTests;
import com.liferay.portalweb.portlet.addapplication.community.searchmycommunities.SearchMyCommunitiesTests;
import com.liferay.portalweb.portlet.addapplication.community.searchpagecomments.SearchPageCommentsTests;
import com.liferay.portalweb.portlet.addapplication.community.searchpageflags.SearchPageFlagsTests;
import com.liferay.portalweb.portlet.addapplication.community.searchpageratings.SearchPageRatingsTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class CommunityTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(SearchBookmarksTests.suite());
		testSuite.addTest(SearchDirectoryTests.suite());
		testSuite.addTest(SearchInvitationTests.suite());
		testSuite.addTest(SearchMyCommunitiesTests.suite());
		testSuite.addTest(SearchPageCommentsTests.suite());
		testSuite.addTest(SearchPageFlagsTests.suite());
		testSuite.addTest(SearchPageRatingsTests.suite());

		return testSuite;
	}

}
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

package com.liferay.portalweb.portlet.addapplication.collaboration;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.addapplication.collaboration.searchblogs.SearchBlogsTests;
import com.liferay.portalweb.portlet.addapplication.collaboration.searchblogsaggregator.SearchBlogsAggregatorTests;
import com.liferay.portalweb.portlet.addapplication.collaboration.searchcalendar.SearchCalendarTests;
import com.liferay.portalweb.portlet.addapplication.collaboration.searchmessageboards.SearchMessageBoardsTests;
import com.liferay.portalweb.portlet.addapplication.collaboration.searchrecentbloggers.SearchRecentBloggersTests;
import com.liferay.portalweb.portlet.addapplication.collaboration.searchwiki.SearchWikiTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class CollaborationTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(SearchBlogsTests.suite());
		testSuite.addTest(SearchBlogsAggregatorTests.suite());
		testSuite.addTest(SearchCalendarTests.suite());
		testSuite.addTest(SearchMessageBoardsTests.suite());
		testSuite.addTest(SearchRecentBloggersTests.suite());
		testSuite.addTest(SearchWikiTests.suite());

		return testSuite;
	}

}
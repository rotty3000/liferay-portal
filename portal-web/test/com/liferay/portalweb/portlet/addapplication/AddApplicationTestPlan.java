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

package com.liferay.portalweb.portlet.addapplication;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.addapplication.collaboration.CollaborationTestPlan;
import com.liferay.portalweb.portlet.addapplication.community.CommunityTestPlan;
import com.liferay.portalweb.portlet.addapplication.contentmanagement.ContentManagementTestPlan;
import com.liferay.portalweb.portlet.addapplication.finance.FinanceTestPlan;
import com.liferay.portalweb.portlet.addapplication.news.NewsTestPlan;
import com.liferay.portalweb.portlet.addapplication.sample.SampleTestPlan;
import com.liferay.portalweb.portlet.addapplication.shopping.ShoppingTestPlan;
import com.liferay.portalweb.portlet.addapplication.social.SocialTestPlan;
import com.liferay.portalweb.portlet.addapplication.tools.ToolsTestPlan;
import com.liferay.portalweb.portlet.addapplication.wiki.WikiTestPlan;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class AddApplicationTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(CollaborationTestPlan.suite());
		testSuite.addTest(CommunityTestPlan.suite());
		testSuite.addTest(ContentManagementTestPlan.suite());
		testSuite.addTest(FinanceTestPlan.suite());
		testSuite.addTest(NewsTestPlan.suite());
		testSuite.addTest(SampleTestPlan.suite());
		testSuite.addTest(ShoppingTestPlan.suite());
		testSuite.addTest(SocialTestPlan.suite());
		testSuite.addTest(ToolsTestPlan.suite());
		testSuite.addTest(WikiTestPlan.suite());

		return testSuite;
	}

}
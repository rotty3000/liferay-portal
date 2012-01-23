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

package com.liferay.portalweb.portlet.wiki.comment;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.wiki.comment.addfrontpagecomment.AddFrontPageCommentTests;
import com.liferay.portalweb.portlet.wiki.comment.addfrontpagecommentbodynull.AddFrontPageCommentBodyNullTests;
import com.liferay.portalweb.portlet.wiki.comment.addfrontpagecommentmultiple.AddFrontPageCommentMultipleTests;
import com.liferay.portalweb.portlet.wiki.comment.deletefrontpagecomment.DeleteFrontPageCommentTests;
import com.liferay.portalweb.portlet.wiki.comment.editfrontpagecommentbody.EditFrontPageCommentBodyTests;
import com.liferay.portalweb.portlet.wiki.comment.ratefrontpagecomment.RateFrontPageCommentTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class CommentTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddFrontPageCommentTests.suite());
		testSuite.addTest(AddFrontPageCommentBodyNullTests.suite());
		testSuite.addTest(AddFrontPageCommentMultipleTests.suite());
		testSuite.addTest(DeleteFrontPageCommentTests.suite());
		testSuite.addTest(EditFrontPageCommentBodyTests.suite());
		testSuite.addTest(RateFrontPageCommentTests.suite());

		return testSuite;
	}

}
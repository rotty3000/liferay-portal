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

package com.liferay.portalweb.portlet.wiki.attachment;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.wiki.attachment.addfrontpageattachment.AddFrontPageAttachmentTests;
import com.liferay.portalweb.portlet.wiki.attachment.addfrontpageemptyattachment.AddFrontPageEmptyAttachmentTests;
import com.liferay.portalweb.portlet.wiki.attachment.addwikipageattachment.AddWikiPageAttachmentTests;
import com.liferay.portalweb.portlet.wiki.attachment.addwikipageemptyattachment.AddWikiPageEmptyAttachmentTests;
import com.liferay.portalweb.portlet.wiki.attachment.deletefrontpageattachment.DeleteFrontPageAttachmentTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class AttachmentTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddFrontPageAttachmentTests.suite());
		testSuite.addTest(AddFrontPageEmptyAttachmentTests.suite());
		testSuite.addTest(AddWikiPageAttachmentTests.suite());
		testSuite.addTest(AddWikiPageEmptyAttachmentTests.suite());
		testSuite.addTest(DeleteFrontPageAttachmentTests.suite());

		return testSuite;
	}

}
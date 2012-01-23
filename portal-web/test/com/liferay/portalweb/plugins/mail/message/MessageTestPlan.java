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

package com.liferay.portalweb.plugins.mail.message;

import com.liferay.portalweb.plugins.mail.message.deletemessagenullallmail.DeleteMessageNullAllMailTests;
import com.liferay.portalweb.plugins.mail.message.deletemessagenulldrafts.DeleteMessageNullDraftsTests;
import com.liferay.portalweb.plugins.mail.message.deletemessagenullinbox.DeleteMessageNullInboxTests;
import com.liferay.portalweb.plugins.mail.message.deletemessagenullsentmail.DeleteMessageNullSentMailTests;
import com.liferay.portalweb.plugins.mail.message.sendmessagesubjectnull.SendMessageSubjectNullTests;
import com.liferay.portalweb.plugins.mail.message.sendmessagetonull.SendMessageToNullTests;
import com.liferay.portalweb.portal.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class MessageTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(DeleteMessageNullAllMailTests.suite());
		testSuite.addTest(DeleteMessageNullDraftsTests.suite());
		testSuite.addTest(DeleteMessageNullInboxTests.suite());
		testSuite.addTest(DeleteMessageNullSentMailTests.suite());
		testSuite.addTest(SendMessageToNullTests.suite());
		testSuite.addTest(SendMessageSubjectNullTests.suite());

		return testSuite;
	}

}
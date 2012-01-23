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

package com.liferay.portalweb.portlet.pollsdisplay.question;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.pollsdisplay.question.deletequestionselect.DeleteQuestionSelectTests;
import com.liferay.portalweb.portlet.pollsdisplay.question.selectquestion.SelectQuestionTests;
import com.liferay.portalweb.portlet.pollsdisplay.question.selectquestionmultiple.SelectQuestionMultipleTests;
import com.liferay.portalweb.portlet.pollsdisplay.question.selectquestionnull.SelectQuestionNullTests;
import com.liferay.portalweb.portlet.pollsdisplay.question.votequestion.VoteQuestionTests;
import com.liferay.portalweb.portlet.pollsdisplay.question.votequestionnull.VoteQuestionNullTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class QuestionTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(DeleteQuestionSelectTests.suite());
		testSuite.addTest(SelectQuestionTests.suite());
		testSuite.addTest(SelectQuestionMultipleTests.suite());
		testSuite.addTest(SelectQuestionNullTests.suite());
		testSuite.addTest(VoteQuestionTests.suite());
		testSuite.addTest(VoteQuestionNullTests.suite());

		return testSuite;
	}

}
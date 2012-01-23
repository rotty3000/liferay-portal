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

package com.liferay.portalweb.portlet.announcements.announcementsentry;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.announcements.announcementsentry.addannouncementsentrycontentnull.AddAnnouncementsEntryContentNullTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.addannouncementsentrygeneral.AddAnnouncementsEntryGeneralTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.addannouncementsentrypriorityimportant.AddAnnouncementsEntryPriorityImportantTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.addannouncementsentryprioritynormal.AddAnnouncementsEntryPriorityNormalTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.deleteannouncementsentrygeneral.DeleteAnnouncementsEntryGeneralTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.editannouncementsentrygeneral.EditAnnouncementsEntryGeneralTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.hideannouncementsentrygeneral.HideAnnouncementsEntryGeneralTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.markasreadannouncementsentrygeneral.MarkAsReadAnnouncementsEntryGeneralTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.showannouncementsentrygeneral.ShowAnnouncementsEntryGeneralTests;
import com.liferay.portalweb.portlet.announcements.announcementsentry.viewpriorityorder.ViewPriorityOrderTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class AnnouncementsEntryTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddAnnouncementsEntryContentNullTests.suite());
		testSuite.addTest(AddAnnouncementsEntryGeneralTests.suite());
		testSuite.addTest(AddAnnouncementsEntryPriorityImportantTests.suite());
		testSuite.addTest(AddAnnouncementsEntryPriorityNormalTests.suite());
		testSuite.addTest(ViewPriorityOrderTests.suite());
		testSuite.addTest(DeleteAnnouncementsEntryGeneralTests.suite());
		testSuite.addTest(EditAnnouncementsEntryGeneralTests.suite());
		testSuite.addTest(HideAnnouncementsEntryGeneralTests.suite());
		testSuite.addTest(MarkAsReadAnnouncementsEntryGeneralTests.suite());
		testSuite.addTest(ShowAnnouncementsEntryGeneralTests.suite());

		return testSuite;
	}

}
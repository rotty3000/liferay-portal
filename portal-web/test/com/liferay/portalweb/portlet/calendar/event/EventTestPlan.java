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

package com.liferay.portalweb.portlet.calendar.event;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.calendar.event.addevent.AddEventTests;
import com.liferay.portalweb.portlet.calendar.event.addeventdateendinvalid.AddEventDateEndInvalidTests;
import com.liferay.portalweb.portlet.calendar.event.addeventdaterepeatinvalid.AddEventDateRepeatInvalidTests;
import com.liferay.portalweb.portlet.calendar.event.addeventdatestartinvalid.AddEventDateStartInvalidTests;
import com.liferay.portalweb.portlet.calendar.event.addeventdurationinvalid.AddEventDurationInvalidTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingdaily.AddEventRepeatingDailyTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingdailyweekday.AddEventRepeatingDailyWeekDayTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingmonthlydate.AddEventRepeatingMonthlyDateTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingmonthlyday.AddEventRepeatingMonthlyDayTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingweekly.AddEventRepeatingWeeklyTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingyearlydate.AddEventRepeatingYearlyDateTests;
import com.liferay.portalweb.portlet.calendar.event.addeventrepeatingyearlyday.AddEventRepeatingYearlyDayTests;
import com.liferay.portalweb.portlet.calendar.event.addeventtitlenull.AddEventTitleNullTests;
import com.liferay.portalweb.portlet.calendar.event.addeventtypeappointment.AddEventTypeAppointmentTests;
import com.liferay.portalweb.portlet.calendar.event.addeventtypeconcert.AddEventTypeConcertTests;
import com.liferay.portalweb.portlet.calendar.event.addeventtypevacation.AddEventTypeVacationTests;
import com.liferay.portalweb.portlet.calendar.event.asserteventselectfieldenabled.AssertEventSelectFieldEnabledTests;
import com.liferay.portalweb.portlet.calendar.event.deleteevent.DeleteEventTests;
import com.liferay.portalweb.portlet.calendar.event.editevent.EditEventTests;
import com.liferay.portalweb.portlet.calendar.event.vieweventtypeappointment.ViewEventTypeAppointmentTests;
import com.liferay.portalweb.portlet.calendar.event.vieweventtypeconcert.ViewEventTypeConcertTests;
import com.liferay.portalweb.portlet.calendar.event.vieweventtypevacation.ViewEventTypeVacationTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class EventTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddEventTests.suite());
		testSuite.addTest(AddEventDateEndInvalidTests.suite());
		testSuite.addTest(AddEventDateRepeatInvalidTests.suite());
		testSuite.addTest(AddEventDateStartInvalidTests.suite());
		testSuite.addTest(AddEventDurationInvalidTests.suite());
		testSuite.addTest(AddEventRepeatingDailyTests.suite());
		testSuite.addTest(AddEventRepeatingDailyWeekDayTests.suite());
		testSuite.addTest(AddEventRepeatingMonthlyDateTests.suite());
		testSuite.addTest(AddEventRepeatingMonthlyDayTests.suite());
		testSuite.addTest(AddEventRepeatingWeeklyTests.suite());
		testSuite.addTest(AddEventRepeatingYearlyDateTests.suite());
		testSuite.addTest(AddEventRepeatingYearlyDayTests.suite());
		testSuite.addTest(AddEventTitleNullTests.suite());
		testSuite.addTest(AddEventTypeAppointmentTests.suite());
		testSuite.addTest(AddEventTypeConcertTests.suite());
		testSuite.addTest(AddEventTypeVacationTests.suite());
		testSuite.addTest(AssertEventSelectFieldEnabledTests.suite());
		testSuite.addTest(DeleteEventTests.suite());
		testSuite.addTest(EditEventTests.suite());
		testSuite.addTest(ViewEventTypeAppointmentTests.suite());
		testSuite.addTest(ViewEventTypeConcertTests.suite());
		testSuite.addTest(ViewEventTypeVacationTests.suite());

		return testSuite;
	}

}
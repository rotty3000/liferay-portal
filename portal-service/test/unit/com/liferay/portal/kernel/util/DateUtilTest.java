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

package com.liferay.portal.kernel.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.testng.Assert;

/**
 * @author Alexander Chow
 * @author Manuel de la Peña
 * @author Ray Augé
 */
@PrepareForTest(CalendarFactoryUtil.class)
@RunWith(PowerMockRunner.class)
public class DateUtilTest extends PowerMockito {

	@Test
	public void testGetDaysBetween() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

		_testGetDaysBetween(new Date(), new Date(), 0);
		_testGetDaysBetween(
			dateFormat.parse("12-31-2011"), dateFormat.parse("1-1-2012"), 1);
		_testGetDaysBetween(
			dateFormat.parse("1-1-2011"), dateFormat.parse("1-1-2012"), 365);
		_testGetDaysBetween(
			dateFormat.parse("2-28-2012"), dateFormat.parse("3-1-2012"), 2);
		_testGetDaysBetween(
			dateFormat.parse("3-1-2012"), dateFormat.parse("2-28-2012"), 2);
	}

	private void _testGetDaysBetween(Date date1, Date date2, int expected) {
		mockStatic(CalendarFactoryUtil.class);

		when(
			CalendarFactoryUtil.getCalendar()
		).thenReturn(
			new GregorianCalendar()
		);

		Assert.assertEquals(
			DateUtil.getDaysBetween(date1, date2, _timeZone), expected);
	}

	@Mock
	private TimeZone _timeZone;

}
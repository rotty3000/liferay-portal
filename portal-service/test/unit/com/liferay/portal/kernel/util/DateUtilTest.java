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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Alexander Chow
 * @author Manuel de la Peña
 * @author Ray Augé
 */
@PrepareForTest(DateFormatFactoryUtil.class)
@RunWith(PowerMockRunner.class)
public class DateUtilTest extends PowerMockito {

	@Test
	public void testGetISOFormatLength8() {
		_testGetISOFormat("01234567","yyyyMMdd");
	}

	@Test
	public void testGetISOFormatLength12() {
		_testGetISOFormat("012345678901","yyyyMMddHHmm");
	}

	@Test
	public void testGetISOFormatLength13() {
		_testGetISOFormat("0123456789012","yyyyMMdd'T'HHmm");
	}

	@Test
	public void testGetISOFormatLength14() {
		_testGetISOFormat("01234567890123","yyyyMMddHHmmss");
	}

	@Test
	public void testGetISOFormatLength15() {
		_testGetISOFormat("012345678901234","yyyyMMdd'T'HHmmss");
	}

	@Test
	public void testGetISOFormatT() {
		_testGetISOFormat("01234567T9012345","yyyyMMdd'T'HHmmssz");
	}

	@Test
	public void testGetISOFormatAny() {
		_testGetISOFormat(Mockito.anyString(),"yyyyMMddHHmmssz");
	}

	private void _mockDateUtilPattern(String pattern) {
		mockStatic(DateFormatFactoryUtil.class);

		when(
			DateFormatFactoryUtil.getSimpleDateFormat(pattern)
		).thenReturn(
			new SimpleDateFormat(pattern, new Locale("es_ES"))
		);
	}

	private void _testGetISOFormat(String text, String pattern) {
		_mockDateUtilPattern(pattern);

		DateFormat dateFormat = DateUtil.getISOFormat(text);

		String actualPattern = ((SimpleDateFormat)dateFormat).toPattern();

		Assert.assertEquals(pattern, actualPattern);
	}

}
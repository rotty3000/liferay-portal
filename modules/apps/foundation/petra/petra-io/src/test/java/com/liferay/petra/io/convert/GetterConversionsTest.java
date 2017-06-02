/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.petra.io.convert;

import com.liferay.petra.io.StringPool;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jesse Rao
 * @author Raymond Augé
 */
public class GetterConversionsTest {

	@Test
	public void testGetBoolean() {
		Assert.assertFalse(Conversions.getBoolean("false"));
		Assert.assertTrue(Conversions.getBoolean("true"));
		Assert.assertFalse(Conversions.getBoolean(Boolean.FALSE));
		Assert.assertTrue(Conversions.getBoolean(Boolean.TRUE));
		Assert.assertFalse(Conversions.getBoolean(null, false));
		Assert.assertTrue(Conversions.getBoolean(null, true));
		Assert.assertFalse(Conversions.getBoolean(StringPool.BLANK));
		Assert.assertFalse(Conversions.getBoolean(StringPool.BLANK, false));
		Assert.assertFalse(Conversions.getBoolean(StringPool.BLANK, true));

		for (String s : Conversions.BOOLEANS) {
			Assert.assertTrue(Conversions.getBoolean(s));
			Assert.assertTrue(Conversions.getBoolean(s, true));
			Assert.assertTrue(Conversions.getBoolean(s, false));
		}
	}

	@Test
	public void testGetDouble() {

		// Wrong first char

		Assert.assertEquals(
			Conversions.DEFAULT_DOUBLE, Conversions.getDouble("e12.3"),
			Conversions.DEFAULT_DOUBLE);

		// Wrong middle char

		Assert.assertEquals(
			Conversions.DEFAULT_DOUBLE, Conversions.getDouble("12e.3"),
			Conversions.DEFAULT_DOUBLE);

		// Start with '+'

		Assert.assertEquals(
			12.3, Conversions.getDouble("+12.3"), Conversions.DEFAULT_DOUBLE);

		// Start with '-'

		Assert.assertEquals(
			-12.3, Conversions.getDouble("-12.3"), Conversions.DEFAULT_DOUBLE);

		// Maximum double

		Assert.assertEquals(
			Double.MAX_VALUE,
			Conversions.getDouble(Double.toString(Double.MAX_VALUE)),
			Conversions.DEFAULT_DOUBLE);

		// Minimum double

		Assert.assertEquals(
			Double.MIN_VALUE,
			Conversions.getDouble(Double.toString(Double.MIN_VALUE)),
			Conversions.DEFAULT_DOUBLE);

		/*

		// Locale aware

		Assert.assertEquals(
			4.7, GetterUtil.getDouble("4,7", LocaleUtil.PORTUGAL),
			GetterUtil.DEFAULT_DOUBLE);

		Assert.assertEquals(
			4.7, GetterUtil.getDouble("4.7", LocaleUtil.US),
			GetterUtil.DEFAULT_DOUBLE);

		// Locale aware respecting the whole input

		Assert.assertEquals(
			GetterUtil.DEFAULT_DOUBLE,
			GetterUtil.getDouble("4.7", LocaleUtil.HUNGARY),
			GetterUtil.DEFAULT_DOUBLE);*/
	}

	@Test
	public void testGetInteger() {

		// Wrong first char

		int result = Conversions.getInteger("e123", -1);

		Assert.assertEquals(-1, result);

		// Wrong middle char

		result = Conversions.getInteger("12e3", -1);

		Assert.assertEquals(-1, result);

		// Start with '+'

		result = Conversions.getInteger("+123", -1);

		Assert.assertEquals(123, result);

		// Start with '-'

		result = Conversions.getInteger("-123", -1);

		Assert.assertEquals(-123, result);

		// Maximum int

		result = Conversions.getInteger(
			Integer.toString(Integer.MAX_VALUE), -1);

		Assert.assertEquals(Integer.MAX_VALUE, result);

		// Minimum int

		result = Conversions.getInteger(
			Integer.toString(Integer.MIN_VALUE), -1);

		Assert.assertEquals(Integer.MIN_VALUE, result);

		// Larger than maximum int

		result = Conversions.getInteger(
			Integer.toString(Integer.MAX_VALUE) + "0", -1);

		Assert.assertEquals(-1, result);

		// Smaller than minimum int

		result = Conversions.getInteger(
			Integer.toString(Integer.MIN_VALUE) + "0", -1);

		Assert.assertEquals(-1, result);
	}

	@Test
	public void testGetLong() {

		// Wrong first char

		long result = Conversions.getLong("e123", -1L);

		Assert.assertEquals(-1L, result);

		// Wrong middle char

		result = Conversions.getLong("12e3", -1L);

		Assert.assertEquals(-1L, result);

		// Start with '+'

		result = Conversions.getLong("+123", -1L);

		Assert.assertEquals(123L, result);

		// Start with '-'

		result = Conversions.getLong("-123", -1L);

		Assert.assertEquals(-123L, result);

		// Maximum long

		result = Conversions.getLong(Long.toString(Long.MAX_VALUE), -1L);

		Assert.assertEquals(Long.MAX_VALUE, result);

		// Minimum long

		result = Conversions.getLong(Long.toString(Long.MIN_VALUE), -1L);

		Assert.assertEquals(Long.MIN_VALUE, result);

		// Larger than maximum long

		result = Conversions.getLong(Long.toString(Long.MAX_VALUE) + "0", -1L);

		Assert.assertEquals(-1L, result);

		// Smaller than minimum long

		result = Conversions.getLong(Long.toString(Long.MIN_VALUE) + "0", -1L);

		Assert.assertEquals(-1L, result);
	}

	@Test
	public void testGetShort() {

		// Wrong first char

		short result = Conversions.getShort("e123", (short)-1);

		Assert.assertEquals((short)-1, result);

		// Wrong middle char

		result = Conversions.getShort("12e3", (short)-1);

		Assert.assertEquals((short)-1, result);

		// Start with '+'

		result = Conversions.getShort("+123", (short)-1);

		Assert.assertEquals((short)123, result);

		// Start with '-'

		result = Conversions.getShort("-123", (short)-1);

		Assert.assertEquals((short)-123, result);

		// Maximum short

		result = Conversions.getShort(
			Short.toString(Short.MAX_VALUE), (short)-1);

		Assert.assertEquals(Short.MAX_VALUE, result);

		// Minimum short

		result = Conversions.getShort(
			Short.toString(Short.MIN_VALUE), (short)-1);

		Assert.assertEquals(Short.MIN_VALUE, result);

		// Larger than maximum short

		result = Conversions.getShort(
			Short.toString(Short.MAX_VALUE) + "0", (short)-1);

		Assert.assertEquals((short)-1, result);

		// Smaller than minimum short

		result = Conversions.getShort(
			Short.toString(Short.MIN_VALUE) + "0", (short)-1);

		Assert.assertEquals((short)-1, result);
	}

	@Test
	public void testGetString() {
		Assert.assertEquals(
			StringPool.BLANK, Conversions.getString(StringPool.BLANK));
		Assert.assertEquals(
			Conversions.DEFAULT_STRING, Conversions.getString(null));
		Assert.assertEquals("default", Conversions.getString(null, "default"));
		Assert.assertEquals(
			"default", Conversions.getString(new Object(), "default"));
		Assert.assertEquals("test", Conversions.getString("test"));
	}

	@Test
	public void testMapPasswordEscaping() {
		Map<String, Object> map = new HashMap<>();
		map.put("password", "secret");

		Assert.assertEquals("{password=********}", Conversions.getString(map));

		map = new HashMap<>();
		map.put("Password", "secret");

		Assert.assertEquals("{Password=********}", Conversions.getString(map));

		map = new HashMap<>();
		map.put("somepASSwordString", "secret");

		Assert.assertEquals(
			"{somepASSwordString=********}", Conversions.getString(map));
	}

}
package com.liferay.messaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Rule;
import org.osgi.util.converter.StandardConverter;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;

public class FelixConverterGetterUtilCompatibilityTest {

	@Before
	public void setUp() {
		ConverterBuilder builder = new StandardConverter().newConverterBuilder();

		final Pattern passwordPattern = Pattern.compile("(?i).*password.*");
		final class PasswordMap extends LinkedHashMap<Object, Object> {
			public PasswordMap(Map<?, ?> map) {
				super(map);
			}
		}

		_converter = builder.
			rule(
				new Rule<String, Boolean>(
					o -> {
						if (Arrays.asList("true", "t", "y", "on", "1").contains(o)) {
							return true;
						}
						return false;
					}
				) {}
			).
			rule(
				new Rule<Map<?, ?>, String>(
					o -> {
						if (o instanceof PasswordMap) {
							return null;
						}
						Map<Object, Object> copy = new PasswordMap(o);
						for (Map.Entry<Object, Object> entry : copy.entrySet()) {
							Matcher matcher = passwordPattern.matcher(String.valueOf(entry.getKey()));

							if (matcher.matches()) {
								entry.setValue("********");
							}
						}
						return _converter.convert(copy).to(String.class);
					}
				) {}
			).
			rule(
				new Rule<Object, String>(
					o -> {
						if (o.getClass().equals(Object.class)) {
							throw new RuntimeException();
						}
						return null;
					}
				) {}
			).
			build();
	}

	private Converter _converter;

	@Test
	public void testGetBoolean() {
		Assert.assertFalse(_converter.convert("false").to(boolean.class));
		Assert.assertTrue(_converter.convert("true").to(boolean.class));
		Assert.assertFalse(_converter.convert(Boolean.FALSE).to(boolean.class));
		Assert.assertTrue(_converter.convert(Boolean.TRUE).to(boolean.class));
		Assert.assertFalse(_converter.convert(null).defaultValue(false).to(boolean.class));
		Assert.assertTrue(_converter.convert(null).defaultValue(true).to(boolean.class));
		Assert.assertFalse(_converter.convert(StringPool.BLANK).to(boolean.class));
		Assert.assertFalse(_converter.convert(StringPool.BLANK).defaultValue(false).to(boolean.class));
		Assert.assertFalse(_converter.convert(StringPool.BLANK).defaultValue(true).to(boolean.class));

		for (String s : GetterUtil.BOOLEANS) {
			Assert.assertTrue(_converter.convert(s).defaultValue(true).to(boolean.class));
			Assert.assertTrue(_converter.convert(s).defaultValue(true).to(boolean.class));
			Assert.assertTrue(_converter.convert(s).defaultValue(false).to(boolean.class));
		}
	}

	@Test
	public void testGetDouble() {

		// Wrong first char

		Assert.assertEquals(
			GetterUtil.DEFAULT_DOUBLE, _converter.convert("e12.3").defaultValue(GetterUtil.DEFAULT_DOUBLE).to(double.class),
			GetterUtil.DEFAULT_DOUBLE);

		// Wrong middle char

		Assert.assertEquals(
			GetterUtil.DEFAULT_DOUBLE, _converter.convert("12e.3").defaultValue(GetterUtil.DEFAULT_DOUBLE).to(double.class),
			GetterUtil.DEFAULT_DOUBLE);

		// Start with '+'

		Assert.assertEquals(
			12.3, _converter.convert("+12.3").to(double.class), GetterUtil.DEFAULT_DOUBLE);

		// Start with '-'

		Assert.assertEquals(
			-12.3, _converter.convert("-12.3").to(double.class), GetterUtil.DEFAULT_DOUBLE);

		// Maximum double

		Assert.assertEquals(
			Double.MAX_VALUE,
			_converter.convert(Double.toString(Double.MAX_VALUE)).to(double.class),
			GetterUtil.DEFAULT_DOUBLE);

		// Minimum double

		Assert.assertEquals(
			Double.MIN_VALUE,
			_converter.convert(Double.toString(Double.MIN_VALUE)).to(double.class),
			GetterUtil.DEFAULT_DOUBLE);

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
			GetterUtil.DEFAULT_DOUBLE);
		*/
	}

	@Test
	public void testGetInteger() {

		// Wrong first char

		int result = _converter.convert("e123").defaultValue(-1).to(int.class);

		Assert.assertEquals(-1, result);

		// Wrong middle char

		result = _converter.convert("12e3").defaultValue(-1).to(int.class);

		Assert.assertEquals(-1, result);

		// Start with '+'

		result = _converter.convert("+123").defaultValue(-1).to(int.class);

		Assert.assertEquals(123, result);

		// Start with '-'

		result = _converter.convert("-123").defaultValue(-1).to(int.class);

		Assert.assertEquals(-123, result);

		// Maximum int

		result = _converter.convert(Integer.toString(Integer.MAX_VALUE)).defaultValue(-1).to(int.class);

		Assert.assertEquals(Integer.MAX_VALUE, result);

		// Minimum int

		result = _converter.convert(Integer.toString(Integer.MIN_VALUE)).defaultValue(-1).to(int.class);

		Assert.assertEquals(Integer.MIN_VALUE, result);

		// Larger than maximum int

		result = _converter.convert(Integer.toString(Integer.MAX_VALUE) + "0").defaultValue(-1).to(int.class);

		Assert.assertEquals(-1, result);

		// Smaller than minimum int

		result = _converter.convert(Integer.toString(Integer.MIN_VALUE) + "0").defaultValue(-1).to(int.class);

		Assert.assertEquals(-1, result);
	}

	@Test
	public void testGetLong() {

		// Wrong first char

		long result = _converter.convert("e123").defaultValue(-1L).to(long.class);

		Assert.assertEquals(-1L, result);

		// Wrong middle char

		result = _converter.convert("12e3").defaultValue(-1L).to(long.class);

		Assert.assertEquals(-1L, result);

		// Start with '+'

		result = _converter.convert("+123").defaultValue(-1L).to(long.class);

		Assert.assertEquals(123L, result);

		// Start with '-'

		result = _converter.convert("-123").defaultValue(-1L).to(long.class);

		Assert.assertEquals(-123L, result);

		// Maximum long

		result = _converter.convert(Long.toString(Long.MAX_VALUE)).defaultValue(-1L).to(long.class);

		Assert.assertEquals(Long.MAX_VALUE, result);

		// Minimum long

		result = _converter.convert(Long.toString(Long.MIN_VALUE)).defaultValue(-1L).to(long.class);

		Assert.assertEquals(Long.MIN_VALUE, result);

		// Larger than maximum long

		result = _converter.convert(Long.toString(Long.MAX_VALUE) + "0").defaultValue(-1L).to(long.class);

		Assert.assertEquals(-1L, result);

		// Smaller than minimum long

		result = _converter.convert(Long.toString(Long.MIN_VALUE) + "0").defaultValue(-1L).to(long.class);

		Assert.assertEquals(-1L, result);
	}

	@Test
	public void testGetShort() {

		// Wrong first char

		short result = _converter.convert("e123").defaultValue((short)-1).to(short.class);

		Assert.assertEquals((short)-1, result);

		// Wrong middle char

		result = _converter.convert("12e3").defaultValue((short)-1).to(short.class);

		Assert.assertEquals((short)-1, result);

		// Start with '+'

		result = _converter.convert("+123").defaultValue((short)-1).to(short.class);

		Assert.assertEquals((short)123, result);

		// Start with '-'

		result = _converter.convert("-123").defaultValue((short)-1).to(short.class);

		Assert.assertEquals((short)-123, result);

		// Maximum short

		result = _converter.convert(Short.toString(Short.MAX_VALUE)).defaultValue((short)-1).to(short.class);

		Assert.assertEquals(Short.MAX_VALUE, result);

		// Minimum short

		result = _converter.convert(Short.toString(Short.MIN_VALUE)).defaultValue((short)-1).to(short.class);

		Assert.assertEquals(Short.MIN_VALUE, result);

		// Larger than maximum short

		result = _converter.convert(
			Short.toString(Short.MAX_VALUE) + "0").defaultValue((short)-1).to(short.class);

		Assert.assertEquals((short)-1, result);

		// Smaller than minimum short

		result = _converter.convert(
			Short.toString(Short.MIN_VALUE) + "0").defaultValue((short)-1).to(short.class);

		Assert.assertEquals((short)-1, result);
	}

	@Test
	public void testGetString() {
		Assert.assertEquals(
			StringPool.BLANK,
			_converter.convert(StringPool.BLANK).defaultValue(StringPool.BLANK).to(String.class));
		Assert.assertEquals(
			GetterUtil.DEFAULT_STRING,
			_converter.convert(null).defaultValue(GetterUtil.DEFAULT_STRING).to(String.class));
		Assert.assertEquals(
			"default", _converter.convert(null).defaultValue("default").to(String.class));
		Assert.assertEquals(
			"default", _converter.convert(new Object()).defaultValue("default").to(String.class));
		Assert.assertEquals("test", _converter.convert("test").to(String.class));
	}

	@Test
	public void testMapPasswordEscaping() {
		Map<String, Object> map = new HashMap<>();
		map.put("password", "secret");
		Assert.assertEquals("{password=********}", _converter.convert(map).to(String.class));

		map = new HashMap<>();
		map.put("Password", "secret");
		Assert.assertEquals("{Password=********}", _converter.convert(map).to(String.class));

		map = new HashMap<>();
		map.put("somepASSwordString", "secret");
		Assert.assertEquals("{somepASSwordString=********}", _converter.convert(map).to(String.class));
	}

}

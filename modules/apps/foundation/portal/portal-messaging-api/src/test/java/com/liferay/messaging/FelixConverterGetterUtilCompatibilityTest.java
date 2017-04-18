package com.liferay.messaging;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.StandardConverter;
import org.osgi.util.converter.TypeRule;
import org.osgi.util.function.Function;

import com.liferay.portal.kernel.util.GetterUtil;

public class FelixConverterGetterUtilCompatibilityTest {
	
	/*
	 * TODO: Decide where these builder functions that return conversion
	 * functions should live. E.g., buildToStringConversionFunction(String).
	 * 
	 * TODO: Decide where the helper functions (e.g., get(String, boolean) and
	 * _parseInt(String, int) should live.
	 */

	public Function<Object, String> buildToStringConversionFunction(
			String defaultValue) {

		Function<Object, String> conversionFunction = o -> {
			if (!(o instanceof String)) {
				return defaultValue;
			}
			return (String) o;
		};
		return conversionFunction;
	}
		
	public Function<Object, Double> buildToDoubleConversionFunction(
			double defaultValue) {

		Function<Object, Double> conversionFunction = o -> {
			if (o instanceof String) {
				/*
				 * TODO: Must not invoke any outside function. This call to a
				 * static helper function must go. Maybe move the contents of
				 * the get function inside here somehow.
				 */
				double value = get((String)o, 0.0);
				return value;
			}

			if (o instanceof Double) {
				return (Double)o;
			}

			if (o instanceof Number) {
				Number number = (Number)o;

				return number.doubleValue();
			}

			return defaultValue;
		};
		return conversionFunction;
	}
		
	public Function<Object, Integer> buildToIntegerConversionFunction(
			int defaultValue) {

		Function<Object, Integer> conversionFunction = o -> {
			if (o instanceof String) {
				/*
				 * TODO: Must not invoke any outside function. This call to a
				 * static helper function must go. Maybe move the contents of
				 * the parse function inside here somehow.
				 */
				int value = _parseInt(((String)o).trim(), 0);
				return value;
			}

			if (o instanceof Integer) {
				return (Integer)o;
			}

			if (o instanceof Number) {
				Number number = (Number)o;

				return number.intValue();
			}

			return defaultValue;
		};
		return conversionFunction;
	}
		
	public Function<Object, Long> buildToLongConversionFunction(
			long defaultValue) {

		Function<Object, Long> conversionFunction = o -> {
			if (o instanceof String) {
				/*
				 * TODO: Must not invoke any outside function. This call to a
				 * static helper function must go. Maybe move the contents of
				 * the parse function inside here somehow.
				 */
				long value = _parseLong(((String)o).trim(), 0);
				return value;
			}

			if (o instanceof Long) {
				return (Long)o;
			}

			if (o instanceof Number) {
				Number number = (Number)o;

				return number.longValue();
			}

			return defaultValue;
		};
		return conversionFunction;
	}
		
	public Function<Object, Boolean> buildToBooleanConversionFunction(
			boolean defaultValue) {

		Function<Object, Boolean> conversionFunction = o -> {
			if (o instanceof String) {
				/*
				 * TODO: Must not invoke any outside function. This call to a
				 * static helper function must go. Maybe move the contents of
				 * the get function inside here somehow.
				 */
				boolean value = get((String)o, false);
				return value;
			}
			if (o instanceof Boolean) {
				return (Boolean)o;
			}
			return false;
		};
		return conversionFunction;
	}
		
	@Before
	public void setUp() {
		StandardConverter standardConverter = new StandardConverter();
		
		ConverterBuilder stringConverterBuilder =
				standardConverter.newConverterBuilder();
		stringConverterBuilder = stringConverterBuilder.rule(
				new TypeRule<Object, String>(
						Object.class, String.class,
						buildToStringConversionFunction("")));
		_stringConverter = stringConverterBuilder.build();

		ConverterBuilder doubleConverterBuilder =
				standardConverter.newConverterBuilder();
		doubleConverterBuilder =
				doubleConverterBuilder.rule(
						new TypeRule<Object, Double>(
								Object.class, Double.class,
								buildToDoubleConversionFunction(0.0)));
		_doubleConverter = doubleConverterBuilder.build();

		ConverterBuilder intConverterBuilder = standardConverter.newConverterBuilder();
		intConverterBuilder =
				intConverterBuilder.rule(
						new TypeRule<Object, Integer>(
								Object.class, Integer.class,
								buildToIntegerConversionFunction(0)));
		_intConverter = intConverterBuilder.build();

		ConverterBuilder longConverterBuilder = standardConverter.newConverterBuilder();
		longConverterBuilder =
				longConverterBuilder.rule(
						new TypeRule<Object, Long>(
								Object.class, Long.class,
								buildToLongConversionFunction(0L)));
		_longConverter = longConverterBuilder.build();

		ConverterBuilder booleanConverterBuilder = standardConverter.newConverterBuilder();
		booleanConverterBuilder =
				booleanConverterBuilder.rule(
						new TypeRule<Object, Boolean>(
								Object.class, Boolean.class,
								buildToBooleanConversionFunction(false)));
		_booleanConverter = booleanConverterBuilder.build();
	}

	@Test
	public void testStringConversion() {
		String string = "abc";
		String getterString = GetterUtil.getString(string);
		String converterString = _stringConverter.convert(string).to(String.class);
		Assert.assertEquals(getterString, converterString);

		Object object = new Object();
		String getterObjectString = GetterUtil.getString(object);
		String converterObjectString = _stringConverter.convert(object).to(String.class);
		Assert.assertEquals(getterObjectString, converterObjectString);
		
		/*
		// Test getter util vs standard converter fails
		String standardConverterObjectString = new StandardConverter().convert(object).to(String.class);
		Assert.assertEquals(getterObjectString, standardConverterObjectString);
		*/
	}

	@Test
	public void testDoubleConversion() {
		List<String> doubleStrings = new ArrayList<String>();
		String doubleString1 = "";
		doubleStrings.add(doubleString1);
		String doubleString2 = "0";
		doubleStrings.add(doubleString2);
		String doubleString3 = "0.0";
		doubleStrings.add(doubleString3);
		String doubleString4 = "12.345";
		doubleStrings.add(doubleString4);
		String doubleString5 = "-12.345";
		doubleStrings.add(doubleString5);
		String doubleString6 = "- 12.345";
		doubleStrings.add(doubleString6);
		String doubleString7 = "0.12345";
		doubleStrings.add(doubleString7);
		String doubleString8 = "-0.12345";
		doubleStrings.add(doubleString8); String doubleString9 = "abc";
		doubleStrings.add(doubleString9);
		String doubleString10 = "abc.ef";
		doubleStrings.add(doubleString10);
		
		double delta = 1e-10; // TODO: Choose a standard delta value to use instead of this arbitrary one
		for (String doubleString : doubleStrings) {
			double getterDouble = GetterUtil.getDouble(doubleString);
			double converterDouble = _doubleConverter.convert(doubleString).to(Double.class);
			Assert.assertEquals(getterDouble, converterDouble, delta);

			/*
			// Test getter util vs standard converter fails
			double standardConverterDouble = new StandardConverter().convert(doubleString).to(Double.class);
			Assert.assertEquals(getterDouble, standardConverterDouble, delta);
			*/
		}

		List<Float> floats = new ArrayList<Float>();
		float float1 = 0.0f;
		floats.add(float1);
		float float2 = 12.345f;
		floats.add(float2);
		float float3 = -12.345f;
		floats.add(float3);
		float float4 = - 12.345f;
		floats.add(float4);
		float float5 = 0.12345f;
		floats.add(float5);
		float float6 = -0.12345f;
		floats.add(float6);

		for (float f : floats) {
			float getterFloat = GetterUtil.getFloat(f);
			float converterFloat = _doubleConverter.convert(f).to(Float.class);
			Assert.assertEquals(getterFloat, converterFloat, delta);
		}
	}

	@Test
	public void testIntConversion() {
		List<String> intStrings = new ArrayList<String>();
		String intString1 = "";
		intStrings.add(intString1);
		String intString2 = "0";
		intStrings.add(intString2);
		String intString3 = "0.0";
		intStrings.add(intString3);
		String intString4 = "12.345";
		intStrings.add(intString4);
		String intString5 = "-12.345";
		intStrings.add(intString5);
		String intString6 = "- 12.345";
		intStrings.add(intString6);
		String intString7 = "0.12345";
		intStrings.add(intString7);
		String intString8 = "-0.12345";
		intStrings.add(intString8);
		String intString9 = "abc";
		intStrings.add(intString9);
		String intString10 = "12345";
		intStrings.add(intString10);
		String intString11 = "-12345";
		intStrings.add(intString11);
		
		for (String intString : intStrings) {
			int getterInt = GetterUtil.getInteger(intString);
			int converterInt = _intConverter.convert(intString).to(Integer.class);
			Assert.assertEquals(getterInt, converterInt);

			/*
			// Test getter util vs standard converter fails
			int standardConverterInt = new StandardConverter().convert(intString).to(Integer.class);
			Assert.assertEquals(getterInt, standardConverterInt);
			*/
		}
		
		List<Integer> ints = new ArrayList<Integer>();
		int int1 = 0;
		ints.add(int1);
		int int2 = 42;
		ints.add(int2);
		int int3 = -42;
		ints.add(int3);
		
		for (int i : ints) {
			int getterInt = GetterUtil.getInteger(i);
			int converterInt = _intConverter.convert(i).to(Integer.class);
			Assert.assertEquals(getterInt, converterInt);
		}
		
		List<Long> longs = new ArrayList<Long>();
		long long1 = 0;
		longs.add(long1);
		long long2 = 42;
		longs.add(long2);
		long long3 = -42;
		longs.add(long3);
		
		for (long l : longs) {
			int getterInt = GetterUtil.getInteger(l);
			int converterInt = _intConverter.convert(l).to(Integer.class);
			Assert.assertEquals(getterInt, converterInt);
		}
		
		List<Float> floats = new ArrayList<Float>();
		float float1 = 0.0f;
		floats.add(float1);
		float float2 = 42.3f;
		floats.add(float2);
		float float3 = -42.3f;
		floats.add(float3);
		
		for (float l : floats) {
			int getterInt = GetterUtil.getInteger(l);
			int converterInt = _intConverter.convert(l).to(Integer.class);
			Assert.assertEquals(getterInt, converterInt);
		}
		
		List<Double> doubles = new ArrayList<Double>();
		double double1 = 0.0;
		doubles.add(double1);
		double double2 = 42.3;
		doubles.add(double2);
		double double3 = -42.3;
		doubles.add(double3);
		
		for (double l : doubles) {
			int getterInt = GetterUtil.getInteger(l);
			int converterInt = _intConverter.convert(l).to(Integer.class);
			Assert.assertEquals(getterInt, converterInt);
		}
	}

	@Test
	public void testLongConversion() {
		List<String> intStrings = new ArrayList<String>();
		String intString1 = "";
		intStrings.add(intString1);
		String intString2 = "0";
		intStrings.add(intString2);
		String intString3 = "0.0";
		intStrings.add(intString3);
		String intString4 = "12.345";
		intStrings.add(intString4);
		String intString5 = "-12.345";
		intStrings.add(intString5);
		String intString6 = "- 12.345";
		intStrings.add(intString6);
		String intString7 = "0.12345";
		intStrings.add(intString7);
		String intString8 = "-0.12345";
		intStrings.add(intString8);
		String intString9 = "abc";
		intStrings.add(intString9);
		String intString10 = "12345";
		intStrings.add(intString10);
		String intString11 = "-12345";
		intStrings.add(intString11);
		
		for (String intString : intStrings) {
			long getterLong = GetterUtil.getLong(intString);
			long converterLong = _longConverter.convert(intString).to(Long.class);
			Assert.assertEquals(getterLong, converterLong);

			/*
			// Test getter util vs standard converter fails
			long standardConverterLong = new StandardConverter().convert(intString).to(Long.class);
			Assert.assertEquals(getterLong, standardConverterLong);
			*/
		}
		
		List<Integer> ints = new ArrayList<Integer>();
		int int1 = 0;
		ints.add(int1);
		int int2 = 42;
		ints.add(int2);
		int int3 = -42;
		ints.add(int3);
		
		for (int i : ints) {
			long getterLong = GetterUtil.getLong(i);
			long converterLong = _longConverter.convert(i).to(Long.class);
			Assert.assertEquals(getterLong, converterLong);
		}
		
		List<Long> longs = new ArrayList<Long>();
		long long1 = 0;
		longs.add(long1);
		long long2 = 42;
		longs.add(long2);
		long long3 = -42;
		longs.add(long3);
		
		for (long l : longs) {
			long getterLong = GetterUtil.getLong(l);
			long converterLong = _longConverter.convert(l).to(Long.class);
			Assert.assertEquals(getterLong, converterLong);
		}
		
		List<Float> floats = new ArrayList<Float>();
		float float1 = 0.0f;
		floats.add(float1);
		float float2 = 42.3f;
		floats.add(float2);
		float float3 = -42.3f;
		floats.add(float3);
		
		for (float l : floats) {
			long getterLong = GetterUtil.getLong(l);
			long converterLong = _longConverter.convert(l).to(Long.class);
			Assert.assertEquals(getterLong, converterLong);
		}
		
		List<Double> doubles = new ArrayList<Double>();
		double double1 = 0.0;
		doubles.add(double1);
		double double2 = 42.3;
		doubles.add(double2);
		double double3 = -42.3;
		doubles.add(double3);
		
		for (double l : doubles) {
			long getterLong = GetterUtil.getLong(l);
			long converterLong = _longConverter.convert(l).to(Long.class);
			Assert.assertEquals(getterLong, converterLong);
		}
	}

	@Test
	public void testBooleanConversion() {
		List<String> strings = new ArrayList<String>();
		String string1 = "TRUE";
		strings.add(string1);
		String string2 = "FALSE";
		strings.add(string2);
		String string3 = "true";
		strings.add(string3);
		String string4 = "false";
		strings.add(string4);
		String string5 = "TrUe";
		strings.add(string5);
		String string6 = "fAlSe";
		strings.add(string6);
		String string7 = "abc";
		strings.add(string7);
		String string8 = "AbC";
		strings.add(string8);

		for (int i = 0; i < strings.size(); i++) {
			boolean getterBoolean = GetterUtil.getBoolean(strings.get(i), false);
			boolean converterBoolean = _booleanConverter.convert(strings.get(i)).to(Boolean.class);
			Assert.assertEquals(getterBoolean, converterBoolean);

			// Test getter util vs standard converter fails
			boolean standardConverterBoolean = new StandardConverter().convert(strings.get(i)).to(Boolean.class);
			Assert.assertEquals(getterBoolean, standardConverterBoolean);
		}
		
		boolean b1 = false;
		boolean getterBoolean = GetterUtil.getBoolean(b1, false);
		boolean converterBoolean = _booleanConverter.convert(b1).to(Boolean.class);
		Assert.assertEquals(getterBoolean, converterBoolean);

		boolean b2 = true;
		getterBoolean = GetterUtil.getBoolean(b2, false);
		converterBoolean = _booleanConverter.convert(b2).to(Boolean.class);
		Assert.assertEquals(getterBoolean, converterBoolean);
	}

	/**
	 * Returns the String value as a boolean. If the value is <code>null</code>,
	 * the default value is returned. If the value does not match a {@link
	 * #BOOLEANS} value, <code>false</code> is returned.
	 *
	 * @param  value the value to convert
	 * @param  defaultValue a default value
	 * @return the value as a boolean
	 */
	public static boolean get(String value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		value = value.trim();

		if (value.length() > 4) {
			return false;
		}

		if (value.length() == 4) {
			char c = value.charAt(0);

			if ((c != 't') && (c != 'T')) {
				return false;
			}

			c = value.charAt(1);

			if ((c != 'r') && (c != 'R')) {
				return false;
			}

			c = value.charAt(2);

			if ((c != 'u') && (c != 'U')) {
				return false;
			}

			c = value.charAt(3);

			if ((c != 'e') && (c != 'E')) {
				return false;
			}

			return true;
		}

		if (value.length() == 2) {
			char c = value.charAt(0);

			if ((c != 'o') && (c != 'O')) {
				return false;
			}

			c = value.charAt(1);

			if ((c != 'n') && (c != 'N')) {
				return false;
			}

			return true;
		}

		if (value.length() == 1) {
			char c = value.charAt(0);

			if ((c == '1') || (c == 't') || (c == 'T') || (c == 'y') ||
				(c == 'Y')) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the String value as a double. If the value is <code>null</code>
	 * or not convertible to a double, the default value is returned.
	 *
	 * @param  value the value to convert
	 * @param  defaultValue a default value
	 * @return the value as a double
	 */
	public static double get(String value, double defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		value = value.trim();

		try {
			return Double.parseDouble(value);
		}
		catch (Exception e) {
		}

		return defaultValue;
	}
	
	private static int _parseInt(String value, int defaultValue) {
		int length = value.length();

		if (length <= 0) {
			return defaultValue;
		}

		int pos = 0;
		int limit = -Integer.MAX_VALUE;
		boolean negative = false;

		char c = value.charAt(0);

		if (c < '0') {
			if (c == '-') {
				limit = Integer.MIN_VALUE;
				negative = true;
			}
			else if (c != '+') {
				return defaultValue;
			}

			if (length == 1) {
				return defaultValue;
			}

			pos++;
		}

		int smallLimit = limit / 10;

		int result = 0;

		while (pos < length) {
			if (result < smallLimit) {
				return defaultValue;
			}

			c = value.charAt(pos++);

			if ((c < '0') || (c > '9')) {
				return defaultValue;
			}

			int number = c - '0';

			result *= 10;

			if (result < (limit + number)) {
				return defaultValue;
			}

			result -= number;
		}

		if (negative) {
			return result;
		}
		else {
			return -result;
		}
	}
	
	private static long _parseLong(String value, long defaultValue) {
		int length = value.length();

		if (length <= 0) {
			return defaultValue;
		}

		int pos = 0;
		long limit = -Long.MAX_VALUE;
		boolean negative = false;

		char c = value.charAt(0);

		if (c < '0') {
			if (c == '-') {
				limit = Long.MIN_VALUE;
				negative = true;
			}
			else if (c != '+') {
				return defaultValue;
			}

			if (length == 1) {
				return defaultValue;
			}

			pos++;
		}

		long smallLimit = limit / 10;

		long result = 0;

		while (pos < length) {
			if (result < smallLimit) {
				return defaultValue;
			}

			c = value.charAt(pos++);

			if ((c < '0') || (c > '9')) {
				return defaultValue;
			}

			int number = c - '0';

			result *= 10;

			if (result < (limit + number)) {
				return defaultValue;
			}

			result -= number;
		}

		if (negative) {
			return result;
		}
		else {
			return -result;
		}
	}

	private Converter _booleanConverter;
	private Converter _doubleConverter;
	private Converter _stringConverter;
	private Converter _intConverter;
	private Converter _longConverter;

}

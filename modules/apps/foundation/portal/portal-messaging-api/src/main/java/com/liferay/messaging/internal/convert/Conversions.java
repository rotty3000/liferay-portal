package com.liferay.messaging.internal.convert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Rule;
import org.osgi.util.converter.StandardConverter;

public class Conversions {

	public static final String BLANK = "";
	public static final String[] BOOLEANS = {"true", "t", "y", "on", "1"};
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final double DEFAULT_DOUBLE = 0.0;
	public static final int DEFAULT_INTEGER = 0;
	public static final long DEFAULT_LONG = 0;
	public static final short DEFAULT_SHORT = 0;
	public static final String DEFAULT_STRING = "";
	public static final String PASSWORD_MASK = "********";
	
	public static <T> Set<T> setFromArray(T[] array) {
		// TODO: figure out how to use the Felix converter to convert from array to set
		// return _instance._converter.convert(array).defaultValue(new HashSet<T>()).to(Set<T>.class);
		if ((array == null ) || (array.length == 0)) {
			return new HashSet<T>();
		}

		Set<T> set = new HashSet<T>(array.length);

		for (int i = 0; i < array.length; i++) {
			set.add(array[i]);
		}

		return set;
	}

	public static <T> Set<T> setFromList(List<T> list) {
		// TODO: figure out how to use the Felix converter to convert from array to list
		// return _instance._converter.convert(list).defaultValue(new HashSet<T>()).to(Set<T>.class);
		if ((list == null) || (list.size() == 0)) {
			return new HashSet<T>();
		}

		Set<T> set = new HashSet<T>(list.size());

		for (T t : list) {
			set.add(t);
		}

		return set;
	}

	public static <T> T convert(Object object, T defaultValue, Class<T> clazz) {
		return _instance._converter.convert(object).defaultValue(defaultValue).to(clazz);
	}

	public static boolean getBoolean(Object object) {
		return getBoolean(object, DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(Object object, boolean defaultValue) {
		return convert(object, defaultValue, boolean.class);
	}

	public static double getDouble(Object object) {
		return getDouble(object, DEFAULT_DOUBLE);
	}

	public static double getDouble(Object object, double defaultValue) {
		return convert(object, defaultValue, double.class);
	}

	public static int getInteger(Object object) {
		return getInteger(object, DEFAULT_INTEGER);
	}

	public static int getInteger(Object object, int defaultValue) {
		return convert(object, defaultValue, int.class);
	}

	public static long getLong(Object object) {
		return getLong(object, DEFAULT_LONG);
	}

	public static long getLong(Object object, long defaultValue) {
		return convert(object, defaultValue, long.class);
	}

	public static short getShort(Object object) {
		return getShort(object, DEFAULT_SHORT);
	}

	public static short getShort(Object object, short defaultValue) {
		return convert(object, defaultValue, short.class);
	}

	public static String getString(Object object) {
		return getString(object, DEFAULT_STRING);
	}

	public static String getString(Object object, String defaultValue) {
		return convert(object, defaultValue, String.class);
	}

	private Conversions() {
		ConverterBuilder builder = new StandardConverter().newConverterBuilder();

		final Converter rootConverter = builder.
			rule(
				new Rule<String, Boolean>(
					o -> {
						if (Arrays.asList(BOOLEANS).contains(o)) {
							return true;
						}
						return false;
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

		builder = rootConverter.newConverterBuilder();

		_converter = builder.
			rule(
				new Rule<Map<String, Object>, String>(
					o -> {
						Map<String, Object> copy = new LinkedHashMap<>(o);
						for (Map.Entry<String, Object> entry : copy.entrySet()) {
							Matcher matcher = _passwordPattern.matcher(
								String.valueOf(entry.getKey()));

							if (matcher.matches()) {
								entry.setValue(PASSWORD_MASK);
							}
						}
						return rootConverter.convert(copy).to(String.class);
					}
				) {}
			).
			build();
	}

	private static final Conversions _instance = new Conversions();

	private static final Pattern _passwordPattern = Pattern.compile(
		"(?i).*password.*");

	private Converter _converter;

}

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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Converting;
import org.osgi.util.converter.Rule;
import org.osgi.util.converter.StandardConverter;

/**
 * @author Jesse Rao
 * @author Raymond Augé
 */
public class Conversions {

	public static final String[] BOOLEANS = {"true", "t", "y", "on", "1"};

	public static final boolean DEFAULT_BOOLEAN = false;

	public static final double DEFAULT_DOUBLE = 0.0;

	public static final int DEFAULT_INTEGER = 0;

	public static final long DEFAULT_LONG = 0;

	public static final short DEFAULT_SHORT = 0;

	public static final String DEFAULT_STRING = "";

	public static final String PASSWORD_MASK = "********";

	public static Converting convert(Object object) {
		return _instance._converter.convert(object);
	}

	public static <T> T convert(Object object, T defaultValue, Class<T> clazz) {
		Converting converting = _instance._converter.convert(object);

		converting.defaultValue(defaultValue);

		return converting.to(clazz);
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
		ConverterBuilder builder =
			new StandardConverter().newConverterBuilder();

		builder = builder.rule(
			new Rule<String, Boolean>(
				o -> {
					if (Arrays.asList(BOOLEANS).contains(o)) {
						return true;
					}

					return false;
				}) {
			}
		).rule(
			new Rule<Object, String>(
				o -> {
					Class<?> clazz = o.getClass();

					if (clazz.equals(Object.class)) {
						throw new RuntimeException();
					}

					return null;
				}) {
			}
		);

		final Converter rootConverter = builder.build();

		builder = rootConverter.newConverterBuilder();

		builder = builder.rule(
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
				}) {
			});

		_converter = builder.build();
	}

	private static final Conversions _instance = new Conversions();

	private static final Pattern _passwordPattern = Pattern.compile(
		"(?i).*password.*");

	private final Converter _converter;

}
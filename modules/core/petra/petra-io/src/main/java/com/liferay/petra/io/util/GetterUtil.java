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

package com.liferay.petra.io.util;

import com.liferay.petra.string.StringPool;

/**
 * Provides utility methods for reading values as various types.
 *
 * @author Brian Wing Shun Chan
 */
public class GetterUtil {
	
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final double DEFAULT_DOUBLE = 0.0;
	public static final int DEFAULT_INTEGER = 0;
	public static final long DEFAULT_LONG = 0;
	public static final String DEFAULT_STRING = StringPool.BLANK;
	
	public static boolean get(Object value, boolean defaultValue) {
		if (value instanceof String) {
			return get((String)value, defaultValue);
		}

		if (value instanceof Boolean) {
			return (Boolean)value;
		}

		return defaultValue;
	}
	
	public static double get(Object value, double defaultValue) {
		if (value instanceof String) {
			return get((String)value, defaultValue);
		}

		if (value instanceof Double) {
			return (Double)value;
		}

		if (value instanceof Number) {
			Number number = (Number)value;

			return number.doubleValue();
		}

		return defaultValue;
	}
	
	public static int get(Object value, int defaultValue) {
		if (value instanceof String) {
			return get((String)value, defaultValue);
		}

		if (value instanceof Integer) {
			return (Integer)value;
		}

		if (value instanceof Number) {
			Number number = (Number)value;

			return number.intValue();
		}

		return defaultValue;
	}
	
	public static long get(Object value, long defaultValue) {
		if (value instanceof String) {
			return get((String)value, defaultValue);
		}

		if (value instanceof Long) {
			return (Long)value;
		}

		if (value instanceof Number) {
			Number number = (Number)value;

			return number.longValue();
		}

		return defaultValue;
	}
	
	public static String get(Object value, String defaultValue) {
		if (value instanceof String) {
			return get((String)value, defaultValue);
		}

		return defaultValue;
	}
	
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
	
	public static boolean getBoolean(Object value) {
		return getBoolean(value, DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(Object value, boolean defaultValue) {
		return get(value, defaultValue);
	}

	public static boolean getBoolean(String value) {
		return getBoolean(value, DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(String value, boolean defaultValue) {
		return get(value, defaultValue);
	}
	
	public static double getDouble(Object value) {
		return getDouble(value, DEFAULT_DOUBLE);
	}

	public static double getDouble(Object value, double defaultValue) {
		return get(value, defaultValue);
	}
	
	public static int getInteger(Object value) {
		return getInteger(value, DEFAULT_INTEGER);
	}

	public static int getInteger(Object value, int defaultValue) {
		return get(value, defaultValue);
	}
	
	public static long getLong(Object value) {
		return getLong(value, DEFAULT_LONG);
	}

	public static long getLong(Object value, long defaultValue) {
		return get(value, defaultValue);
	}
	
	public static String getString(Object value) {
		return getString(value, DEFAULT_STRING);
	}

	public static String getString(Object value, String defaultValue) {
		return get(value, defaultValue);
	}
	
	public static String getString(String value) {
		return getString(value, DEFAULT_STRING);
	}

	public static String getString(String value, String defaultValue) {
		return get(value, defaultValue);
	}

}
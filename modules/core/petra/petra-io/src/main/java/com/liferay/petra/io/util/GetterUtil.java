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

/**
 * Provides utility methods for reading values as various types.
 *
 * @author Brian Wing Shun Chan
 */
public class GetterUtil {
	
	public static final boolean DEFAULT_BOOLEAN = false;
	
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

	public static boolean getBoolean(String value) {
		return getBoolean(value, DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(String value, boolean defaultValue) {
		return get(value, defaultValue);
	}

}
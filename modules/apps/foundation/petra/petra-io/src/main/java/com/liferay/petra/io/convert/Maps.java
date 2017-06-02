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

import java.util.Map;

/**
 * @author Jesse Rao
 * @author Raymond Augé
 */
public class Maps {

	public static String getString(Map<String, ?> map, String key) {
		return getString(map, key, "");
	}

	public static String getString(
		Map<String, ?> map, String key, String defaultValue) {

		Object value = map.get(key);

		if (value == null) {
			return defaultValue;
		}

		if (value instanceof String) {
			return Conversions.getString((String)value, defaultValue);
		}

		if (value instanceof String[]) {
			String[] array = (String[])value;

			if (array.length == 0) {
				return defaultValue;
			}

			return Conversions.getString(array[0], defaultValue);
		}

		return Conversions.getString(String.valueOf(value), defaultValue);
	}

	private Maps() {

		// No Instantiation

	}

}
package com.liferay.portal.messaging.internal.util;

import com.liferay.messaging.internal.convert.Conversions;

import java.util.Map;

public class MapUtil {

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

}

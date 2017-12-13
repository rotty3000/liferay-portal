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

import java.util.Map;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

public class MapUtil {
	
	public static boolean isEmpty(Map<?, ?> map) {
		if ((map == null) || map.isEmpty()) {
			return true;
		}

		return false;
	}
	
	public static String toString(Map<?, ?> map) {
		return toString(map, null, null);
	}

	public static String toString(
		Map<?, ?> map, String hideIncludesRegex, String hideExcludesRegex) {

		if (isEmpty(map)) {
			return StringPool.OPEN_CURLY_BRACE + StringPool.CLOSE_CURLY_BRACE;
		}

		StringBundler sb = new StringBundler(map.size() * 4 + 1);

		sb.append(StringPool.OPEN_CURLY_BRACE);

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();

			String keyString = String.valueOf(key);

			if (hideIncludesRegex != null) {
				if (!keyString.matches(hideIncludesRegex)) {
					value = "********";
				}
			}

			if (hideExcludesRegex != null) {
				if (keyString.matches(hideExcludesRegex)) {
					value = "********";
				}
			}

			sb.append(keyString);
			sb.append(StringPool.EQUAL);

			if (value instanceof Map<?, ?>) {
				sb.append(toString((Map<?, ?>)value));
			}
			else if (value instanceof String[]) {
				String valueString = StringUtil.merge(
					(String[])value, StringPool.COMMA_AND_SPACE);

				sb.append(
					StringPool.OPEN_BRACKET.concat(valueString).concat(
						StringPool.CLOSE_BRACKET));
			}
			else {
				sb.append(value);
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setStringAt(StringPool.CLOSE_CURLY_BRACE, sb.index() - 1);

		return sb.toString();
	}

}
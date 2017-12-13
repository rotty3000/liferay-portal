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

package com.liferay.petra.string;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringUtil {
	
	public static String extractDigits(String s) {
		if (s == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		char[] chars = s.toCharArray();

		for (char c : chars) {
			if (isDigit(c)) {
				sb.append(c);
			}
		}

		return sb.toString();
	}
	
	public static boolean isDigit(char c) {
		int x = c;

		if ((x >= _DIGIT_BEGIN) && (x <= _DIGIT_END)) {
			return true;
		}

		return false;
	}
	
	public static String merge(Object[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		if (array.length == 0) {
			return StringPool.BLANK;
		}

		if (array.length == 1) {
			return String.valueOf(array[0]);
		}

		StringBundler sb = new StringBundler(2 * array.length - 1);

		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(delimiter);
			}

			sb.append(String.valueOf(array[i]).trim());
		}

		return sb.toString();
	}
	
	public static String removeChars(String s, char... oldSubs) {
		if (s == null) {
			return null;
		}

		if (oldSubs == null) {
			return s;
		}

		StringBuilder sb = new StringBuilder(s.length());

		iterate:
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			for (int j = 0; j < oldSubs.length; j++) {
				if (c == oldSubs[j]) {
					continue iterate;
				}
			}

			sb.append(c);
		}

		if (s.length() == sb.length()) {
			return s;
		}

		return sb.toString();
	}

	public static List<String> split(String s) {
		return split(s, CharPool.COMMA);
	}

	public static List<String> split(String s, char delimiter) {
		if ((s == null) || s.isEmpty()) {
			return Collections.emptyList();
		}

		s = s.trim();

		if (s.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> elements = new ArrayList<>();

		_split(elements, s, delimiter);

		return elements;
	}

	private static void _split(List<String> values, String s, char delimiter) {
		int offset = 0;
		int pos;

		while ((pos = s.indexOf(delimiter, offset)) != -1) {
			if (offset < pos) {
				values.add(s.substring(offset, pos));
			}

			offset = pos + 1;
		}

		if (offset < s.length()) {
			values.add(s.substring(offset));
		}
	}
	
	private static final int _DIGIT_BEGIN = 48;

	private static final int _DIGIT_END = 57;

}
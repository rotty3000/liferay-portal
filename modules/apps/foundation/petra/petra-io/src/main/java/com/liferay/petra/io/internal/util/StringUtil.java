package com.liferay.petra.io.internal.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

	/**
	 * Splits the string <code>s</code> around the specified delimiter.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * splitLines("First;Second;Third", ';') returns {"First","Second","Third"}
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param  s the string to split
	 * @param  delimiter the delimiter
	 * @return the array of strings resulting from splitting string
	 *         <code>s</code> around the specified delimiter character, or an
	 *         empty string array if <code>s</code> is <code>null</code> or if
	 *         <code>s</code> is empty
	 */
	public static String[] split(String s, char delimiter) {
		if (Validator.isNull(s)) {
			return new String[0];
		}

		s = s.trim();

		if (s.length() == 0) {
			return new String[0];
		}

		List<String> nodeValues = new ArrayList<>();

		_split(nodeValues, s, 0, delimiter);

		return nodeValues.toArray(new String[nodeValues.size()]);
	}
	/**
	 * Splits the string <code>s</code> around the specified delimiter string.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <p>
	 * <pre>
	 * <code>
	 * splitLines("oneandtwoandthreeandfour", "and") returns {"one","two","three","four"}
	 * </code>
	 * </pre>
	 * </p>
	 *
	 * @param  s the string to split
	 * @param  delimiter the delimiter
	 * @return the array of strings resulting from splitting string
	 *         <code>s</code> around the specified delimiter string, or an empty
	 *         string array if <code>s</code> is <code>null</code> or equals the
	 *         delimiter
	 */
	public static String[] split(String s, String delimiter) {
		if (Validator.isNull(s) || (delimiter == null) ||
			delimiter.equals("")) {

			return new String[0];
		}

		s = s.trim();

		if (s.equals(delimiter)) {
			return new String[0];
		}

		if (delimiter.length() == 1) {
			return split(s, delimiter.charAt(0));
		}

		List<String> nodeValues = new ArrayList<>();

		int offset = 0;

		int pos = s.indexOf(delimiter, offset);

		while (pos != -1) {
			nodeValues.add(s.substring(offset, pos));

			offset = pos + delimiter.length();

			pos = s.indexOf(delimiter, offset);
		}

		if (offset < s.length()) {
			nodeValues.add(s.substring(offset));
		}

		return nodeValues.toArray(new String[nodeValues.size()]);
	}
	
	private static void _split(
		List<String> values, String s, int offset, char delimiter) {

		int pos = s.indexOf(delimiter, offset);

		while (pos != -1) {
			values.add(s.substring(offset, pos));

			offset = pos + 1;

			pos = s.indexOf(delimiter, offset);
		}

		if (offset < s.length()) {
			values.add(s.substring(offset));
		}
	}
	
}

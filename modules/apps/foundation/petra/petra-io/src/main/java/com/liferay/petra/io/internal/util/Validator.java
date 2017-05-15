package com.liferay.petra.io.internal.util;

public class Validator {

	/**
	 * Returns <code>true</code> if the string is not <code>null</code>, meaning
	 * it is not a <code>null</code> reference, an empty string, whitespace, or
	 * the string "<code>null</code>", with or without leading or trailing
	 * whitespace.
	 *
	 * @param  s the string to check
	 * @return <code>true</code> if the string is not <code>null</code>;
	 *         <code>false</code> otherwise
	 */
	public static boolean isNotNull(String s) {
		return !isNull(s);
	}

	/**
	 * Returns <code>true</code> if the string is <code>null</code>, meaning it
	 * is a <code>null</code> reference, an empty string, whitespace, or the
	 * string "<code>null</code>", with or without leading or trailing
	 * whitespace.
	 *
	 * @param  s the string to check
	 * @return <code>true</code> if the string is <code>null</code>;
	 *         <code>false</code> otherwise
	 */
	public static boolean isNull(String s) {
		if (s == null) {
			return true;
		}

		int counter = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == ' ') {
				continue;
			}
			else if (counter > 3) {
				return false;
			}

			if (counter == 0) {
				if (c != 'n') {
					return false;
				}
			}
			else if (counter == 1) {
				if (c != 'u') {
					return false;
				}
			}
			else if ((counter == 2) || (counter == 3)) {
				if (c != 'l') {
					return false;
				}
			}

			counter++;
		}

		if ((counter == 0) || (counter == 4)) {
			return true;
		}

		return false;
	}
}

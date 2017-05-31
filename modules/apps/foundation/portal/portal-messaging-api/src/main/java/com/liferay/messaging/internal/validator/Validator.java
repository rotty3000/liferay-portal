package com.liferay.messaging.internal.validator;

public class Validator {
	
	public static final char SPACE = ' ';
	public static final char LOWER_CASE_N = 'n';
	public static final char LOWER_CASE_U = 'u';
	public static final char LOWER_CASE_L = 'l';

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

			if (c == SPACE) {
				continue;
			}
			else if (counter > 3) {
				return false;
			}

			if (counter == 0) {
				if (c != LOWER_CASE_N) {
					return false;
				}
			}
			else if (counter == 1) {
				if (c != LOWER_CASE_U) {
					return false;
				}
			}
			else if ((counter == 2) || (counter == 3)) {
				if (c != LOWER_CASE_L) {
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

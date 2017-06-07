package com.liferay.petra.io.convert;

/**
 * @author Jesse Rao
 * @author Raymond Augé
 */
public class Arrays {

	public static boolean isEmpty(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		return false;
	}

	private Arrays() {

		// No Instantiation

	}

}

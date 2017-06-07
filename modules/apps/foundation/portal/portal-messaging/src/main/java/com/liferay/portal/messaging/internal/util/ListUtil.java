package com.liferay.portal.messaging.internal.util;

import java.util.List;

public class ListUtil {

	public static boolean isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

}

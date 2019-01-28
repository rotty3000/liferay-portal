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

package com.liferay.portal.osgi.web.wab.generator.internal.cdi;

import java.util.EnumSet;

/**
 * @author Raymond Augé
 */
public enum Discover {

	all, annotated, annotated_by_bean, none;

	public static void parse(
		String s, EnumSet<Discover> options, CDIAnnotations state) {

		if (s == null) {
			return;
		}

		boolean negation = false;

		if (s.startsWith("!")) {
			negation = true;

			s = s.substring(1);
		}

		Discover option = Discover.valueOf(s);

		if (negation) {
			options.remove(option);
		}
		else {
			options.add(option);
		}
	}

}
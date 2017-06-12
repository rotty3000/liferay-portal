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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.util.converter.Converting;
import org.osgi.util.converter.TypeReference;

/**
 * @author Jesse Rao
 * @author Raymond Augé
 */
public class Sets {

	public static <T> Set<T> from(List<T> list) {
		Converting converting = Conversions.convert(list);

		converting.defaultValue(new LinkedHashSet<T>());

		return converting.to(new TypeReference<Set<T>>() {});
	}

	public static <T> Set<T> from(T[] array) {
		Converting converting = Conversions.convert(array);

		converting.defaultValue(new LinkedHashSet<T>());

		return converting.to(new TypeReference<Set<T>>() {});
	}

}
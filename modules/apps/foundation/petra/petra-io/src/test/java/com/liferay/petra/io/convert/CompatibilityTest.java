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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jesse Rao
 * @author Raymond Augé
 */
public class CompatibilityTest {

	@Test
	public void test() {
		Map<String, Object> values = new HashMap<String, Object>();

		values.put("int", new Integer(42));
		values.put("double", new Double(42.0));

		List<Integer> list = new ArrayList<Integer>();

		list.add(1);
		list.add(2);
		list.add(3);

		values.put("list", list);
		values.put("password", "password");

		String conversionsString = Conversions.getString(values);

		Assert.assertEquals(
			"{password=********, double=42.0, list=[1, 2, 3], int=42}",
			conversionsString);
	}

}

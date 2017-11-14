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

package com.liferay.vulcan.test.internal.json;

import static com.liferay.vulcan.test.json.JsonElementType.ARRAY;
import static com.liferay.vulcan.test.json.JsonElementType.BOOLEAN;
import static com.liferay.vulcan.test.json.JsonElementType.NUMBER;
import static com.liferay.vulcan.test.json.JsonElementType.OBJECT;
import static com.liferay.vulcan.test.json.JsonElementType.OTHER;
import static com.liferay.vulcan.test.json.JsonElementType.STRING;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.liferay.vulcan.test.json.JsonElementType;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class JsonElementTypeTest {

	@Test
	public void testInvokingGetJsonElementTypeReturnsCorrectType() {
		JsonElement arrayJsonElement = new JsonArray();

		JsonElementType arrayJsonElementType =
			JsonElementType.getJsonElementType(arrayJsonElement);

		JsonElement booleanJsonElement = new JsonPrimitive(true);

		JsonElementType booleanJsonElementType =
			JsonElementType.getJsonElementType(booleanJsonElement);

		JsonElement numberJsonElement = new JsonPrimitive(42);

		JsonElementType numberJsonElementType =
			JsonElementType.getJsonElementType(numberJsonElement);

		JsonElement objectJsonElement = new JsonObject();

		JsonElementType objectJsonElementType =
			JsonElementType.getJsonElementType(objectJsonElement);

		JsonElement otherJsonElement = JsonNull.INSTANCE;

		JsonElementType otherJsonElementType =
			JsonElementType.getJsonElementType(otherJsonElement);

		JsonElement stringJsonElement = new JsonPrimitive(
			"Live long and prosper");

		JsonElementType stringJsonElementType =
			JsonElementType.getJsonElementType(stringJsonElement);

		assertThat(arrayJsonElementType, is(equalTo(ARRAY)));
		assertThat(booleanJsonElementType, is(equalTo(BOOLEAN)));
		assertThat(numberJsonElementType, is(equalTo(NUMBER)));
		assertThat(objectJsonElementType, is(equalTo(OBJECT)));
		assertThat(otherJsonElementType, is(equalTo(OTHER)));
		assertThat(stringJsonElementType, is(equalTo(STRING)));
	}

	@Test
	public void testInvokingGetReadableNameReturnsCorrectName() {
		assertThat(ARRAY.getReadableName(), is(equalTo("an array")));
		assertThat(BOOLEAN.getReadableName(), is(equalTo("a boolean")));
		assertThat(NUMBER.getReadableName(), is(equalTo("a number")));
		assertThat(OBJECT.getReadableName(), is(equalTo("an object")));
		assertThat(OTHER.getReadableName(), is(equalTo("other")));
		assertThat(STRING.getReadableName(), is(equalTo("a string")));
	}

}
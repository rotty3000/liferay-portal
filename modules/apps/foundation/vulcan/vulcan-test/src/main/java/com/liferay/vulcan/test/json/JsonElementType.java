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

package com.liferay.vulcan.test.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

/**
 * Instances of this enum, represent the different types a JSON object allows.
 *
 * @author Alejandro Hernández
 * @review
 */
public enum JsonElementType implements SelfDescribing {

	ARRAY, BOOLEAN, NUMBER, OBJECT, OTHER, STRING;

	/**
	 * Returns the correct {@code JsonElementType} for a {@code JsonElement}.
	 *
	 * @param  jsonElement the {@code JsonElement} whose type is wanted
	 * @return the correct {@code JsonElementType} for a {@code JsonElement}
	 * @review
	 */
	public static JsonElementType getJsonElementType(JsonElement jsonElement) {
		if (jsonElement.isJsonObject()) {
			return OBJECT;
		}

		if (jsonElement.isJsonArray()) {
			return ARRAY;
		}

		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

			if (jsonPrimitive.isBoolean()) {
				return BOOLEAN;
			}

			if (jsonPrimitive.isString()) {
				return STRING;
			}

			if (jsonPrimitive.isNumber()) {
				return NUMBER;
			}
		}

		return OTHER;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(getReadableName());
	}

	/**
	 * Returns the readable name of this type.
	 *
	 * @return the readable name of this type.
	 * @review
	 */
	public String getReadableName() {
		if (this == ARRAY) {
			return "an array";
		}
		else if (this == BOOLEAN) {
			return "a boolean";
		}
		else if (this == NUMBER) {
			return "a number";
		}
		else if (this == OBJECT) {
			return "an object";
		}
		else if (this == STRING) {
			return "a string";
		}

		return "other";
	}

}
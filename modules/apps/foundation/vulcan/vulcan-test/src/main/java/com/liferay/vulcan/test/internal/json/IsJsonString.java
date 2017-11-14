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

import static com.liferay.vulcan.test.json.JsonElementType.STRING;

import com.google.gson.JsonPrimitive;

import com.liferay.vulcan.test.json.AbstractJsonElementMatcher;

import java.util.Objects;

import org.hamcrest.Matcher;

/**
 * A {@link Matcher} that can be used to check if an element is a correct string
 * json primitive.
 *
 * @author Alejandro Hernández
 * @review
 */
public class IsJsonString
	extends AbstractJsonElementMatcher<JsonPrimitive, String> {

	public IsJsonString(final Matcher<String> stringMatcher) {
		super(STRING, stringMatcher, JsonPrimitive::getAsString);

		Objects.requireNonNull(stringMatcher);
	}

}
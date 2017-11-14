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

import static com.liferay.vulcan.test.json.JsonElementType.getJsonElementType;

import com.google.gson.JsonElement;

import java.util.Objects;
import java.util.function.Function;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Abstract class that can be inherited from to create a {@link Matcher} for a
 * type of {@link JsonElement}.
 *
 * @author Alejandro Hernández
 * @review
 */
public abstract class AbstractJsonElementMatcher<A extends JsonElement, B>
	extends TypeSafeDiagnosingMatcher<JsonElement> {

	public AbstractJsonElementMatcher(
		JsonElementType jsonElementType, Matcher<B> bMatcher,
		Function<A, B> transformFunction) {

		super(JsonElement.class);

		_jsonElementType = Objects.requireNonNull(jsonElementType);
		_bMatcher = bMatcher;
		_transformFunction = transformFunction;
	}

	@Override
	public void describeTo(Description description) {
		description.appendDescriptionOf(
			_jsonElementType
		).appendText(
			" element with a value that "
		).appendDescriptionOf(
			_bMatcher
		);
	}

	/**
	 * Returns the text that will be used when validation fails.
	 *
	 * @return the text to be used when validation fails.
	 * @review
	 */
	protected String getFailText() {
		return "was " + _jsonElementType.getReadableName() +
			" element with a value that ";
	}

	/**
	 * Returns the inner {@code Matcher} this {@code Matcher} uses.
	 *
	 * @return the inner {@code Matcher} this {@code Matcher} uses.
	 * @review
	 */
	protected Matcher<B> getInnerMatcher() {
		return _bMatcher;
	}

	@Override
	protected boolean matchesSafely(
		JsonElement jsonElement, Description description) {

		JsonElementType jsonElementType = getJsonElementType(jsonElement);

		if (jsonElementType.equals(_jsonElementType)) {
			@SuppressWarnings("unchecked") A a = (A)jsonElement;

			B value = _transformFunction.apply(a);

			if (_bMatcher.matches(value)) {
				return true;
			}

			description.appendText(getFailText());

			_bMatcher.describeMismatch(value, description);

			return false;
		}

		description.appendText(
			"was not "
		).appendDescriptionOf(
			_jsonElementType
		).appendText(
			" element, but "
		).appendDescriptionOf(
			jsonElementType
		).appendText(
			" element"
		);

		return false;
	}

	private final Matcher<B> _bMatcher;
	private final JsonElementType _jsonElementType;
	private final Function<A, B> _transformFunction;

}
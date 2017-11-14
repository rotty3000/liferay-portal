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

import static com.liferay.vulcan.test.json.JsonElementType.OBJECT;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.liferay.vulcan.test.internal.json.IsJsonObject;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * An instance of this class must be used to provide tests for a {@link
 * JsonMatchers#aJsonObjectStringWith(Conditions)} {@link Matcher}.
 *
 * <p>
 * This class shouldn't be instantiated directly. Use an instance of a {@link
 * Conditions.Builder} instead.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class Conditions
	extends AbstractJsonElementMatcher<JsonObject, JsonObject> {

	@Override
	public void describeTo(Description description) {
		description.appendText(
			"a JSON object where "
		).appendDescriptionOf(
			getInnerMatcher()
		);
	}

	/**
	 * An instance of this builder can be used to create {@link Conditions}.
	 *
	 * @review
	 */
	public static class Builder {

		public Builder() {
			this(new HashMap<>());
		}

		/**
		 * Constructs the {@link Conditions} instance with the information
		 * provided to the builder.
		 *
		 * @return the {@code Conditions} instance
		 * @review
		 */
		public Conditions build() {
			return new Conditions(this);
		}

		/**
		 * Adds a new {@link Matcher} for a field of the json object.
		 *
		 * @param  key the name of the field being tested
		 * @param  valueMatcher the matcher for testing the field value
		 * @return the next step of the builder
		 * @review
		 */
		public Builder where(
			String key, Matcher<? extends JsonElement> valueMatcher) {

			Map<String, Matcher<? extends JsonElement>> newMap = new HashMap<>(
				_entryMatchers);

			newMap.put(key, valueMatcher);

			return new Builder(newMap);
		}

		/**
		 * Deactivate strict mode for this checking.
		 *
		 * <p>
		 * Strict mode means that the json object being tested should not
		 * contain any element that is not being tested with a {@link
		 * #where(String, Matcher)} call.
		 * </p>
		 *
		 * @return the next step of the builder
		 * @review
		 */
		public Builder withStrictModeDeactivated() {
			return new Builder(_entryMatchers, false);
		}

		private Builder(
			Map<String, Matcher<? extends JsonElement>> entryMatchers) {

			this(entryMatchers, true);
		}

		private Builder(
			Map<String, Matcher<? extends JsonElement>> entryMatchers,
			boolean strictMode) {

			_entryMatchers = entryMatchers;
			_strictMode = strictMode;
		}

		private final Map<String, Matcher<? extends JsonElement>>
			_entryMatchers;
		private final boolean _strictMode;

	}

	@Override
	protected String getFailText() {
		return "";
	}

	private Conditions(Builder builder) {
		super(
			OBJECT,
			new IsJsonObject(builder._entryMatchers, builder._strictMode),
			JsonObject::getAsJsonObject);
	}

}
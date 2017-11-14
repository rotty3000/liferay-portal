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

package com.liferay.vulcan.message.json;

import static com.liferay.vulcan.test.json.JsonMatchers.aJsonArrayThat;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonBoolean;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonInt;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonObjectWhere;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Matcher;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class JSONObjectBuilderTest {

	@Test
	public void testInvokingAddAllOnAnArrayValueCreatesAValidJsonArray() {
		JSONObjectBuilder.ArrayValueStep arrayValueStep =
			_jsonObjectBuilder.field(
				"array"
			).arrayValue();

		List<Boolean> booleanList = new ArrayList<Boolean>() {
			{
				add(true);
				add(false);
			}
		};

		List<Number> numberList = new ArrayList<Number>() {
			{
				add(42);
				add(21);
			}
		};

		List<String> stringList = new ArrayList<String>() {
			{
				add("vulcan");
				add("api");
			}
		};

		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("solution", 42);

		Collection<JsonObject> jsonObjectList = new ArrayList<JsonObject>() {
			{
				add(jsonObject);
				add(jsonObject);
			}
		};

		arrayValueStep.addAllBooleans(booleanList);
		arrayValueStep.addAllNumbers(numberList);
		arrayValueStep.addAllStrings(stringList);
		arrayValueStep.addAllJsonObjects(jsonObjectList);

		@SuppressWarnings("unchecked")
		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(
				contains(
					aJsonBoolean(true), aJsonBoolean(false),
					aJsonInt(equalTo(42)), aJsonInt(equalTo(21)),
					aJsonString(equalTo("vulcan")), aJsonString(equalTo("api")),
					_aJsonObjectWithTheSolution, _aJsonObjectWithTheSolution)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddConsumerCreatesAValidJsonArray() {
		_jsonObjectBuilder.field(
			"array"
		).arrayValue(
		).add(
			jsonObjectBuilder -> jsonObjectBuilder.field(
				"solution"
			).numberValue(
				42
			)
		);

		@SuppressWarnings("unchecked")
		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(contains(_aJsonObjectWithTheSolution)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddJsonObjectBuilderCreatesAValidJsonArray() {
		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		jsonObjectBuilder.field(
			"solution"
		).numberValue(
			42
		);

		_jsonObjectBuilder.field(
			"array"
		).arrayValue(
		).add(
			jsonObjectBuilder
		);

		@SuppressWarnings("unchecked")
		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(contains(_aJsonObjectWithTheSolution)));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingAddOnAnArrayValueCreatesAValidJsonArray() {
		JSONObjectBuilder.ArrayValueStep arrayValueStep =
			_jsonObjectBuilder.field(
				"array"
			).arrayValue();

		arrayValueStep.addBoolean(true);
		arrayValueStep.addNumber(42);
		arrayValueStep.addString("vulcan");

		@SuppressWarnings("unchecked")
		Matcher<JsonElement> isAJsonArrayWithElements = is(
			aJsonArrayThat(
				contains(
					aJsonBoolean(true), aJsonInt(equalTo(42)),
					aJsonString(equalTo("vulcan")))));

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere("array", isAJsonArrayWithElements));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingArrayValueCreatesAJsonArray() {
		_jsonObjectBuilder.field(
			"array"
		).arrayValue();

		Matcher<JsonElement> isAJsonObjectWithAnArray = is(
			aJsonObjectWhere(
				"array", is(aJsonArrayThat(not(contains(anything()))))));

		assertThat(getJsonObject(), isAJsonObjectWithAnArray);
	}

	@Test
	public void testInvokingBooleanValueCreatesABoolean() {
		_jsonObjectBuilder.field(
			"solution"
		).booleanValue(
			true
		);

		Matcher<JsonElement> isAJsonObjectWithTheSolution = is(
			aJsonObjectWhere("solution", is(aJsonBoolean(true))));

		assertThat(getJsonObject(), isAJsonObjectWithTheSolution);
	}

	@Test
	public void testInvokingFalseIfElseConditionCreatesACorrectField() {
		_jsonObjectBuilder.ifElseCondition(
			false, builder -> builder.field("true"),
			builder -> builder.field("solution")
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	@Test
	public void testInvokingFieldAndFalseIfConditionCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"first"
		).ifCondition(
			false, builder -> builder.field("solution")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(aJsonInt(equalTo(42)))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldAndFalseIfElseConditionCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"first"
		).ifElseCondition(
			false, builder -> builder.field("true"),
			builder -> builder.field("solution")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(is(_aJsonObjectWithTheSolution))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldAndNestedPrefixedFieldCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"solution"
		).nestedPrefixedField(
			"prefix", "first", "second", "third"
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheSolution = is(
			aJsonObjectWhere("solution", isAJsonObjectWithTheFirstPrefix()));

		assertThat(getJsonObject(), isAJsonObjectWithTheSolution);
	}

	@Test
	public void testInvokingFieldAndNestedSuffixedFieldCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"solution"
		).nestedSuffixedField(
			"suffix", "first", "second", "third"
		).numberValue(
			42
		);

		assertThat(
			getJsonObject(),
			is(aJsonObjectWhere("solution", isAJsonObjectWithTheFirst())));
	}

	@Test
	public void testInvokingFieldAndTrueIfConditionCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"first"
		).ifCondition(
			true, builder -> builder.field("solution")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(is(_aJsonObjectWithTheSolution))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldAndTrueIfElseConditionCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"first"
		).ifElseCondition(
			true, builder -> builder.field("solution"),
			builder -> builder.field("false")
		).numberValue(
			42
		);

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", is(is(_aJsonObjectWithTheSolution))));

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst);
	}

	@Test
	public void testInvokingFieldCreatesACorrectField() {
		_jsonObjectBuilder.field(
			"solution"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	@Test
	public void testInvokingNestedFieldCreatesACorrectNestedField() {
		_jsonObjectBuilder.nestedField(
			"the", "solution"
		).numberValue(
			42
		);

		assertThat(
			getJsonObject(),
			is(aJsonObjectWhere("the", is(_aJsonObjectWithTheSolution))));
	}

	@Test
	public void testInvokingNestedPrefixedFieldCreatesACorrectField() {
		_jsonObjectBuilder.nestedPrefixedField(
			"prefix", "first", "second", "third"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), isAJsonObjectWithTheFirstPrefix());
	}

	@Test
	public void testInvokingNestedSuffixedFieldCreatesACorrectField() {
		_jsonObjectBuilder.nestedSuffixedField(
			"suffix", "first", "second", "third"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), isAJsonObjectWithTheFirst());
	}

	@Test
	public void testInvokingNumberValueCreatesANumber() {
		_jsonObjectBuilder.field(
			"solution"
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	@Test
	public void testInvokingStringValueCreatesAString() {
		_jsonObjectBuilder.field(
			"solution"
		).stringValue(
			"forty-two"
		);

		Matcher<JsonElement> isAJsonObjectWithTheSolution = is(
			aJsonObjectWhere(
				"solution", is(aJsonString(equalTo("forty-two")))));

		assertThat(getJsonObject(), isAJsonObjectWithTheSolution);
	}

	@Test
	public void testInvokingTrueIfElseConditionCreatesACorrectField() {
		_jsonObjectBuilder.ifElseCondition(
			true, builder -> builder.field("solution"),
			builder -> builder.field("false")
		).numberValue(
			42
		);

		assertThat(getJsonObject(), is(_aJsonObjectWithTheSolution));
	}

	protected JsonObject getJsonObject() {
		return _jsonObjectBuilder.build();
	}

	protected Matcher<JsonElement> isAJsonObjectWithTheFirst() {
		Matcher<JsonElement> isAJsonObjectWithTheThirdSuffix = is(
			aJsonObjectWhere("suffix", is(aJsonInt(equalTo(42)))));

		Matcher<JsonElement> isAJsonObjectWithTheThird = is(
			aJsonObjectWhere("third", isAJsonObjectWithTheThirdSuffix));

		Matcher<JsonElement> isAJsonObjectWithTheSecondSuffix = is(
			aJsonObjectWhere("suffix", isAJsonObjectWithTheThird));

		Matcher<JsonElement> isAJsonObjectWithTheSecond = is(
			aJsonObjectWhere("second", isAJsonObjectWithTheSecondSuffix));

		Matcher<JsonElement> isAJsonObjectWithTheFirstSuffix = is(
			aJsonObjectWhere("suffix", isAJsonObjectWithTheSecond));

		return is(aJsonObjectWhere("first", isAJsonObjectWithTheFirstSuffix));
	}

	protected Matcher<JsonElement> isAJsonObjectWithTheFirstPrefix() {
		Matcher<JsonElement> isAJsonObjectWithTheThird = is(
			aJsonObjectWhere("third", is(aJsonInt(equalTo(42)))));

		Matcher<JsonElement> isAJsonObjectWithTheThirdPrefix = is(
			aJsonObjectWhere("prefix", isAJsonObjectWithTheThird));

		Matcher<JsonElement> isAJsonObjectWithTheSecond = is(
			aJsonObjectWhere("second", isAJsonObjectWithTheThirdPrefix));

		Matcher<JsonElement> isAJsonObjectWithTheSecondPrefix = is(
			aJsonObjectWhere("prefix", isAJsonObjectWithTheSecond));

		Matcher<JsonElement> isAJsonObjectWithTheFirst = is(
			aJsonObjectWhere("first", isAJsonObjectWithTheSecondPrefix));

		return is(aJsonObjectWhere("prefix", isAJsonObjectWithTheFirst));
	}

	private final Matcher<JsonElement> _aJsonObjectWithTheSolution =
		aJsonObjectWhere("solution", is(aJsonInt(equalTo(42))));
	private final JSONObjectBuilder _jsonObjectBuilder =
		new JSONObjectBuilder();

}
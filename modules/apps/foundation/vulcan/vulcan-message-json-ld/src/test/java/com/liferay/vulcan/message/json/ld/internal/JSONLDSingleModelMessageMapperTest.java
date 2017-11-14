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

package com.liferay.vulcan.message.json.ld.internal;

import static com.liferay.vulcan.test.json.JsonMatchers.aJsonArrayThat;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonBoolean;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonInt;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonObjectWhere;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonObjectWith;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.test.json.Conditions;
import com.liferay.vulcan.test.message.MockModel;
import com.liferay.vulcan.test.message.MockSingleModelWriter;

import javax.ws.rs.core.HttpHeaders;

import org.hamcrest.Matcher;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier Gamarra
 * @author Alejandro Hernández
 */
public class JSONLDSingleModelMessageMapperTest {

	@Test
	public void testJSONLDSingleModelMessageMapper() {
		HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		MockSingleModelWriter.write(
			_singleModelMessageMapper, jsonObjectBuilder, httpHeaders);

		JsonObject jsonObject = jsonObjectBuilder.build();

		Conditions.Builder builder = new Conditions.Builder();

		Conditions conditions = builder.where(
			"@context", _isAJsonObjectWithTheContext
		).where(
			"@id", _isALinkTo("localhost:8080")
		).where(
			"@type", is(aJsonArrayThat(_containsTheTypes))
		).where(
			"boolean1", is(aJsonBoolean(true))
		).where(
			"boolean2", is(aJsonBoolean(false))
		).where(
			"first-linked", _isALinkTo("localhost:8080/first-linked")
		).where(
			"first-embedded", _isAJsonObjectWithTheFirstEmbedded
		).where(
			"link1", _isALinkTo("www.liferay.com")
		).where(
			"link2", _isALinkTo("community.liferay.com")
		).where(
			"number1", is(aJsonInt(equalTo(2017)))
		).where(
			"number2", is(aJsonInt(equalTo(42)))
		).where(
			"string1", is(aJsonString(equalTo("Live long and prosper")))
		).where(
			"string2", is(aJsonString(equalTo("Hypermedia")))
		).build();

		assertThat(jsonObject, is(aJsonObjectWith(conditions)));
	}

	@Test
	public void testMediaTypeIsCorrect() {
		String mediaType = _singleModelMessageMapper.getMediaType();

		assertThat(mediaType, is(equalTo("application/ld+json")));
	}

	private static Matcher<? extends JsonElement> _isALinkTo(String url) {
		return is(aJsonString(equalTo(url)));
	}

	@SuppressWarnings("unchecked")
	private static Matcher<Iterable<? extends JsonElement>> _containsTheTypes =
		contains(
			aJsonString(equalTo("Type 1")), aJsonString(equalTo("Type 2")));

	private static final Matcher<JsonElement> _isAJsonArrayWithTheType = is(
		aJsonArrayThat(contains(aJsonString(equalTo("Type")))));
	private static final Matcher<JsonElement> _isAJsonObjectWithTheContext;
	private static final Matcher<JsonElement>
		_isAJsonObjectWithTheFirstEmbedded;
	private static final Matcher<JsonElement> _isATypeIdContext = is(
		aJsonObjectWhere("@type", is(aJsonString(equalTo("@id")))));

	static {
		Conditions.Builder builder = new Conditions.Builder();

		Conditions secondEmbeddedConditions = builder.where(
			"@context", is(aJsonObjectWhere("third-linked", _isATypeIdContext))
		).where(
			"@id", _isALinkTo("localhost:8080/inner")
		).where(
			"@type", _isAJsonArrayWithTheType
		).where(
			"boolean", is(aJsonBoolean(true))
		).where(
			"link", _isALinkTo("www.liferay.com")
		).where(
			"number", is(aJsonInt(equalTo(42)))
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).where(
			"third-linked", _isALinkTo("localhost:8080/third-linked")
		).build();

		Matcher<JsonElement> isAJsonObjectWithTheSecondEmbedded = is(
			aJsonObjectWith(secondEmbeddedConditions));

		Conditions firstEmbeddedConditions = builder.where(
			"@context", is(aJsonObjectWhere("second-linked", _isATypeIdContext))
		).where(
			"@id", _isALinkTo("localhost:8080/inner")
		).where(
			"@type", _isAJsonArrayWithTheType
		).where(
			"boolean", is(aJsonBoolean(true))
		).where(
			"link", _isALinkTo("www.liferay.com")
		).where(
			"number", is(aJsonInt(equalTo(42)))
		).where(
			"second-embedded", isAJsonObjectWithTheSecondEmbedded
		).where(
			"second-linked", _isALinkTo("localhost:8080/second-linked")
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).build();

		_isAJsonObjectWithTheFirstEmbedded = is(
			aJsonObjectWith(firstEmbeddedConditions));

		Conditions contextConditions = builder.where(
			"@vocab", is(aJsonString(equalTo("http://schema.org")))
		).where(
			"first-linked", _isATypeIdContext
		).build();

		_isAJsonObjectWithTheContext = is(aJsonObjectWith(contextConditions));
	}

	private final JSONLDSingleModelMessageMapper<MockModel>
		_singleModelMessageMapper = new JSONLDSingleModelMessageMapper<>();

}
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

package com.liferay.vulcan.message.hal.internal;

import static com.liferay.vulcan.test.json.JsonMatchers.aJsonBoolean;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonInt;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonObjectWhere;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonObjectWith;
import static com.liferay.vulcan.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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
 * @author Alejandro Hernández
 */
public class HALSingleModelMessageMapperTest {

	@Test
	public void testHALSingleModelMessageMapper() {
		HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		MockSingleModelWriter.write(
			_singleModelMessageMapper, jsonObjectBuilder, httpHeaders);

		JsonObject jsonObject = jsonObjectBuilder.build();

		Conditions.Builder builder = new Conditions.Builder();

		Conditions conditions = builder.where(
			"_embedded", _isAJsonObjectWithTheEmbedded
		).where(
			"_links", _isAJsonObjectWithTheLinks
		).where(
			"boolean1", is(aJsonBoolean(true))
		).where(
			"boolean2", is(aJsonBoolean(false))
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

		assertThat(mediaType, is(equalTo("application/hal+json")));
	}

	private static Matcher<JsonElement> _isALinkTo(String url) {
		return is(aJsonObjectWhere("href", is(aJsonString(equalTo(url)))));
	}

	private static final Matcher<JsonElement> _isAJsonObjectWithTheEmbedded;
	private static final Matcher<JsonElement> _isAJsonObjectWithTheLinks;

	static {
		Conditions.Builder builder = new Conditions.Builder();

		Conditions linkConditions = builder.where(
			"first-linked", _isALinkTo("localhost:8080/first-linked")
		).where(
			"link1", _isALinkTo("www.liferay.com")
		).where(
			"link2", _isALinkTo("community.liferay.com")
		).where(
			"self", _isALinkTo("localhost:8080")
		).build();

		_isAJsonObjectWithTheLinks = is(aJsonObjectWith(linkConditions));

		Conditions firstEmbeddedLinkConditions = builder.where(
			"second-linked", _isALinkTo("localhost:8080/second-linked")
		).where(
			"self", _isALinkTo("localhost:8080/inner")
		).where(
			"link", _isALinkTo("www.liferay.com")
		).build();

		Conditions secondEmbeddedLinkConditions = builder.where(
			"self", _isALinkTo("localhost:8080/inner")
		).where(
			"link", _isALinkTo("www.liferay.com")
		).where(
			"third-linked", _isALinkTo("localhost:8080/third-linked")
		).build();

		Conditions secondEmbeddedConditions = builder.where(
			"_links", is(aJsonObjectWith(secondEmbeddedLinkConditions))
		).where(
			"boolean", is(aJsonBoolean(true))
		).where(
			"number", is(aJsonInt(equalTo(42)))
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).build();

		Matcher<JsonElement> isAJsonObjectWithTheSecondEmbedded = is(
			aJsonObjectWhere(
				"second-embedded",
				is(aJsonObjectWith(secondEmbeddedConditions))));

		Conditions firstEmbeddedConditions = builder.where(
			"_embedded", isAJsonObjectWithTheSecondEmbedded
		).where(
			"_links", is(aJsonObjectWith(firstEmbeddedLinkConditions))
		).where(
			"boolean", is(aJsonBoolean(true))
		).where(
			"number", is(aJsonInt(equalTo(42)))
		).where(
			"string", is(aJsonString(equalTo("A string")))
		).build();

		Conditions embeddedConditions = builder.where(
			"first-embedded", is(aJsonObjectWith(firstEmbeddedConditions))
		).build();

		_isAJsonObjectWithTheEmbedded = is(aJsonObjectWith(embeddedConditions));
	}

	private final HALSingleModelMessageMapper<MockModel>
		_singleModelMessageMapper = new HALSingleModelMessageMapper<>();

}
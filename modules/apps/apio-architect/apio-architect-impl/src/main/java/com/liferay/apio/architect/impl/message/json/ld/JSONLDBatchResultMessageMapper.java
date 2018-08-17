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

package com.liferay.apio.architect.impl.message.json.ld;

import com.liferay.apio.architect.batch.BatchResult;
import com.liferay.apio.architect.impl.message.json.BatchResultMessageMapper;
import com.liferay.apio.architect.impl.message.json.JSONObjectBuilder;
import com.liferay.apio.architect.impl.message.json.SingleModelMessageMapper;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;

/**
 * Represents batch result pages in JSON-LD + Hydra format.
 *
 * <p>
 * For more information, see <a href="https://json-ld.org/">JSON-LD </a> and <a
 * href="https://www.hydra-cg.com/">Hydra </a>.
 * </p>
 *
 * @author Alejandro Hernández
 */
@Component
public class JSONLDBatchResultMessageMapper<T>
	implements BatchResultMessageMapper<T> {

	@Override
	public String getMediaType() {
		return "application/ld+json";
	}

	@Override
	public Optional<SingleModelMessageMapper<T>>
		getSingleModelMessageMapperOptional() {

		return Optional.of(_singleModelMessageMapper);
	}

	@Override
	public void mapCollectionURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		_singleModelMessageMapper.mapSelfURL(jsonObjectBuilder, url);
	}

	@Override
	public void mapItemTotalCount(
		JSONObjectBuilder jsonObjectBuilder, int totalCount) {

		jsonObjectBuilder.field(
			"totalItems"
		).numberValue(
			totalCount
		);
	}

	@Override
	public void onFinish(
		JSONObjectBuilder jsonObjectBuilder, BatchResult<T> batchResult) {

		jsonObjectBuilder.field(
			"@id"
		).stringValue(
			"_:batch/" + batchResult.resourceName
		);

		jsonObjectBuilder.field(
			"@context"
		).arrayValue(
			arrayBuilder -> arrayBuilder.add(
				builder -> builder.field(
					"@vocab"
				).stringValue(
					"http://schema.org/"
				)),
			arrayBuilder -> arrayBuilder.addString(
				"https://www.w3.org/ns/hydra/core#")
		);

		jsonObjectBuilder.field(
			"@type"
		).arrayValue(
		).addString(
			"Collection"
		);
	}

	@Override
	public void onFinishItem(
		JSONObjectBuilder batchResultJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder) {

		batchResultJSONObjectBuilder.field(
			"member"
		).arrayValue(
		).add(
			itemJSONObjectBuilder
		);
	}

	private final SingleModelMessageMapper<T> _singleModelMessageMapper =
		new JSONLDSingleModelMessageMapper<>();

}
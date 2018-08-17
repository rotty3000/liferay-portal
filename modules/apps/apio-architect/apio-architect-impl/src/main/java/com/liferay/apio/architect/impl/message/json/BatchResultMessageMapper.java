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

package com.liferay.apio.architect.impl.message.json;

import com.liferay.apio.architect.batch.BatchResult;

import java.util.List;
import java.util.Optional;

/**
 * Maps {@link BatchResult} data to its representation in a JSON object.
 * Instances of this interface work like events. The {@code
 * javax.ws.rs.ext.MessageBodyWriter} of the {@code BatchResult} calls the
 * {@code BatchResultMessageMapper} methods. In each method, developers should
 * only map the provided part of the resource to its representation in a JSON
 * object. To enable this, each method receives a {@link JSONObjectBuilder}.
 *
 * <p>
 * The method {@link #onFinish} is called when the writer finishes writing the
 * batch result. Otherwise, the batch result message mapper's methods aren't
 * called in a particular order.
 * </p>
 *
 * <p>
 * By default, each item method calls {@link
 * #getSingleModelMessageMapperOptional()} to get a {@link
 * SingleModelMessageMapper} and call its corresponding method.
 * </p>
 *
 * @author Alejandro Hernández
 * @param  <T> the type of the model's identifier (e.g., {@code Long}, {@code
 *         String}, etc.)
 */
public interface BatchResultMessageMapper<T>
	extends MessageMapper<BatchResult<T>> {

	/**
	 * Returns the {@link SingleModelMessageMapper} used by the item methods.
	 *
	 * @return the {@code SingleModelMessageMapper}
	 */
	public default Optional<SingleModelMessageMapper<T>>
		getSingleModelMessageMapperOptional() {

		return Optional.empty();
	}

	/**
	 * Maps a collection URL to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the batch result
	 * @param url the collection's URL
	 */
	public default void mapCollectionURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {
	}

	/**
	 * Maps a resource URL to its JSON object representation.
	 *
	 * @param batchResultJSONObjectBuilder the JSON object builder for the batch
	 *        result
	 * @param itemJSONObjectBuilder the JSON object builder for the item
	 * @param url the resource's URL
	 */
	public default void mapItemSelfURL(
		JSONObjectBuilder batchResultJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String url) {

		Optional<SingleModelMessageMapper<T>> optional =
			getSingleModelMessageMapperOptional();

		optional.ifPresent(
			singleModelMessageMapper -> singleModelMessageMapper.mapSelfURL(
				itemJSONObjectBuilder, url));
	}

	/**
	 * Maps the total number of elements in the collection to its JSON object
	 * representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the batch result
	 * @param totalCount the total number of elements in the collection
	 */
	public default void mapItemTotalCount(
		JSONObjectBuilder jsonObjectBuilder, int totalCount) {
	}

	/**
	 * Maps resource types to their JSON object representation.
	 *
	 * @param batchResultJSONObjectBuilder the JSON object builder for the batch
	 *        result
	 * @param itemJSONObjectBuilder the JSON object builder for the item
	 * @param types the resource types
	 */
	public default void mapItemTypes(
		JSONObjectBuilder batchResultJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, List<String> types) {

		Optional<SingleModelMessageMapper<T>> optional =
			getSingleModelMessageMapperOptional();

		optional.ifPresent(
			singleModelMessageMapper -> singleModelMessageMapper.mapTypes(
				itemJSONObjectBuilder, types));
	}

	/**
	 * Finishes the item. This is the final batch result message mapper method
	 * the writer calls for the item.
	 *
	 * @param batchResultJSONObjectBuilder the JSON object builder for the batch
	 *        result
	 * @param itemJSONObjectBuilder the JSON object builder for the item
	 */
	public default void onFinishItem(
		JSONObjectBuilder batchResultJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder) {
	}

	/**
	 * Starts the item. This is the first batch result message mapper method the
	 * writer calls for the item.
	 *
	 * @param batchResultJSONObjectBuilder the JSON object builder for the batch
	 *        result
	 * @param itemJSONObjectBuilder the JSON object builder for the item
	 */
	public default void onStartItem(
		JSONObjectBuilder batchResultJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder) {
	}

}
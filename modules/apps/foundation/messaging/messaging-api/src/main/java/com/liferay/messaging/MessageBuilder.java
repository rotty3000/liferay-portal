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

package com.liferay.messaging;

import java.util.Map;

/**
 * @author Raymond Augé
 */
public interface MessageBuilder {

	/**
	 * Return a fully constructed message
	 *
	 * @return the message
	 */
	public Message build();

	/**
	 * Add key, value pairs of data into the message.
	 *
	 * @param  key
	 * @param  value
	 * @return the builder
	 */
	public MessageBuilder put(String key, Object value);

	/**
	 * Send the message asynchronously.
	 *
	 * @throws IllegalStateException if destination name is not set
	 */
	public void send();

	/**
	 * Send the message synchronously.
	 *
	 * @return the result
	 * @throws IllegalStateException if destination name is not set
	 */
	public Object sendSynchronous();

	/**
	 * Send the message synchronously supplying the timeout within which
	 * a result is expected.
	 *
	 * @param timeout
	 * @return the result
	 * @throws IllegalStateException if destination name is not set
	 */
	public Object sendSynchronous(long timeout);

	/**
	 * Set the destination name.
	 *
	 * @param  destinationName
	 * @return the builder
	 */
	public MessageBuilder setDestinationName(String destinationName);

	/**
	 * Set an arbitrary payload into the message.
	 *
	 * @param  payload
	 * @return the builder
	 */
	public MessageBuilder setPayload(Object payload);

	/**
	 * Set a response into the message.
	 *
	 * @param  response
	 * @return the builder
	 */
	public MessageBuilder setResponse(Object response);

	/**
	 * Set the response destination name into the message.
	 *
	 * @param  responseDestinationName
	 * @return the buider
	 */
	public MessageBuilder setResponseDestinationName(
		String responseDestinationName);

	/**
	 * Set the response id into the message.
	 *
	 * @param  responseId
	 * @return the builder
	 */
	public MessageBuilder setResponseId(String responseId);

	/**
	 * Use a map to add a number of key, value pairs of data into the message
	 * at once.
	 *
	 * @param  values
	 * @return the builder
	 */
	public MessageBuilder setValues(Map<String, Object> values);

}
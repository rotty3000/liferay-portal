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

package com.liferay.messaging.impl.internal;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBuilder;
import com.liferay.messaging.MessageBus;

import java.util.Map;

/**
 * @author Raymond Augé
 */
public class DefaultMessageBuilder implements MessageBuilder {

	public DefaultMessageBuilder(
		MessageBus messageBus, String destinationName) {

		_messageBus = messageBus;

		_message = new Message();

		_message.setDestinationName(destinationName);
	}

	@Override
	public Message build() {
		return _message;
	}

	@Override
	public MessageBuilder put(String key, Object value) {
		_message.put(key, value);

		return this;
	}

	@Override
	public MessageBuilder setDestinationName(String destinationName) {
		_message.setDestinationName(destinationName);

		return this;
	}

	@Override
	public MessageBuilder setPayload(Object payload) {
		_message.setPayload(payload);

		return this;
	}

	@Override
	public MessageBuilder setResponse(Object response) {
		_message.setResponse(response);

		return this;
	}

	@Override
	public MessageBuilder setResponseDestinationName(String responseDestinationName) {
		_message.setResponseDestinationName(responseDestinationName);

		return this;
	}

	@Override
	public MessageBuilder setResponseId(String responseId) {
		_message.setResponseId(responseId);

		return this;
	}

	@Override
	public MessageBuilder setValues(Map<String, Object> values) {
		_message.setValues(values);

		return this;
	}

	@Override
	public void send() {
		if (_message.getDestinationName() == null) {
			throw new IllegalStateException("destinationName is not set");
		}

		_messageBus.sendMessage(_message.getDestinationName(), _message);

	}

	@Override
	public Object sendSynchronous() {
		if (_message.getDestinationName() == null) {
			throw new IllegalStateException("destinationName is not set");
		}

		return _messageBus.sendSynchronousMessage(
			_message.getDestinationName(), _message);
	}

	@Override
	public Object sendSynchronous(long timeout) {
		if (_message.getDestinationName() == null) {
			throw new IllegalStateException("destinationName is not set");
		}

		return _messageBus.sendSynchronousMessage(
			_message.getDestinationName(), _message, timeout);
	}

	private final Message _message;
	private final MessageBus _messageBus;

}
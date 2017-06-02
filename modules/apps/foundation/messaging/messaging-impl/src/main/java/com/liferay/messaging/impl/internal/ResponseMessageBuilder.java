package com.liferay.messaging.impl.internal;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBuilder;
import com.liferay.messaging.MessageBus;

import java.util.Map;

public class ResponseMessageBuilder implements MessageBuilder {

	public ResponseMessageBuilder(MessageBus messageBus, Message original) {
		_messageBus = messageBus;

		_message = new Message();

		_message.copyFrom(original);

		// Swap destinations

		_message.setDestinationName(original.getResponseDestinationName());
		_message.setResponseDestinationName(original.getDestinationName());
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
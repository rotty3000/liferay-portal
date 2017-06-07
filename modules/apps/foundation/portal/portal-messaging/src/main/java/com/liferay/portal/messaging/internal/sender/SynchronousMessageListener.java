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

package com.liferay.portal.messaging.internal.sender;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageBusException;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageOutboundProcessor;
import com.liferay.messaging.MessageProcessorException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael C. Han
 */
public class SynchronousMessageListener implements MessageListener {

	public SynchronousMessageListener(
		MessageBus messageBus, Message message, long timeout,
		List<MessageOutboundProcessor> processors) {

		_messageBus = messageBus;
		_message = message;
		_timeout = timeout;
		_processors = processors;

		_responseId = _message.getResponseId();
	}

	public Object getResults() {
		return _results;
	}

	@Override
	public void receive(Message message) {
		if (!message.getResponseId().equals(_responseId)) {
			return;
		}

		_results = message.getPayload();

		_countDownLatch.countDown();
	}

	public Object send() throws MessageBusException {
		String destinationName = _message.getDestinationName();
		String responseDestinationName = _message.getResponseDestinationName();

		_messageBus.registerMessageListener(responseDestinationName, this);

		Message sendingMessage = _message;

		try {
			for (MessageOutboundProcessor processor : _processors) {
				try {
					sendingMessage = processor.beforeSend(sendingMessage);
				}
				catch (MessageProcessorException mpe) {
					throw new MessageBusException(
						"Unable to process message before sending " +
							sendingMessage);
				}
			}

			_messageBus.sendMessage(destinationName, sendingMessage);

			_countDownLatch.await(_timeout, TimeUnit.MILLISECONDS);

			if (_results == null) {
				throw new MessageBusException(
					"No reply received for message: " + sendingMessage);
			}

			return _results;
		}
		catch (InterruptedException ie) {
			throw new MessageBusException(
				"Message sending interrupted for: " + sendingMessage, ie);
		}
		finally {
			_messageBus.unregisterMessageListener(
				responseDestinationName, this);

			for (MessageOutboundProcessor processor : _processors) {
				try {
					processor.afterSend(sendingMessage);
				}
				catch (MessageProcessorException mpe) {
					throw new MessageBusException(
						"Unable to process message after sending " +
							sendingMessage);
				}
			}
		}
	}

	private final CountDownLatch _countDownLatch = new CountDownLatch(1);
	private final Message _message;
	private final MessageBus _messageBus;
	private final List<MessageOutboundProcessor> _processors;
	private final String _responseId;
	private Object _results;
	private final long _timeout;

}
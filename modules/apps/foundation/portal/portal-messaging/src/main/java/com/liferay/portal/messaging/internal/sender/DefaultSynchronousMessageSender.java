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

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationNames;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageBusException;
import com.liferay.messaging.MessageOutboundProcessor;
import com.liferay.messaging.internal.validator.Validator;
import com.liferay.messaging.sender.SynchronousMessageSender;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael C. Han
 */
public class DefaultSynchronousMessageSender
	implements SynchronousMessageSender {

	@Override
	public Object send(String destinationName, Message message)
		throws MessageBusException {

		return send(destinationName, message, _timeout);
	}

	@Override
	public Object send(String destinationName, Message message, long timeout)
		throws MessageBusException {

		Destination destination = _messageBus.getDestination(destinationName);

		if (destination == null) {
			if (_logger.isInfoEnabled()) {
				_logger.info(
					"Destination " + destinationName + " is not configured");
			}

			return null;
		}

		if (destination.getMessageListenerCount() == 0) {
			if (_logger.isInfoEnabled()) {
				_logger.info(
					"Destination " + destinationName +
						" does not have any message listeners");
			}

			return null;
		}

		message.setDestinationName(destinationName);

		String responseDestinationName = message.getResponseDestinationName();

		// Create a temporary destination if no response destination is
		// configured

		if (Validator.isNull(responseDestinationName) ||
			!_messageBus.hasDestination(responseDestinationName)) {

			if (_logger.isDebugEnabled()) {
				_logger.debug(
					"Response destination " + responseDestinationName +
						" is not configured");
			}

			message.setResponseDestinationName(
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
		}

		String responseId = generateUUID();

		message.setResponseId(responseId);

		SynchronousMessageListener synchronousMessageListener =
			new SynchronousMessageListener(
				_messageBus, message, timeout, _processors);

		return synchronousMessageListener.send();
	}

	public void setMessageBus(MessageBus messageBus) {
		_messageBus = messageBus;
	}

	public void setMessageOutboundProcessor(
		List<MessageOutboundProcessor> processors) {

		_processors = processors;
	}

	public void setTimeout(long timeout) {
		_timeout = timeout;
	}

	protected String generateUUID() {
		UUID uuid = new UUID(
			ThreadLocalRandom.current().nextLong(),
			ThreadLocalRandom.current().nextLong());

		return uuid.toString();
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		DefaultSynchronousMessageSender.class);

	private MessageBus _messageBus;
	private List<MessageOutboundProcessor> _processors;
	private long _timeout;

}
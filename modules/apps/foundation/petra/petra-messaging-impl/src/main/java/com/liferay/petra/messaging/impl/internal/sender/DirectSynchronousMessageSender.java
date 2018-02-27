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

package com.liferay.petra.messaging.impl.internal.sender;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBus;
import com.liferay.petra.messaging.api.MessageBusException;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.api.MessageListenerException;
import com.liferay.petra.messaging.spi.Destination;
import com.liferay.petra.messaging.spi.SynchronousDestination;
import com.liferay.petra.messaging.spi.sender.SynchronousMessageSender;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shuyang Zhou
 */
public class DirectSynchronousMessageSender
	implements SynchronousMessageSender {

	@Override
	public Object send(String destinationName, Message message)
		throws MessageBusException {

		Destination destination = (Destination)_messageBus.getDestination(
			destinationName);

		if (destination == null) {
			if (_logger.isInfoEnabled()) {
				_logger.info(
					"Destination " + destinationName + " is not configured");
			}

			return null;
		}

		if (destination instanceof SynchronousDestination) {
			SynchronousDestination synchronousDestination =
				(SynchronousDestination)destination;

			synchronousDestination.send(message);
		}
		else {
			Collection<MessageListener> messageListeners =
				destination.getMessageListeners();

			for (MessageListener messageListener : messageListeners) {
				try {
					messageListener.receive(message);
				}
				catch (MessageListenerException mle) {
					throw new MessageBusException(mle);
				}
			}
		}

		return message.getResponse();
	}

	@Override
	public Object send(String destinationName, Message message, long timeout)
		throws MessageBusException {

		if (_logger.isWarnEnabled()) {
			_logger.warn(
				DirectSynchronousMessageSender.class.getName() +
					" does not support timeout");
		}

		return send(destinationName, message);
	}

	public void setMessageBus(MessageBus messageBus) {
		_messageBus = messageBus;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		DirectSynchronousMessageSender.class);

	private MessageBus _messageBus;

}
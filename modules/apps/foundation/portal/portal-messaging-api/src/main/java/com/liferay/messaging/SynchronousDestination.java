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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shuyang Zhou
 */
public class SynchronousDestination extends BaseDestination {

	@Override
	public DestinationStatistics getDestinationStatistics() {
		DestinationStatistics destinationStatistics =
			new DestinationStatistics();

		destinationStatistics.setSentMessageCount(_sentMessageCounter.get());

		return destinationStatistics;
	}

	@Override
	public void send(Message message) {
		if (messageListeners.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("No message listeners for destination " + getName());
			}

			return;
		}

		List<MessageInboundProcessor> messageInboundProcessors = getMessageInboundProcessors();

		try {
			for (MessageInboundProcessor processor : messageInboundProcessors) {
				try {
					message = processor.beforeReceive(message);
				}
				catch (MessageProcessorException mpe) {
					_logger.error("Unable to process message " + message, mpe);
				}
			}

			for (MessageListener messageListener : messageListeners) {
				try {
					messageListener.receive(message);
				}
				catch (MessageListenerException mle) {
					_logger.error("Unable to process message " + message, mle);
				}
			}
		}
		finally {
			for (MessageInboundProcessor processor : messageInboundProcessors) {
				try {
					processor.afterReceive(message);
				}
				catch (MessageProcessorException mpe) {
					_logger.error("Unable to process message " + message, mpe);
				}
			}
		}

		_sentMessageCounter.incrementAndGet();
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		SynchronousDestination.class);

	private final AtomicLong _sentMessageCounter = new AtomicLong();

}
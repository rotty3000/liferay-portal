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

import org.osgi.service.component.annotations.Activate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <strong>Note:</strong> When using this as a parent class to a Declarative
 * Services {@code @Cmponent} apply the instruction
 * {@code -dsannotations-options: inherit} in the bnd file.
 * </p>
 *
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
			if (_log.isDebugEnabled()) {
				_log.debug("No message listeners for destination " + getName());
			}

			return;
		}

		List<InboundMessageProcessor> inboundMessageProcessors =
			getInboundMessageProcessors();

		try {
			for (InboundMessageProcessor processor : inboundMessageProcessors) {
				try {
					message = processor.beforeReceive(message);
				}
				catch (MessageProcessorException mpe) {
					_log.error("Unable to process message " + message, mpe);
				}
			}

			for (MessageListener messageListener : messageListeners) {
				try {
					messageListener.receive(message);
				}
				catch (MessageListenerException mle) {
					_log.error("Unable to process message " + message, mle);
				}
			}
		}
		finally {
			for (InboundMessageProcessor processor : inboundMessageProcessors) {
				try {
					processor.afterReceive(message);
				}
				catch (MessageProcessorException mpe) {
					_log.error("Unable to process message " + message, mpe);
				}
			}
		}

		_sentMessageCounter.incrementAndGet();
	}

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setName(destinationSettings.destination_name());
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SynchronousDestination.class);

	private final AtomicLong _sentMessageCounter = new AtomicLong();

}
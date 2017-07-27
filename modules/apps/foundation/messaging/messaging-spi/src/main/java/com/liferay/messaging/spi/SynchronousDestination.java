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

package com.liferay.messaging.spi;

import com.liferay.messaging.DestinationSettings;
import com.liferay.messaging.DestinationStatistics;
import com.liferay.messaging.InboundMessageProcessor;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;
import com.liferay.messaging.MessageProcessorException;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shuyang Zhou
 * @author Raymond Augé
 */
@Component(factory = "synchronous.destination")
public class SynchronousDestination extends BaseDestination {

	@Override
	public DestinationStatistics getDestinationStatistics() {
		DestinationStatisticsImpl destinationStatistics =
			new DestinationStatisticsImpl();

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
					message = processor.beforeThread(
						message, Thread.currentThread());
				}
				catch (MessageProcessorException mpe) {
					_log.error("Unable to process message " + message, mpe);
				}
			}

			for (MessageListener messageListener : getMessageListeners()) {
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
					processor.afterThread(message, Thread.currentThread());
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
		afterPropertiesSet();
		open();
	}

	@Deactivate
	protected void deactivate() {
		close();
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SynchronousDestination.class);

	private final AtomicLong _sentMessageCounter = new AtomicLong();

}
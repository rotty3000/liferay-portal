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

package com.liferay.petra.messaging.spi;

import com.liferay.petra.messaging.api.DestinationSettings;
import com.liferay.petra.messaging.api.InboundMessageProcessor;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.api.MessageListenerException;
import com.liferay.petra.messaging.api.MessageProcessorException;
import com.liferay.petra.concurrent.ThreadPoolExecutor;

import java.util.Collection;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Destination that delivers a message to a list of message listeners one at a
 * time.
 * </p>
 *
 * @author Michael C. Han
 * @author Raymond Augé
 */
@Component(factory = "serial.destination")
public class SerialDestination extends BaseAsyncDestination {

	public SerialDestination() {
		setWorkersCoreSize(_WORKERS_CORE_SIZE);
		setWorkersMaxSize(_WORKERS_MAX_SIZE);
	}

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setMaximumQueueSize(destinationSettings.maxQueueSize());
		setName(destinationSettings.destination_name());
		afterPropertiesSet();
		open();
	}

	@Deactivate
	protected void deactivate() {
		close();
	}

	@Override
	protected void dispatch(
		final Collection<MessageListener> messageListeners,
		final Collection<InboundMessageProcessor> messageInboundProcessors,
		final Message message) {

		final Thread dispatchThread = Thread.currentThread();

		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

		Runnable runnable = new MessageRunnable(message) {

			@Override
			public void run() {
				Message processedMessage = getMessage();

				try {
					for (InboundMessageProcessor processor :
							messageInboundProcessors) {

						try {
							processedMessage = processor.beforeThread(
								processedMessage, dispatchThread);
						}
						catch (MessageProcessorException mpe) {
							_log.error(
								"Unable to process message before thread {}",
								processedMessage, mpe);
						}
					}

					for (MessageListener messageListener : messageListeners) {
						try {
							messageListener.receive(processedMessage);
						}
						catch (MessageListenerException mle) {
							_log.error(
								"Unable to process message {}",
								processedMessage, mle);
						}
					}
				}
				finally {
					for (InboundMessageProcessor processor :
							messageInboundProcessors) {

						try {
							processor.afterThread(
								processedMessage, dispatchThread);
						}
						catch (MessageProcessorException mpe) {
							_log.error(
								"Unable to process message after thread {}",
								processedMessage, mpe);
						}
					}
				}
			}

		};

		threadPoolExecutor.execute(runnable);
	}

	private static final int _WORKERS_CORE_SIZE = 1;

	private static final int _WORKERS_MAX_SIZE = 1;

	private static final Logger _log = LoggerFactory.getLogger(
		SerialDestination.class);

}
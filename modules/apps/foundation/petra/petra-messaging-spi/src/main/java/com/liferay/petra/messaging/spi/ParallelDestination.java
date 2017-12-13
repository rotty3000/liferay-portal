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
 * Destination that delivers a message to a list of message listeners in
 * parallel.
 * </p>
 *
 * @author Michael C. Han
 * @author Raymond Augé
 */
@Component(factory = "parallel.destination")
public class ParallelDestination extends BaseAsyncDestination {

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setMaximumQueueSize(destinationSettings.maxQueueSize());
		setName(destinationSettings.destination_name());
		setWorkersCoreSize(destinationSettings.workerCoreSize());
		setWorkersMaxSize(destinationSettings.workerMaxSize());
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
		final Collection<InboundMessageProcessor> inboundMessageProcessors,
		final Message message) {

		final Thread dispatchThread = Thread.currentThread();

		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

		for (final MessageListener messageListener : messageListeners) {
			Runnable runnable = new MessageRunnable(message) {

				@Override
				public void run() {
					Message processedMessage = getMessage();

					try {
						for (InboundMessageProcessor processor :
								inboundMessageProcessors) {

							try {
								processedMessage = processor.beforeThread(
									processedMessage, dispatchThread);
							}
							catch (MessageProcessorException mpe) {
								_log.error(
									"Unable to process message {} before " +
										"thread {}",
									processedMessage, dispatchThread, mpe);
							}
						}

						messageListener.receive(processedMessage);
					}
					catch (MessageListenerException mle) {
						_log.error(
							"Unable to process message {}", processedMessage,
							mle);
					}
					finally {
						for (InboundMessageProcessor processor :
								inboundMessageProcessors) {

							try {
								processor.afterThread(
									processedMessage, dispatchThread);
							}
							catch (MessageProcessorException mpe) {
								_log.error(
									"Unable to process message {} after" +
										"thread {}",
									processedMessage, dispatchThread, mpe);
							}
						}
					}
				}

			};

			threadPoolExecutor.execute(runnable);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		ParallelDestination.class);

}
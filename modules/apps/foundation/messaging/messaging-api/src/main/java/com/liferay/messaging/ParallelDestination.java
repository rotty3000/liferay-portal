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

import com.liferay.petra.concurrent.ThreadPoolExecutor;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Destination that delivers a message to a list of message listeners in
 * parallel.
 * </p>
 * <p>
 * <strong>Note:</strong> When using this as a parent class to a Declarative
 * Services {@code @Cmponent} apply the instruction
 * {@code -dsannotations-options: inherit} in the bnd file.
 * </p>
 *
 * @author Michael C. Han
 */
public class ParallelDestination extends BaseAsyncDestination {

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setMaximumQueueSize(destinationSettings.maximumQueueSize());
		setName(destinationSettings.destination_name());
		setWorkersCoreSize(destinationSettings.workersCoreSize());
		setWorkersMaxSize(destinationSettings.workersMaxSize());
	}

	@Override
	protected void dispatch(
		Set<MessageListener> messageListeners,
		final List<InboundMessageProcessor> inboundMessageProcessors,
		final Message message) {

		final Thread dispatchThread = Thread.currentThread();

		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

		for (final MessageListener messageListener : messageListeners) {
			Runnable runnable = new MessageRunnable(message) {

				@Override
				public void run() {
					Message processedMessage = message;

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
									message, dispatchThread, mpe);
							}
						}

						messageListener.receive(processedMessage);
					}
					catch (MessageListenerException mle) {
						_log.error(
							"Unable to process message {}", message, mle);
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
									message, dispatchThread, mpe);
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
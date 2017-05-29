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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Destination that delivers a message to a list of message listeners one at a
 * time.
 * </p>
 *
 * @author Michael C. Han
 */
public class SerialDestination extends BaseAsyncDestination {

	public SerialDestination() {
		setWorkersCoreSize(_WORKERS_CORE_SIZE);
		setWorkersMaxSize(_WORKERS_MAX_SIZE);
	}

	@Override
	protected void dispatch(
		final Set<MessageListener> messageListeners,
		final List<InboundMessageProcessor> messageInboundProcessors,
		final Message message) {

		final Thread dispatchThread = Thread.currentThread();

		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

		Runnable runnable = new MessageRunnable(message) {

			@Override
			public void run() {
				Message processedMessage = message;

				try {
					for (InboundMessageProcessor processor :
							messageInboundProcessors) {

						try {
							processedMessage = processor.beforeThread(
								processedMessage, dispatchThread);
						}
						catch (MessageProcessorException mpe) {
							_log.error(
								"Unable to process message before thread " +
									message, mpe);
						}
					}

					for (MessageListener messageListener : messageListeners) {
						try {
							messageListener.receive(message);
						}
						catch (MessageListenerException mle) {
							_log.error(
								"Unable to process message " + message, mle);
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
								"Unable to process message after thread " +
									message, mpe);
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
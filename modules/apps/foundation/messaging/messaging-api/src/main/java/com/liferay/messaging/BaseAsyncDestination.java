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

import com.liferay.petra.concurrent.NamedThreadFactory;
import com.liferay.petra.concurrent.RejectedExecutionHandler;
import com.liferay.petra.concurrent.ThreadPoolExecutor;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public abstract class BaseAsyncDestination extends BaseDestination {

	@Override
	public void close(boolean force) {
		if ((_threadPoolExecutor == null) || _threadPoolExecutor.isShutdown()) {
			return;
		}

		if (force) {
			_threadPoolExecutor.shutdownNow();
		}
		else {
			_threadPoolExecutor.shutdown();
		}
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		DestinationStatistics destinationStatistics =
			new DestinationStatistics();

		destinationStatistics.setActiveThreadCount(
			_threadPoolExecutor.getActiveCount());
		destinationStatistics.setCurrentThreadCount(
			_threadPoolExecutor.getPoolSize());
		destinationStatistics.setLargestThreadCount(
			_threadPoolExecutor.getLargestPoolSize());
		destinationStatistics.setMaxThreadPoolSize(
			_threadPoolExecutor.getMaxPoolSize());
		destinationStatistics.setMinThreadPoolSize(
			_threadPoolExecutor.getCorePoolSize());
		destinationStatistics.setPendingMessageCount(
			_threadPoolExecutor.getPendingTaskCount());
		destinationStatistics.setSentMessageCount(
			_threadPoolExecutor.getCompletedTaskCount());

		return destinationStatistics;
	}

	public int getMaximumQueueSize() {
		return _maximumQueueSize;
	}

	public int getWorkersCoreSize() {
		return _workersCoreSize;
	}

	public int getWorkersMaxSize() {
		return _workersMaxSize;
	}

	@Override
	public void open() {
		if ((_threadPoolExecutor != null) &&
			!_threadPoolExecutor.isShutdown()) {

			return;
		}

		ClassLoader classLoader = _clazz.getClassLoader();

		if (_rejectedExecutionHandler == null) {
			_rejectedExecutionHandler = createRejectionExecutionHandler();
		}

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
			_workersCoreSize, _workersMaxSize, 60L, TimeUnit.SECONDS, false,
			_maximumQueueSize, _rejectedExecutionHandler,
			new NamedThreadFactory(
				getName(), Thread.NORM_PRIORITY, classLoader),
			new ThreadPoolHandlerAdapter());

		ThreadPoolExecutor oldThreadPoolExecutor = null;

		if (_executorServiceRegistrar != null) {
			oldThreadPoolExecutor =
				_executorServiceRegistrar.registerExecutorService(
					getName(), threadPoolExecutor);
		}

		if (oldThreadPoolExecutor != null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Abort creating a new thread pool for destination " +
						getName() + " and reuse previous one");
			}

			threadPoolExecutor.shutdownNow();

			threadPoolExecutor = oldThreadPoolExecutor;
		}

		_threadPoolExecutor = threadPoolExecutor;
	}

	@Override
	public void send(Message message) {
		if (messageListeners.isEmpty()) {
			if (_log.isDebugEnabled()) {
				_log.debug("No message listeners for destination " + getName());
			}

			return;
		}

		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

		if (threadPoolExecutor.isShutdown()) {
			throw new IllegalStateException(
				"Destination " + getName() + " is shutdown and cannot " +
					"receive more messages");
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Sending message " + message + " from destination " +
					getName() + " to message listeners " + messageListeners);
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

			dispatch(messageListeners, inboundMessageProcessors, message);
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
	}

	public void setExecutorServiceRegistrar(
		ExecutorServiceRegistrar executorServiceRegistrar) {

		_executorServiceRegistrar = executorServiceRegistrar;
	}

	public void setMaximumQueueSize(int maximumQueueSize) {
		_maximumQueueSize = maximumQueueSize;
	}

	public void setRejectedExecutionHandler(
		RejectedExecutionHandler rejectedExecutionHandler) {

		_rejectedExecutionHandler = rejectedExecutionHandler;
	}

	public void setWorkersCoreSize(int workersCoreSize) {
		_workersCoreSize = workersCoreSize;

		if (_threadPoolExecutor != null) {
			_threadPoolExecutor.adjustPoolSize(
				workersCoreSize, _workersMaxSize);
		}
	}

	public void setWorkersMaxSize(int workersMaxSize) {
		_workersMaxSize = workersMaxSize;

		if (_threadPoolExecutor != null) {
			_threadPoolExecutor.adjustPoolSize(
				_workersCoreSize, workersMaxSize);
		}
	}

	protected RejectedExecutionHandler createRejectionExecutionHandler() {
		return new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(
				Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

				if (!_log.isWarnEnabled()) {
					return;
				}

				MessageRunnable messageRunnable = (MessageRunnable)runnable;

				_log.warn(
					"Discarding message " + messageRunnable.getMessage() +
						" because it exceeds the maximum queue size of " +
							_maximumQueueSize);
			}

		};
	}

	protected abstract void dispatch(
		Set<MessageListener> messageListeners,
		List<InboundMessageProcessor> messageInboundProcessors,
		Message message);

	protected ThreadPoolExecutor getThreadPoolExecutor() {
		return _threadPoolExecutor;
	}

	private static final Class<BaseAsyncDestination> _clazz =
		BaseAsyncDestination.class;

	private volatile ExecutorServiceRegistrar _executorServiceRegistrar;

	private static final int _WORKERS_CORE_SIZE = 2;

	private static final int _WORKERS_MAX_SIZE = 5;

	private static final Logger _log = LoggerFactory.getLogger(
		BaseAsyncDestination.class);

	private int _maximumQueueSize = Integer.MAX_VALUE;
	private RejectedExecutionHandler _rejectedExecutionHandler;
	private ThreadPoolExecutor _threadPoolExecutor;
	private int _workersCoreSize = _WORKERS_CORE_SIZE;
	private int _workersMaxSize = _WORKERS_MAX_SIZE;

}
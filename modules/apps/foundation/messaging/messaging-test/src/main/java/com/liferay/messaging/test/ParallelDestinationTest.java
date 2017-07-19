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

package com.liferay.messaging.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationStatistics;
import com.liferay.messaging.ExecutorServiceRegistrar;
import com.liferay.messaging.Message;
import com.liferay.messaging.spi.MessageRunnable;
import com.liferay.messaging.spi.ParallelDestination;
import com.liferay.petra.concurrent.RejectedExecutionHandler;
import com.liferay.petra.concurrent.ThreadPoolExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jesse Rao
 */
public class ParallelDestinationTest extends TestUtil {

	@Test(expected = IllegalStateException.class)
	public void testCloseDestination() throws Exception {
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();

			String destinationName = "parallel/test";

			Destination destination = messageBus.getDestination(
				destinationName);

			com.liferay.messaging.spi.Destination spiDestination =
				(com.liferay.messaging.spi.Destination)destination;

			spiDestination.close();

			Message message = new Message();

			spiDestination.send(message);
		}
		finally {
			tbBundle.uninstall();
		}
	}

	@Test
	public void testExecutorServiceRegistrar() throws Exception {
		Bundle tb16 = install("tb16.jar");
		Bundle tb2 = install("tb2.jar");

		ServiceTracker<ExecutorServiceRegistrar, Callable<Map<String, ExecutorService>>>
			tracker = null;

		try {
			tb16.start();
			tb2.start();

			String destinationName = "parallel/test";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=%s)(objectClass=%s)(destination.name=%s))",
					ExecutorServiceRegistrar.class.getName(),
					Callable.class.getName(), destinationName));

			tracker = new ServiceTracker<>(bundleContext, filter, null);

			tracker.open();

			Callable<Map<String, ExecutorService>> service =
				tracker.waitForService(timeout);

			assertNotNull(service);

			Map<String, ExecutorService> map = service.call();

			assertTrue(map.containsKey(destinationName));
			assertNotNull(map.get(destinationName));
		}
		finally {
			tb2.uninstall();
			tb16.uninstall();

			if (tracker != null) {
				tracker.close();
			}
		}
	}

	@Test
	public void testParameters() throws Exception {
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();

			String destinationName = "parallel/test";

			ParallelDestination destination =
				(ParallelDestination)messageBus.getDestination(destinationName);

			assertEquals(2, destination.getWorkersCoreSize());
			assertEquals(5, destination.getWorkersMaxSize());
			assertEquals(Integer.MAX_VALUE, destination.getMaximumQueueSize());

			DestinationStatistics destinationStatistics =
				destination.getDestinationStatistics();

			assertEquals(
				destination.getWorkersCoreSize(),
				destinationStatistics.getMinThreadPoolSize());
			assertEquals(
				destination.getWorkersMaxSize(),
				destinationStatistics.getMaxThreadPoolSize());

			destination.setWorkersCoreSize(4);
			destination.setWorkersMaxSize(10);
			destination.setMaximumQueueSize(20);

			assertEquals(4, destination.getWorkersCoreSize());
			assertEquals(10, destination.getWorkersMaxSize());
			assertEquals(20, destination.getMaximumQueueSize());

			destinationStatistics = destination.getDestinationStatistics();

			assertEquals(
				destination.getWorkersCoreSize(),
				destinationStatistics.getMinThreadPoolSize());
			assertEquals(
				destination.getWorkersMaxSize(),
				destinationStatistics.getMaxThreadPoolSize());
		}
		finally {
			tbBundle.uninstall();
		}
	}

	@Test
	public void testRejectedExecutionHandler() throws Exception {
		Bundle tb16 = install("tb16.jar");
		Bundle tb2 = install("tb2.jar");

		ServiceTracker<
			RejectedExecutionHandler,
			Callable<Map<MessageRunnable, ThreadPoolExecutor>>> tracker = null;

		try {
			tb16.start();
			tb2.start();

			String destinationName = "parallel/test";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=%s)(objectClass=%s)(destination.name=%s))",
					RejectedExecutionHandler.class.getName(),
					Callable.class.getName(), destinationName));

			tracker = new ServiceTracker<>(bundleContext, filter, null);

			tracker.open();

			Callable<Map<MessageRunnable, ThreadPoolExecutor>> service =
				tracker.waitForService(timeout);

			assertNotNull(service);

			messageBus = getMessageBus();

			for (int i = 0; i < 100; i++) {
				Message message = new Message();

				// TODO there's a bug here, the destination is shutdown.
				// We need to figure out why this is happening.

				// messageBus.sendMessage(destinationName, message);
			}

			Map<MessageRunnable, ThreadPoolExecutor> map = service.call();

			assertNotNull(map);
			//assertFalse(map.isEmpty());
		}
		finally {
			tb2.uninstall();
			tb16.uninstall();

			if (tracker != null) {
				tracker.close();
			}
		}
	}

}
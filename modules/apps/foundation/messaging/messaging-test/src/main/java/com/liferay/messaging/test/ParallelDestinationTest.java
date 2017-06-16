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

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationStatistics;
import com.liferay.messaging.ExecutorServiceRegistrar;
import com.liferay.messaging.Message;
import com.liferay.messaging.ParallelDestination;
import com.liferay.petra.concurrent.RejectedExecutionHandler;

import java.util.concurrent.Callable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jesse Rao
 */
public class ParallelDestinationTest extends TestUtil {

	@Test(expected = IllegalStateException.class)
	public void testCloseDestination()
		throws Exception {
		
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();

			String destinationName = "parallel/test";

			Destination destination = messageBus.getDestination(destinationName);
			
			destination.close();
			
			Message message = new Message();
			
			destination.send(message);
		}
		finally {
			tbBundle.uninstall();
		}
	}

	@Test
	public void testOpenDestination()
		throws Exception {
		
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();

			String destinationName = "parallel/test";

			Destination destination = messageBus.getDestination(destinationName);
			
			destination.close();

			destination.open();
			
			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Message message = new Message();
			
			destination.send(message);

			assertEquals(message, callable.call());
		}
		finally {
			tbBundle.uninstall();
		}
	}
	
	@Test
	public void testParameters()
		throws Exception {
		
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();

			String destinationName = "parallel/test";

			ParallelDestination destination = (ParallelDestination) messageBus.getDestination(destinationName);
			
			assertEquals(2, destination.getWorkersCoreSize());
			assertEquals(5, destination.getWorkersMaxSize());
			assertEquals(Integer.MAX_VALUE, destination.getMaximumQueueSize());
			
			DestinationStatistics destinationStatistics = destination.getDestinationStatistics();
			
			assertEquals(destination.getWorkersCoreSize(), destinationStatistics.getMinThreadPoolSize());
			assertEquals(destination.getWorkersMaxSize(), destinationStatistics.getMaxThreadPoolSize());
			
			destination.setWorkersCoreSize(4);
			destination.setWorkersMaxSize(10);
			destination.setMaximumQueueSize(20);

			assertEquals(4, destination.getWorkersCoreSize());
			assertEquals(10, destination.getWorkersMaxSize());
			assertEquals(20, destination.getMaximumQueueSize());

			destinationStatistics = destination.getDestinationStatistics();
			
			assertEquals(destination.getWorkersCoreSize(), destinationStatistics.getMinThreadPoolSize());
			assertEquals(destination.getWorkersMaxSize(), destinationStatistics.getMaxThreadPoolSize());
			
		}
		finally {
			tbBundle.uninstall();
		}
	}

	@Test
	public void testExecutorServiceRegistrar()
		throws Exception {
		
		Bundle tbBundle2 = install("tb16.jar");
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();
			tbBundle2.start();

			String destinationName = "parallel/test";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=com.liferay.messaging.ExecutorServiceRegistrar)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<?, ?> tracker =
				new ServiceTracker<>(bundleContext, filter, null);

			tracker.open();

			ExecutorServiceRegistrar executorServiceRegistrar = (ExecutorServiceRegistrar) tracker.waitForService(timeout);

			assertNotNull(executorServiceRegistrar);

			ParallelDestination destination = (ParallelDestination) messageBus.getDestination(destinationName);
			
			executorServiceRegistrar = (ExecutorServiceRegistrar) tracker.waitForService(timeout);

			destination.setExecutorServiceRegistrar(executorServiceRegistrar);
			
			// TODO: Find some way to assert that the executor service registrar assignment was successful
			// assertEquals(executorServiceRegistrar, destination.getExecutorServiceRegistar());
		}
		finally {
			tbBundle.uninstall();
			tbBundle2.uninstall();
		}
	}

	@Test
	public void testRejectedExecutionHandler()
		throws Exception {
		
		Bundle tbBundle2 = install("tb16.jar");
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();
			tbBundle2.start();

			String destinationName = "parallel/test";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=com.liferay.petra.concurrent.RejectedExecutionHandler)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<?, ?> tracker = new ServiceTracker<>(bundleContext, filter, null);

			tracker.open();

			RejectedExecutionHandler rejectedExecutionHandler = (RejectedExecutionHandler) tracker.waitForService(timeout);

			assertNotNull(rejectedExecutionHandler);

			ParallelDestination destination = (ParallelDestination) messageBus.getDestination(destinationName);
			
			rejectedExecutionHandler = (RejectedExecutionHandler) tracker.waitForService(timeout);

			destination.setRejectedExecutionHandler(rejectedExecutionHandler);
			
			// TODO: Find some way to assert that the rejected execution handler assignment was successful
			// assertEquals(rejectedExecutionHandler, destination.getRejectedExecutionHandler());
		}
		finally {
			tbBundle.uninstall();
			tbBundle2.uninstall();
		}
	}

}
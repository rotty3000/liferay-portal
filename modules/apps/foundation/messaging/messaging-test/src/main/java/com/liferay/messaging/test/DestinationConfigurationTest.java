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
import static org.junit.Assert.fail;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationType;
import com.liferay.messaging.Message;
import com.liferay.petra.concurrent.RejectedExecutionHandler;
import com.liferay.petra.concurrent.ThreadPoolExecutor;

import java.util.concurrent.Callable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 * @author Jesse Rao
 */
public class DestinationConfigurationTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		testSend("tb4.jar", "configuration/tb4");
		testDestinationConfiguration(DestinationType.PARALLEL);
	}

	@Test
	public void testSerial() throws Exception {
		testSend("tb5.jar", "configuration/tb5");
		testDestinationConfiguration(DestinationType.SERIAL);
	}

	@Test
	public void testSynchronous() throws Exception {
		testSend("tb6.jar", "configuration/tb6");
		testDestinationConfiguration(DestinationType.SYNCHRONOUS);
	}

	protected void testDestinationConfiguration(DestinationType destinationType)
		throws Exception {

		String destinationName = null;
		DestinationConfiguration destinationConfiguration = null;

		if (destinationType.equals(DestinationType.SYNCHRONOUS)) {
			destinationName = "SYNCHRONOUS_DESTINATION";
			destinationConfiguration =
				DestinationConfiguration.
				createSynchronousDestinationConfiguration(destinationName);
		}
		else if (destinationType.equals(DestinationType.PARALLEL)) {
			destinationName = "PARALLEL_DESTINATION";
			destinationConfiguration =
				DestinationConfiguration.
				createParallelDestinationConfiguration(destinationName);
		}
		else if (destinationType.equals(DestinationType.SERIAL)) {
			destinationName = "SERIAL_DESTINATION";
			destinationConfiguration =
				DestinationConfiguration.
				createSerialDestinationConfiguration(destinationName);
		}
		else {
			fail("Invalid destination type");
		}

		assertEquals(destinationName,
			destinationConfiguration.getDestinationName());

		assertEquals(destinationType,
			destinationConfiguration.getDestinationType());

		assertEquals(Integer.MAX_VALUE,
			destinationConfiguration.getMaximumQueueSize());
		assertEquals(2, destinationConfiguration.getWorkersCoreSize());
		assertEquals(5, destinationConfiguration.getWorkersMaxSize());
		assertEquals(null,
			destinationConfiguration.getRejectedExecutionHandler());

		destinationConfiguration.setMaximumQueueSize(20);
		destinationConfiguration.setWorkersCoreSize(3);
		destinationConfiguration.setWorkersMaxSize(6);

		RejectedExecutionHandler rejectedExecutionHandler =
				new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable runnable,
					ThreadPoolExecutor threadPoolExecutor) {
			}

		};

		destinationConfiguration.setRejectedExecutionHandler(
			rejectedExecutionHandler);

		assertEquals(20, destinationConfiguration.getMaximumQueueSize());
		assertEquals(3, destinationConfiguration.getWorkersCoreSize());
		assertEquals(6, destinationConfiguration.getWorkersMaxSize());
		assertEquals(rejectedExecutionHandler,
			destinationConfiguration.getRejectedExecutionHandler());

		assertEquals(
			destinationName.hashCode(), destinationConfiguration.hashCode());
	}

	protected void testSend(String bundle, String destination)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destination));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Message message = new Message();

			messageBus.sendMessage(destination, message);

			assertEquals(message, callable.call());
		}
		finally {
			tb.uninstall();
		}
	}

}
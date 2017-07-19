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
import com.liferay.messaging.InboundMessageProcessor;
import com.liferay.messaging.InboundMessageProcessorFactory;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageProcessorException;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jesse Rao
 */
public class DestinationStatisticsTest extends TestUtil {

	public static final int MAX = 10;

	@Test
	public void testParallel() throws Exception {
		test("tb14.jar", "parallel/test");
	}

	@Test
	public void testSerial() throws Exception {
		test("tb15.jar", "serial/test");
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb13.jar", "synchronous/test");
	}

	protected void assertBeforeStats(
		String message, String destinationName,
		DestinationStatistics destinationStatistics) {

		System.out.printf(message, destinationName);
		System.out.printf(
			"  Pending messages: %s%n",
			destinationStatistics.getPendingMessageCount());
		System.out.printf(
			"  Sent messages: %s%n",
			destinationStatistics.getSentMessageCount());
		System.out.printf(
			"  Active threads: %s%n",
			destinationStatistics.getActiveThreadCount());
		System.out.printf(
			"  Current threads: %s%n",
			destinationStatistics.getCurrentThreadCount());
		System.out.printf(
			"  Largest threads: %s%n",
			destinationStatistics.getLargestThreadCount());
		System.out.printf(
			"  Max threads: %s%n",
			destinationStatistics.getMaxThreadPoolSize());
		System.out.printf(
			"  Min threads: %s%n%n",
			destinationStatistics.getMinThreadPoolSize());

		if (destinationName.equals("synchronous/test")) {
			assertEquals(0, destinationStatistics.getPendingMessageCount());
			assertEquals(0, destinationStatistics.getSentMessageCount());
			assertEquals(0, destinationStatistics.getActiveThreadCount());
			assertEquals(0, destinationStatistics.getCurrentThreadCount());
			assertEquals(0, destinationStatistics.getLargestThreadCount());
			assertEquals(0, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(0, destinationStatistics.getMinThreadPoolSize());
		}
		else if (destinationName.equals("parallel/test")) {
			assertEquals(0, destinationStatistics.getPendingMessageCount());
			assertEquals(0, destinationStatistics.getSentMessageCount());
			assertEquals(0, destinationStatistics.getActiveThreadCount());
			assertEquals(0, destinationStatistics.getCurrentThreadCount());
			assertEquals(0, destinationStatistics.getLargestThreadCount());
			assertEquals(5, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(2, destinationStatistics.getMinThreadPoolSize());
		}
		else if (destinationName.equals("serial/test")) {
			assertEquals(0, destinationStatistics.getPendingMessageCount());
			assertEquals(0, destinationStatistics.getSentMessageCount());
			assertEquals(0, destinationStatistics.getActiveThreadCount());
			assertEquals(0, destinationStatistics.getCurrentThreadCount());
			assertEquals(0, destinationStatistics.getLargestThreadCount());
			assertEquals(1, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(1, destinationStatistics.getMinThreadPoolSize());
		}
	}

	protected void assertFinalStats(
		String message, String destinationName,
		DestinationStatistics destinationStatistics) {

		System.out.printf(message, destinationName);
		System.out.printf(
			"  Pending messages: %s%n",
			destinationStatistics.getPendingMessageCount());
		System.out.printf(
			"  Sent messages: %s%n",
			destinationStatistics.getSentMessageCount());
		System.out.printf(
			"  Active threads: %s%n",
			destinationStatistics.getActiveThreadCount());
		System.out.printf(
			"  Current threads: %s%n",
			destinationStatistics.getCurrentThreadCount());
		System.out.printf(
			"  Largest threads: %s%n",
			destinationStatistics.getLargestThreadCount());
		System.out.printf(
			"  Max threads: %s%n",
			destinationStatistics.getMaxThreadPoolSize());
		System.out.printf(
			"  Min threads: %s%n%n",
			destinationStatistics.getMinThreadPoolSize());

		assertEquals(0, destinationStatistics.getPendingMessageCount());

		// We cannot make assertions on the number of sent messages without
		// creating a timing issue. The reason is the thread pool cannot
		// return completely accurate statistics until after shutdown.

		//assertEquals(10, destinationStatistics.getSentMessageCount());

		assertEquals(0, destinationStatistics.getActiveThreadCount());

		if (destinationName.equals("synchronous/test")) {
			assertEquals(0, destinationStatistics.getCurrentThreadCount());
			assertEquals(0, destinationStatistics.getLargestThreadCount());
			assertEquals(0, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(0, destinationStatistics.getMinThreadPoolSize());
		}
		else if (destinationName.equals("parallel/test")) {
			assertEquals(5, destinationStatistics.getCurrentThreadCount());
			assertEquals(5, destinationStatistics.getLargestThreadCount());
			assertEquals(5, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(2, destinationStatistics.getMinThreadPoolSize());
		}
		else if (destinationName.equals("serial/test")) {
			assertEquals(1, destinationStatistics.getCurrentThreadCount());
			assertEquals(1, destinationStatistics.getLargestThreadCount());
			assertEquals(1, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(1, destinationStatistics.getMinThreadPoolSize());
		}
	}

	protected void assertUpdatedStats(
		String message, String destinationName,
		DestinationStatistics destinationStatistics) {

		System.out.printf(message, destinationName);
		System.out.printf(
			"  Pending messages: %s%n",
			destinationStatistics.getPendingMessageCount());
		System.out.printf(
			"  Sent messages: %s%n",
			destinationStatistics.getSentMessageCount());
		System.out.printf(
			"  Active threads: %s%n",
			destinationStatistics.getActiveThreadCount());
		System.out.printf(
			"  Current threads: %s%n",
			destinationStatistics.getCurrentThreadCount());
		System.out.printf(
			"  Largest threads: %s%n",
			destinationStatistics.getLargestThreadCount());
		System.out.printf(
			"  Max threads: %s%n",
			destinationStatistics.getMaxThreadPoolSize());
		System.out.printf(
			"  Min threads: %s%n%n",
			destinationStatistics.getMinThreadPoolSize());

		if (destinationName.equals("synchronous/test")) {
			assertEquals(0, destinationStatistics.getPendingMessageCount());
			assertEquals(10, destinationStatistics.getSentMessageCount());
			assertEquals(0, destinationStatistics.getActiveThreadCount());
			assertEquals(0, destinationStatistics.getCurrentThreadCount());
			assertEquals(0, destinationStatistics.getLargestThreadCount());
			assertEquals(0, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(0, destinationStatistics.getMinThreadPoolSize());
		}
		else if (destinationName.equals("parallel/test")) {
			assertEquals(5, destinationStatistics.getPendingMessageCount());
			assertEquals(0, destinationStatistics.getSentMessageCount());
			assertEquals(5, destinationStatistics.getActiveThreadCount());
			assertEquals(5, destinationStatistics.getCurrentThreadCount());
			assertEquals(5, destinationStatistics.getLargestThreadCount());
			assertEquals(5, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(2, destinationStatistics.getMinThreadPoolSize());
		}
		else if (destinationName.equals("serial/test")) {
			assertEquals(9, destinationStatistics.getPendingMessageCount());
			assertEquals(0, destinationStatistics.getSentMessageCount());
			assertEquals(1, destinationStatistics.getActiveThreadCount());
			assertEquals(1, destinationStatistics.getCurrentThreadCount());
			assertEquals(1, destinationStatistics.getLargestThreadCount());
			assertEquals(1, destinationStatistics.getMaxThreadPoolSize());
			assertEquals(1, destinationStatistics.getMinThreadPoolSize());
		}
	}

	protected void test(String bundle, String destinationName)
		throws Exception {

		Bundle tbBundle = install(bundle);

		ServiceRegistration<?> factoryRegistration = null;

		try {
			tbBundle.start();

			Destination destination = messageBus.getDestination(
				destinationName);

			DestinationStatistics destinationStatistics =
				destination.getDestinationStatistics();

			final CountDownLatch afterThread = new CountDownLatch(MAX);
			final CountDownLatch beforeThread = new CountDownLatch(
				destinationStatistics.getMaxThreadPoolSize());

			InboundMessageProcessor processor = new InboundMessageProcessor() {

				@Override
				public void afterReceive(Message message)
					throws MessageProcessorException {
				}

				@Override
				public void afterThread(Message message, Thread dispatchThread)
					throws MessageProcessorException {

					afterThread.countDown();
				}

				@Override
				public Message beforeReceive(Message message)
					throws MessageProcessorException {

					return message;
				}

				@Override
				public Message beforeThread(
						Message message, Thread dispatchThread)
					throws MessageProcessorException {

					beforeThread.countDown();

					return message;
				}

			};

			InboundMessageProcessorFactory factory =
				new InboundMessageProcessorFactory() {

					@Override
					public InboundMessageProcessor create() {
						return processor;
					}

				};

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("destination.name", destinationName);

			factoryRegistration = bundleContext.registerService(
				InboundMessageProcessorFactory.class, factory, properties);

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

			assertBeforeStats(
				"Before Stats %s:%n", destinationName,
				destination.getDestinationStatistics());

			for (int i = 0; i < MAX; i++) {
				Message message = new Message();

				messageBus.sendMessage(destinationName, message);
			}

			beforeThread.await();

			assertUpdatedStats(
				"Updated Stats %s:%n", destinationName,
				destination.getDestinationStatistics());

			callable.call();

			afterThread.await();

			assertFinalStats(
				"Final Stats %s:%n", destinationName,
				destination.getDestinationStatistics());
		}
		finally {
			tbBundle.uninstall();

			if (factoryRegistration != null) {
				factoryRegistration.unregister();
			}
		}
	}

}
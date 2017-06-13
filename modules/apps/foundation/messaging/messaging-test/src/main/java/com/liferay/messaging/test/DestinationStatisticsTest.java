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

import static org.junit.Assert.assertNotNull;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationStatistics;
import com.liferay.messaging.Message;

import java.util.concurrent.Callable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
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
		//test("tb3.jar", "serial/test");
	}

	@Test
	public void testSynchronous() throws Exception {
		//test("tb1.jar", "synchronous/test");
	}

	protected void test(String bundle, String destinationName)
		throws Exception {

		Bundle tbBundle = install(bundle);

		try {
			tbBundle.start();

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

			Destination destination = messageBus.getDestination(
				destinationName);

			assertStats(
				"Before Stats %s:%n", destinationName,
				destination.getDestinationStatistics());

			for (int i = 0; i < MAX; i++) {
				Message message = new Message();

				messageBus.sendMessage(destinationName, message);
			}

			assertStats(
				"Updated Stats %s:%n", destinationName,
				destination.getDestinationStatistics());

			callable.call();

			Thread.sleep(1000);

			assertStats(
				"Final Stats %s:%n", destinationName,
				destination.getDestinationStatistics());
		}
		finally {
			tbBundle.uninstall();
		}
	}

	protected void assertStats(
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
	}

}
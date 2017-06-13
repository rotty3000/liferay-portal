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

	public static final int MAX = 1234;

	@Test
	public void testParallel() throws Exception {
		test("tb2.jar", "parallel/test");
	}

	@Test
	public void testSerial() throws Exception {
		test("tb3.jar", "serial/test");
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb1.jar", "synchronous/test");
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

			DestinationStatistics destinationStatistics =
				destination.getDestinationStatistics();

			assertEquals(0, destinationStatistics.getSentMessageCount());

			Message message = new Message();

			for (int i = 0; i < MAX; i++) {
				messageBus.sendMessage(destinationName, message);
				assertEquals(message, callable.call());
			}

			destinationStatistics = destination.getDestinationStatistics();

			assertEquals(
				MAX,
				destinationStatistics.getSentMessageCount() +
					destinationStatistics.getPendingMessageCount());

			/*assertEquals(5, destinationStatistics.getActiveThreadCount());
			assertEquals(5, destinationStatistics.getCurrentThreadCount());
			assertEquals(5, destinationStatistics.getLargestThreadCount());*/
		}
		finally {
			tbBundle.uninstall();
		}
	}

}
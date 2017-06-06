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

public class DestinationStatisticsTest extends TestUtil {
	
	public static final int MAX = 1234;

	public void test(String bundle, String destinationName) throws Exception {
		Bundle tbBundle = install(bundle);

		try {
			tbBundle.start();

			Filter filter = bundleContext.createFilter(
				String.format("(&(objectClass=java.util.concurrent.Callable)" +
					"(destination.name=%s))", destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Destination destination = messageBus.getDestination(destinationName);
			
			DestinationStatistics destinationStatistics = destination.getDestinationStatistics();
			
			assertEquals(0, destinationStatistics.getSentMessageCount());

			Message message = new Message();

			for (int i = 0; i < MAX; i++) {
				messageBus.sendMessage(destinationName, message);
				assertEquals(message, callable.call());

			}

			/*
			 * TODO: Solve concurrency issue. Replace this sleep call with a
			 * better way to ensure that the destination statistics are up to
			 * date before calling destinationStatistics.getSentMessageCount().
			 */
			Thread.sleep(1000);
			destinationStatistics = destination.getDestinationStatistics();
			assertEquals(MAX, destinationStatistics.getSentMessageCount());
			
			/*
			 * TODO: Devise a way to test these destination statistics against
			 * expected values.
			 */
			System.out.println("Sent message count for " + destinationName + ": " + destinationStatistics.getSentMessageCount());
			System.out.println("Active thread count for " + destinationName + ": " + destinationStatistics.getActiveThreadCount());
			System.out.println("Current thread count for " + destinationName + ": " + destinationStatistics.getCurrentThreadCount());
			System.out.println("Largest thread count for " + destinationName + ": " + destinationStatistics.getLargestThreadCount());
			System.out.println("Pending message count for " + destinationName + ": " + destinationStatistics.getPendingMessageCount());
		}
		finally {
			tbBundle.uninstall();
		}
	}

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
}

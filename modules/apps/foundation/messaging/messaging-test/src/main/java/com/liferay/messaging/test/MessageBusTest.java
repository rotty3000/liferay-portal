package com.liferay.messaging.test;

import static org.junit.Assert.assertEquals;

import com.liferay.messaging.Destination;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import org.osgi.framework.Bundle;

/**
 * @author Jesse Rao
 */
public class MessageBusTest extends TestUtil {

	public void test(String bundle, String destinationName) throws Exception {
		Bundle tbBundle = install(bundle);

		try {
			tbBundle.start();

			assertEquals(3, messageBus.getDestinationCount());

			Collection<String> destinationNames = new HashSet<String>();
			destinationNames.add(destinationName);
			destinationNames.add("liferay/message_bus/default_response");
			destinationNames.add("liferay/message_bus/message_status");
			assertEquals(destinationNames, messageBus.getDestinationNames());

			assertEquals(true, messageBus.hasDestination(destinationName));

			Destination destination = messageBus.getDestination(destinationName);
			Collection<Destination> destinations = messageBus.getDestinations();
			assertEquals(true, destinations.contains(destination));

			assertEquals(true, messageBus.hasMessageListener(destinationName));
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

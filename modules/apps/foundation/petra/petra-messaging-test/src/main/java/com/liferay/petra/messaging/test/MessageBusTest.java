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

package com.liferay.petra.messaging.test;

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.DestinationNames;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.spi.MessageImpl;
import com.liferay.petra.messaging.test.tb3.TBSerialDestination;
import com.liferay.petra.messaging.test.tb1.TBSynchronousDestination;
import com.liferay.petra.messaging.test.tb2.TBParallelDestination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jesse Rao
 */
public class MessageBusTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		testDestinations("tb2.jar", TBParallelDestination.DESTINATION_NAME);
		testSendMessage("tb2.jar", TBParallelDestination.DESTINATION_NAME);
		testSendSynchronousMessage("tb10.jar", "builder/tb10");
		testSendSynchronousMessage2("tb10.jar", "builder/tb10");
		testSendSynchronousMessage3("tb10.jar", "builder/tb10");
		testSendSynchronousMessage4("tb10.jar", "builder/tb10");
		testSendSynchronousMessage5("tb10.jar", "builder/tb10");
		testSendSynchronousMessage6("tb10.jar", "builder/tb10");
	}

	@Test
	public void testSerial() throws Exception {
		testDestinations("tb3.jar", TBSerialDestination.DESTINATION_NAME);
		testSendMessage("tb3.jar", TBSerialDestination.DESTINATION_NAME);
		testSendSynchronousMessage("tb11.jar", "builder/tb11");
		testSendSynchronousMessage2("tb11.jar", "builder/tb11");
		testSendSynchronousMessage3("tb11.jar", "builder/tb11");
		testSendSynchronousMessage4("tb11.jar", "builder/tb11");
		testSendSynchronousMessage5("tb11.jar", "builder/tb11");
		testSendSynchronousMessage6("tb11.jar", "builder/tb11");
	}

	@Test
	public void testSynchronous() throws Exception {
		testDestinations("tb1.jar", TBSynchronousDestination.DESTINATION_NAME);
		testSendMessage("tb1.jar", TBSynchronousDestination.DESTINATION_NAME);
		testSendSynchronousMessage("tb12.jar", "builder/tb12");
		testSendSynchronousMessage2("tb12.jar", "builder/tb12");
		testSendSynchronousMessage3("tb12.jar", "builder/tb12");
		testSendSynchronousMessage4("tb12.jar", "builder/tb12");
		testSendSynchronousMessage5("tb12.jar", "builder/tb12");
		testSendSynchronousMessage6("tb12.jar", "builder/tb12");
	}

	protected void testDestinations(String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Assert.assertEquals(3, messageBus.getDestinationCount());

			Collection<String> destinationNames = new ArrayList<>();

			destinationNames.add(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
			destinationNames.add(DestinationNames.MESSAGE_BUS_MESSAGE_STATUS);
			destinationNames.add(destinationName);

			Assert.assertArrayEquals(
				destinationNames.toArray(),
				messageBus.getDestinationNames().toArray());

			Assert.assertEquals(
				true, messageBus.hasDestination(destinationName));

			Destination destination = messageBus.getDestination(
				destinationName);
			Collection<Destination> destinations = messageBus.getDestinations();

			Assert.assertEquals(true, destinations.contains(destination));

			Assert.assertEquals(
				true, messageBus.hasMessageListener(destinationName));
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendMessage(String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Message message = new MessageImpl();

			message.setPayload("payload");

			messageBus.sendMessage(destinationName, message);

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Message receivedMessage = callable.call();

			Assert.assertEquals(message, receivedMessage);

			messageBus.sendMessage(destinationName, "payload");

			callableST.open();

			callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			receivedMessage = callable.call();

			Assert.assertEquals("payload", receivedMessage.getPayload());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendSynchronousMessage(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Message message = new MessageImpl();
			String payload = "payload";

			message.setPayload(payload);

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Object response = messageBus.sendSynchronousMessage(
				destinationName, message);

			Message receivedMessage = callable.call();

			Assert.assertEquals(response, receivedMessage);
			Assert.assertEquals(
				message.getPayload(), receivedMessage.getPayload());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendSynchronousMessage2(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Message message = new MessageImpl();
			String payload = "payload";

			message.setPayload(payload);

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Object response = messageBus.sendSynchronousMessage(
				destinationName, message, 1000);

			Message receivedMessage = callable.call();

			Assert.assertEquals(response, receivedMessage);
			Assert.assertEquals(
				message.getPayload(), receivedMessage.getPayload());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendSynchronousMessage3(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			String payload = "payload";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Object response = messageBus.sendSynchronousMessage(
				destinationName, payload);

			Message receivedMessage = callable.call();

			Assert.assertEquals(response, receivedMessage);
			Assert.assertEquals(payload, receivedMessage.getPayload());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendSynchronousMessage4(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			String payload = "payload";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Object response = messageBus.sendSynchronousMessage(
				destinationName, payload, 1000);

			Message receivedMessage = callable.call();

			Assert.assertEquals(response, receivedMessage);
			Assert.assertEquals(payload, receivedMessage.getPayload());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendSynchronousMessage5(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			String payload = "payload";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Object response = messageBus.sendSynchronousMessage(
				destinationName, payload,
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

			Message receivedMessage = callable.call();

			Assert.assertEquals(response, receivedMessage);
			Assert.assertEquals(payload, receivedMessage.getPayload());
			Assert.assertEquals(
				destinationName, receivedMessage.getDestinationName());
			Assert.assertEquals(
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE,
				receivedMessage.getResponseDestinationName());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testSendSynchronousMessage6(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			String payload = "payload";

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Object response = messageBus.sendSynchronousMessage(
				destinationName, payload,
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE, 1000);

			Message receivedMessage = callable.call();

			Assert.assertEquals(response, receivedMessage);
			Assert.assertEquals(payload, receivedMessage.getPayload());
			Assert.assertEquals(
				destinationName, receivedMessage.getDestinationName());
			Assert.assertEquals(
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE,
				receivedMessage.getResponseDestinationName());
		}
		finally {
			tb.uninstall();
		}
	}

}
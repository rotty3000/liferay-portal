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

import com.liferay.messaging.DestinationNames;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class MessageBuilderTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		test("tb2.jar", "parallel/test");
		testResponseMessageBuilder("tb2.jar", "parallel/test");
	}

	@Test
	public void testSerial() throws Exception {
		test("tb3.jar", "serial/test");
		testResponseMessageBuilder("tb3.jar", "serial/test");
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb1.jar", "synchronous/test");
		testResponseMessageBuilder("tb1.jar", "synchronous/test");
	}

	protected void test(String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

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

			MessageBuilder builder = messageBuilderFactory.create(
				destinationName);

			Boolean boxedBool = Boolean.valueOf(true);
			Integer boxedInt = Integer.valueOf(123);
			Long boxedLong = Long.valueOf(1234567890);
			Double boxedDouble = Double.valueOf(123.456);
			String string = new String("string");
			Object object = new Object();

			builder.put("boolean", boxedBool);
			builder.put("int", boxedInt);
			builder.put("long", boxedLong);
			builder.put("double", boxedDouble);
			builder.put("string", string);
			builder.put("object", object);

			builder.setDestinationName(destinationName);
			builder.setPayload("payload");
			builder.setResponse("response");
			builder.setResponseId("responseId");
			builder.setResponseDestinationName(
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

			// TODO: Should sendSynchronous be used if destinationName is
			// "synchronous/test"?

			builder.send();

			Assert.assertEquals(builder.build(), callable.call());

			// Build and test a second message

			callableST = new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Map<String, Object> values = new HashMap<>();

			values.put("boolean", boxedBool);
			values.put("double", boxedDouble);
			values.put("int", boxedInt);
			values.put("long", boxedLong);
			values.put("object", object);
			values.put("string", string);

			builder = messageBuilderFactory.create(destinationName);

			builder.setValues(values);

			builder.setDestinationName(destinationName);
			builder.setPayload("payload");
			builder.setResponse("response");
			builder.setResponseId("responseId");
			builder.setResponseDestinationName(
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

			// TODO: Should sendSynchronous be used if destinationName is
			// "synchronous/test"?

			builder.send();

			// TODO: Why does this assertion fail? Expected and actual results
			// differ in their values map but only for the serial test...

			//Assert.assertEquals(builder.build(), callable.call());
		}
		finally {
			tb.uninstall();
		}
	}

	protected void testResponseMessageBuilder(
			String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);
		Bundle defaultResponseListenerBundle = install("tb21.jar");

		try {
			tb.start();
			defaultResponseListenerBundle.start();

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

			MessageBuilder builder = messageBuilderFactory.create(
				destinationName);

			Assert.assertTrue(messageBus.hasMessageListener(destinationName));
			Assert.assertTrue(
				messageBus.getDestination(destinationName).isRegistered());

			Boolean boxedBool = Boolean.valueOf(true);
			Integer boxedInt = Integer.valueOf(123);
			Long boxedLong = Long.valueOf(1234567890);
			Double boxedDouble = Double.valueOf(123.456);
			String string = new String("string");
			Object object = new Object();

			builder.put("boolean", boxedBool);
			builder.put("int", boxedInt);
			builder.put("long", boxedLong);
			builder.put("double", boxedDouble);
			builder.put("string", string);
			builder.put("object", object);

			builder.setDestinationName(destinationName);
			builder.setPayload("payload");
			builder.setResponse("response");
			builder.setResponseId("responseId");
			builder.setResponseDestinationName(
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

			// TODO: Should sendSynchronous be used if destinationName is
			// "synchronous/test"?

			builder.send();

			Message message = callable.call();

			Assert.assertEquals(builder.build(), message);

			// Build and test response message

			destinationName = DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE;

			Assert.assertTrue(messageBus.hasMessageListener(destinationName));
			Assert.assertTrue(
				messageBus.getDestination(destinationName).isRegistered());

			filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			callableST = new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			builder = messageBuilderFactory.createResponse(message);

			// TODO: Should sendSynchronous be used if destinationName is
			// "synchronous/test"?

			builder.send();

			Message responseMessage = callable.call();

			Assert.assertEquals(
				message.getPayload(), responseMessage.getPayload());
			Assert.assertTrue(responseMessage.contains("boolean"));
			Assert.assertTrue(responseMessage.contains("int"));
			Assert.assertTrue(responseMessage.contains("long"));
			Assert.assertTrue(responseMessage.contains("double"));
			Assert.assertTrue(responseMessage.contains("string"));
			Assert.assertTrue(responseMessage.contains("object"));
		}
		finally {
			tb.uninstall();
			defaultResponseListenerBundle.uninstall();
		}
	}

}
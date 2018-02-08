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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.spi.MessageImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class MessageTest extends TestUtil {

	public static final double DELTA = 1e-100;

	@Test
	public void testMessageSerialization() throws ClassNotFoundException {
		MessageBuilder messageBuilder = messageBuilderFactory.create("destinationName");
		
		messageBuilder.setPayload("payload");

		messageBuilder.put("abc", "123");
		
		Message message = messageBuilder.build();
		
		byte[] serializedMessage = message.toByteArray();

		MessageImpl deserializedMessage = MessageImpl.fromByteArray(serializedMessage);

		Assert.assertEquals(message, deserializedMessage);
	}
	
	@Test
	public void testMessageClone() {
		MessageImpl message = new MessageImpl();

		message.put("abc", "123");

		MessageImpl clonedMessage = (MessageImpl) message.clone();

		Assert.assertEquals(message, clonedMessage);
	}

	@Test
	public void testContains() {
		Message message = new MessageImpl();

		message.put("abc", "123");

		boolean containsABC = message.contains("abc");

		Assert.assertTrue(containsABC);

		boolean contains123 = message.contains("123");

		Assert.assertFalse(contains123);

		boolean containsDEF = message.contains("def");

		Assert.assertFalse(containsDEF);
	}

	@Test
	public void testCopy() {
		MessageImpl message = new MessageImpl();

		String destinationName = "destination/test";
		String payload = "payload";
		String response = "response";
		String responseDestinationName = "responseDestination/test";
		String responseId = "responseId";

		message.setDestinationName(destinationName);
		message.setPayload(payload);
		message.setResponse(response);
		message.setResponseDestinationName(responseDestinationName);
		message.setResponseId(responseId);

		message.put("abc", "123");

		MessageImpl copiedFromMessage = new MessageImpl();

		Assert.assertEquals(null, copiedFromMessage.getDestinationName());
		Assert.assertEquals(null, copiedFromMessage.getPayload());
		Assert.assertEquals(null, copiedFromMessage.getResponse());
		Assert.assertEquals(
			null, copiedFromMessage.getResponseDestinationName());
		Assert.assertEquals(null, copiedFromMessage.getResponseId());

		copiedFromMessage.copyFrom(message);

		Assert.assertEquals(message, copiedFromMessage);

		MessageImpl copiedToMessage = new MessageImpl();

		Assert.assertEquals(null, copiedToMessage.getDestinationName());
		Assert.assertEquals(null, copiedToMessage.getPayload());
		Assert.assertEquals(null, copiedToMessage.getResponse());
		Assert.assertEquals(null, copiedToMessage.getResponseDestinationName());
		Assert.assertEquals(null, copiedToMessage.getResponseId());

		message.copyTo(copiedToMessage);

		Assert.assertEquals(message, copiedToMessage);
	}

	@Test
	public void testGettersSetters() {
		Message message = new MessageImpl();

		String destinationName = "destination/test";
		String payload = "payload";
		String response = "response";
		String responseDestinationName = "responseDestination/test";
		String responseId = "responseId";

		message.setDestinationName(destinationName);
		message.setPayload(payload);
		message.setResponse(response);
		message.setResponseDestinationName(responseDestinationName);
		message.setResponseId(responseId);

		Map<String, Object> values = new HashMap<>();

		Boolean boxedBool = Boolean.valueOf(true);
		Integer boxedInt = Integer.valueOf(123);
		Long boxedLong = Long.valueOf(1234567890);
		Double boxedDouble = Double.valueOf(123.456);
		String string = new String("string");
		Object object = new Object();

		values.put("boolean", boxedBool);
		values.put("double", boxedDouble);
		values.put("int", boxedInt);
		values.put("long", boxedLong);
		values.put("object", object);
		values.put("string", string);

		message.setValues(values);

		Assert.assertEquals((boolean)boxedBool, message.getBoolean("boolean"));
		Assert.assertEquals((int)boxedInt, message.getInteger("int"));
		Assert.assertEquals((long)boxedLong, message.getLong("long"));
		Assert.assertEquals(
			(double)boxedDouble, message.getDouble("double"), DELTA);
		Assert.assertEquals((String)string, message.getString("string"));
		Assert.assertEquals((Object)object, message.get("object"));
		Assert.assertEquals(values, message.getValues());

		Assert.assertFalse(message.contains("extra"));

		message.put("extra", "extraValue");

		Assert.assertTrue(message.contains("extra"));

		message.remove("extra");

		Assert.assertFalse(message.contains("extra"));
	}

}
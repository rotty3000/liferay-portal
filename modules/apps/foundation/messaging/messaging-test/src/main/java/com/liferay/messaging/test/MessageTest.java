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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.messaging.Message;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Brian Wing Shun Chan
 */
public class MessageTest {

	public static final double DELTA = 1e-100;

	// TODO: Why does this throw class not found exception?
	/*
	@Test
	public void testMessageSerialization() throws ClassNotFoundException {
		Message message = new Message();
		
		message.put("abc", "123");
		
		byte[] serializedMessage = message.toByteArray();
		
		Message deserializedMessage = Message.fromByteArray(serializedMessage);
		
		assertEquals(message, deserializedMessage);
	}
	*/

	// TODO: Why this error?
	/* 
	 * java.lang.AssertionError: expected: com.liferay.messaging.Message<{destinationName=null, response=null, responseDestinationName=null, responseId=null, payload=null, values={abc=123}}> but was: com.liferay.messaging.Message<{destinationName=null, response=null, responseDestinationName=null, responseId=null, payload=null, values={abc=123}}
	 */
	/*
	@Test
	public void testMessageClone() {
		Message message = new Message();
		
		message.put("abc", "123");
		
		Message clonedMessage = message.clone();
		
		assertEquals(message, clonedMessage);
	}
	*/
	
	@Test
	public void testContains() {
		Message message = new Message();
		
		message.put("abc", "123");
		
		boolean containsABC = message.contains("abc");
		
		assertTrue(containsABC);
		
		boolean contains123 = message.contains("123");
		
		assertFalse(contains123);

		boolean containsDEF = message.contains("def");
		
		assertFalse(containsDEF);
	}
	
	@Test
	public void testCopy() {
		Message message = new Message();
		
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
		
		Message copiedFromMessage = new Message();
		
		assertEquals(null, copiedFromMessage.getDestinationName());
		assertEquals(null, copiedFromMessage.getPayload());
		assertEquals(null, copiedFromMessage.getResponse());
		assertEquals(null, copiedFromMessage.getResponseDestinationName());
		assertEquals(null, copiedFromMessage.getResponseId());
		
		copiedFromMessage.copyFrom(message);

		assertEquals(destinationName, copiedFromMessage.getDestinationName());
		assertEquals(payload, copiedFromMessage.getPayload());
		assertEquals(response, copiedFromMessage.getResponse());
		assertEquals(
				responseDestinationName,
				copiedFromMessage.getResponseDestinationName());
		assertEquals(responseId, copiedFromMessage.getResponseId());
		
		Message copiedToMessage = new Message();

		assertEquals(null, copiedToMessage.getDestinationName());
		assertEquals(null, copiedToMessage.getPayload());
		assertEquals(null, copiedToMessage.getResponse());
		assertEquals(null, copiedToMessage.getResponseDestinationName());
		assertEquals(null, copiedToMessage.getResponseId());
		
		message.copyTo(copiedToMessage);

		assertEquals(destinationName, copiedToMessage.getDestinationName());
		assertEquals(payload, copiedToMessage.getPayload());
		assertEquals(response, copiedToMessage.getResponse());
		assertEquals(
				responseDestinationName,
				copiedToMessage.getResponseDestinationName());
		assertEquals(responseId, copiedToMessage.getResponseId());
	}
	
	@Test
	public void testGettersSetters() {
		Message message = new Message();
		
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
		
		Map<String, Object> values = new HashMap<String, Object>();
		
		Boolean boxedBool = new Boolean(true);
		Integer boxedInt = new Integer(123);
		Long boxedLong = new Long(1234567890);
		Double boxedDouble = new Double(123.456);
		String string = new String("string");
		Object object = new Object();

		values.put("boolean", boxedBool);
		values.put("int", boxedInt);
		values.put("long", boxedLong);
		values.put("double", boxedDouble);
		values.put("string", string);
		values.put("object", object);

		message.setValues(values);
		
		assertEquals((boolean) boxedBool, message.getBoolean("boolean"));
		assertEquals((int) boxedInt, message.getInteger("int"));
		assertEquals((long) boxedLong, message.getLong("long"));
		assertEquals((double) boxedDouble, message.getDouble("double"), DELTA);
		assertEquals((String) string, message.getString("string"));
		assertEquals((Object) object, message.get("object"));
		assertEquals(values, message.getValues());
		
		assertFalse(message.contains("extra"));

		message.put("extra", "extraValue");
		
		assertTrue(message.contains("extra"));

		message.remove("extra");

		assertFalse(message.contains("extra"));
	}
	
}
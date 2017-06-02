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

package com.liferay.messaging.impl.internal.sender;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationNames;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBusException;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.SerialDestination;
import com.liferay.messaging.SynchronousDestination;
import com.liferay.messaging.impl.internal.DefaultMessageBus;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class DefaultSynchronousMessageSenderTest {

	@Before
	public void setUp() {
		_messageBus = new DefaultMessageBus();

		SynchronousDestination synchronousDestination =
			new SynchronousDestination();

		synchronousDestination.setName(
			DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

		_messageBus.registerDestination(
			synchronousDestination, doGetProperties(synchronousDestination));

		_defaultSynchronousMessageSender =
			new DefaultSynchronousMessageSender();

		_defaultSynchronousMessageSender.setMessageBus(_messageBus);
		_defaultSynchronousMessageSender.setTimeout(10000);

		synchronousDestination.open();
	}

	@After
	public void tearDown() {
		_messageBus.shutdown(true);
	}

	@Test
	public void testSendToAsyncDestination() throws MessageBusException {
		SerialDestination serialDestination = new SerialDestination() {

			@Override
			public void open() {
				super.open();
			}

		};

		serialDestination.setName("testSerialDestination");

		serialDestination.afterPropertiesSet();

		serialDestination.open();

		doTestSend(serialDestination);
	}

	@Test
	public void testSendToSynchronousDestination() throws MessageBusException {
		SynchronousDestination synchronousDestination =
			new SynchronousDestination();

		synchronousDestination.setName("testSynchronousDestination");

		synchronousDestination.afterPropertiesSet();

		synchronousDestination.open();

		doTestSend(synchronousDestination);
	}

	protected Map<String, Object> doGetProperties(Destination destination) {
		return Collections.singletonMap(
			"destination.name", destination.getName());
	}

	protected void doTestSend(Destination destination)
		throws MessageBusException {

		Object response = new Object();

		destination.register(new ReplayMessageListener(response));

		_messageBus.registerDestination(
			destination, doGetProperties(destination));

		try {
			Assert.assertSame(
				response,
				_defaultSynchronousMessageSender.send(
					destination.getName(), new Message()));
		}
		finally {
			_messageBus.unregisterDestination(
				destination, doGetProperties(destination));

			destination.close(true);
		}
	}

	private DefaultSynchronousMessageSender _defaultSynchronousMessageSender;
	private DefaultMessageBus _messageBus;

	private class ReplayMessageListener implements MessageListener {

		public ReplayMessageListener(Object response) {
			_response = response;
		}

		@Override
		public void receive(Message message) {
			Message responseMessage = new Message();

			responseMessage.setDestinationName(
				message.getResponseDestinationName());
			responseMessage.setResponseId(message.getResponseId());

			responseMessage.setPayload(_response);

			_messageBus.sendMessage(
				message.getResponseDestinationName(), responseMessage);
		}

		private final Object _response;

	}

}
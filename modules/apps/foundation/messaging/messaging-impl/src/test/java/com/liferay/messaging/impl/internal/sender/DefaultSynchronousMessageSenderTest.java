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
import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationNames;
import com.liferay.messaging.DestinationType;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBusException;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.impl.internal.DefaultMessageBus;
import com.liferay.messaging.spi.BaseDestination;
import com.liferay.messaging.spi.DestinationFactory;
import com.liferay.messaging.spi.MessageImpl;
import com.liferay.messaging.spi.ParallelDestination;
import com.liferay.messaging.spi.SerialDestination;
import com.liferay.messaging.spi.SynchronousDestination;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.osgi.framework.Constants;

/**
 * @author Shuyang Zhou
 */
public class DefaultSynchronousMessageSenderTest {

	@Before
	public void setUp() {
		_messageBus = new DefaultMessageBus();

		_messageBus.registerDestinationFactory(
			new DestinationFactory() {

				@Override
				public Destination createDestination(
					DestinationConfiguration destinationConfiguration,
					Map<String, Object> properties) {

					BaseDestination destination;

					switch (destinationConfiguration.getDestinationType()) {
						case PARALLEL:
							destination = new ParallelDestination();
							break;
						case SERIAL:
							destination = new SerialDestination();
							break;
						default:
							destination = new SynchronousDestination();
					}

					destination.setName(
						destinationConfiguration.getDestinationName());
					destination.afterPropertiesSet();
					destination.open();

					return destination;
				}

				@Override
				public void dispose(Destination destination) {
					BaseDestination baseDestination =
						(BaseDestination)destination;

					baseDestination.close();
				}

			});

		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationType.SYNCHRONOUS,
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

		_messageBus.registerDestinationConfiguration(
			destinationConfiguration,
			doGetProperties(destinationConfiguration));

		_defaultSynchronousMessageSender =
			new DefaultSynchronousMessageSender();

		_defaultSynchronousMessageSender.setMessageBus(_messageBus);
		_defaultSynchronousMessageSender.setTimeout(10000);
	}

	@Test
	public void testSendToAsyncDestination() throws MessageBusException {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationType.SERIAL, "testSerialDestination");

		doTestSend(destinationConfiguration);
	}

	@Test
	public void testSendToSynchronousDestination() throws MessageBusException {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationType.SYNCHRONOUS, "testSynchronousDestination");

		doTestSend(destinationConfiguration);
	}

	protected Map<String, Object> doGetProperties(
		DestinationConfiguration destinationConfiguration) {

		Map<String, Object> properties = new HashMap<>();

		properties.put(
			"destination.name", destinationConfiguration.getDestinationName());
		properties.put(Constants.SERVICE_ID, _serviceId.incrementAndGet());
		properties.put(Constants.SERVICE_RANKING, Long.valueOf(0));

		return properties;
	}

	protected void doTestSend(DestinationConfiguration destinationConfiguration)
		throws MessageBusException {

		String destinationName = destinationConfiguration.getDestinationName();

		Map<String, Object> properties = doGetProperties(
			destinationConfiguration);

		_messageBus.registerDestinationConfiguration(
			destinationConfiguration, properties);

		Object response = new Object();

		Destination destination = _messageBus.getDestination(destinationName);

		BaseDestination baseDestination = (BaseDestination)destination;

		baseDestination.addMessageListener(
			new ReplayMessageListener(response),
			doGetProperties(destinationConfiguration));

		try {
			Assert.assertSame(
				response,
				_defaultSynchronousMessageSender.send(
					destination.getName(), new MessageImpl()));
		}
		finally {
			_messageBus.unregisterDestinationConfiguration(
				destinationConfiguration, properties);
		}
	}

	private DefaultSynchronousMessageSender _defaultSynchronousMessageSender;
	private DefaultMessageBus _messageBus;
	private final AtomicLong _serviceId = new AtomicLong();

	private class ReplayMessageListener implements MessageListener {

		public ReplayMessageListener(Object response) {
			_response = response;
		}

		@Override
		public void receive(Message message) {
			Message responseMessage = new MessageImpl();

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
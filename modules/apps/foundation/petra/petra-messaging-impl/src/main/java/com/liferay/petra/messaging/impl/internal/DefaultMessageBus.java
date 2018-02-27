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

package com.liferay.petra.messaging.impl.internal;

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBus;
import com.liferay.petra.messaging.api.MessageBusEventListener;
import com.liferay.petra.messaging.api.MessageBusException;
import com.liferay.petra.messaging.api.MessageProcessorException;
import com.liferay.petra.messaging.api.OutboundMessageProcessor;
import com.liferay.petra.messaging.impl.configuration.MessageBusConfiguration;
import com.liferay.petra.messaging.spi.BaseDestination;
import com.liferay.petra.messaging.spi.DestinationFactory;
import com.liferay.petra.messaging.spi.MessageImpl;
import com.liferay.petra.messaging.spi.ServiceMaps;
import com.liferay.petra.messaging.spi.sender.SingleDestinationMessageSenderFactory;
import com.liferay.petra.messaging.spi.sender.SynchronousMessageSender;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael C. Han
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	service =
		{DefaultMessageBus.class, MessageBus.class}
)
public class DefaultMessageBus implements MessageBus {

	@Override
	public Destination getDestination(String destinationName) {
		Collection<Destination> values = _destinations.values();

		Stream<Destination> stream = values.stream();

		return (Destination)stream.filter(
			destination -> destination.getName().equals(destinationName)
		).findFirst(
		).orElse(
			null
		);
	}

	@Override
	public int getDestinationCount() {
		return _destinations.size();
	}

	@Override
	public Collection<String> getDestinationNames() {
		Collection<Destination> values = _destinations.values();

		Stream<Destination> stream = values.stream();

		List<String> list = stream.map(
			destination -> destination.getName()
		).distinct(
		).sorted(
		).collect(
			Collectors.toList()
		);

		return Collections.unmodifiableCollection(list);
	}

	@Override
	public Collection<Destination> getDestinations() {
		return Collections.unmodifiableCollection(_destinations.values());
	}

	@Override
	public boolean hasDestination(String destinationName) {
		Destination destination = getDestination(destinationName);

		if (destination != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean hasMessageListener(String destinationName) {
		Destination destination = getDestination(destinationName);

		if ((destination != null) && destination.isRegistered()) {
			return true;
		}
		else {
			return false;
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unregisterDestinationConfiguration"
	)
	public void registerDestinationConfiguration(
		DestinationConfiguration destinationConfiguration,
		Map<String, Object> properties) {

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration, properties);

		_destinations.put(properties, destination);

		for (MessageBusEventListener messageBusEventListener :
				_messageBusEventListeners.values()) {

			messageBusEventListener.destinationAdded(destination);
		}
	}

	/**
	 * Only use this for testing! We really want to use field injection here.
	 */
	public void registerDestinationFactory(
		DestinationFactory destinationFactory) {

		_destinationFactory = destinationFactory;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unregisterMessageBusEventListener"
	)
	public void registerMessageBusEventListener(
		MessageBusEventListener messageBusEventListener,
		Map<String, Object> properties) {

		_messageBusEventListeners.put(properties, messageBusEventListener);

		for (Destination destination : getDestinations()) {
			messageBusEventListener.destinationAdded(destination);
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unregisterSingleDestinationMessageSenderFactory"
	)
	public void registerSingleDestinationMessageSenderFactory(
		SingleDestinationMessageSenderFactory
			singleDestinationMessageSenderFactory) {

		_singleDestinationMessageSenderFactory =
			singleDestinationMessageSenderFactory;
	}

	@Override
	public void sendMessage(String destinationName, Message message) {
		Destination destination = getDestination(destinationName);

		if (destination == null) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					"Destination {} is not configured", destinationName);
			}

			return;
		}

		message.setDestinationName(destinationName);

		BaseDestination baseDestination = (BaseDestination)destination;

		Collection<OutboundMessageProcessor> outboundMessageProcessors =
			baseDestination.getOutboundMessageProcessors();

		try {
			for (OutboundMessageProcessor outboundMessageProcessor :
					outboundMessageProcessors) {

				try {
					message = outboundMessageProcessor.beforeSend(message);
				}
				catch (MessageProcessorException mpe) {
					throw new MessageBusException(
						"Unable to process message before sending " + message,
						mpe);
				}
			}

			com.liferay.petra.messaging.spi.Destination spiDestination =
				(com.liferay.petra.messaging.spi.Destination)destination;

			spiDestination.send(message);
		}
		finally {
			for (OutboundMessageProcessor outboundMessageProcessor :
					outboundMessageProcessors) {

				try {
					outboundMessageProcessor.afterSend(message);
				}
				catch (MessageProcessorException mpe) {
					throw new MessageBusException(
						"Unable to process message after sending " + message,
						mpe);
				}
			}
		}
	}

	@Override
	public void sendMessage(String destinationName, Object payload) {
		Message message = new MessageImpl();

		message.setPayload(payload);

		sendMessage(destinationName, message);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Message message) {

		final SingleDestinationMessageSenderFactory
			singleDestinationMessageSenderFactory =
				_singleDestinationMessageSenderFactory;

		if (singleDestinationMessageSenderFactory == null) {
			throw new IllegalStateException(
				"singleDestinationMessageSenderFactory is not available!");
		}

		SynchronousMessageSender synchronousMessageSender =
			singleDestinationMessageSenderFactory.getSynchronousMessageSender(
				_synchronousMessageSenderMode);

		return synchronousMessageSender.send(destinationName, message);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Message message, long timeout) {

		final SingleDestinationMessageSenderFactory
			singleDestinationMessageSenderFactory =
				_singleDestinationMessageSenderFactory;

		if (singleDestinationMessageSenderFactory == null) {
			throw new IllegalStateException(
				"singleDestinationMessageSenderFactory is not available!");
		}

		SynchronousMessageSender synchronousMessageSender =
			singleDestinationMessageSenderFactory.getSynchronousMessageSender(
				_synchronousMessageSenderMode);

		return synchronousMessageSender.send(destinationName, message, timeout);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Object payload) {

		return sendSynchronousMessage(destinationName, payload, null);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Object payload, long timeout) {

		return sendSynchronousMessage(destinationName, payload, null, timeout);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Object payload,
		String responseDestinationName) {

		Message message = new MessageImpl();

		message.setResponseDestinationName(responseDestinationName);
		message.setPayload(payload);

		return sendSynchronousMessage(destinationName, message);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Object payload, String responseDestinationName,
		long timeout) {

		Message message = new MessageImpl();

		message.setResponseDestinationName(responseDestinationName);
		message.setPayload(payload);

		return sendSynchronousMessage(destinationName, message, timeout);
	}

	public void unregisterDestinationConfiguration(
		DestinationConfiguration destinationConfiguration,
		Map<String, Object> properties) {

		Destination destination = _destinations.remove(properties);

		for (MessageBusEventListener messageBusEventListener :
				_messageBusEventListeners.values()) {

			messageBusEventListener.destinationRemoved(destination);
		}

		_destinationFactory.dispose(destination);
	}

	public void unregisterMessageBusEventListener(
		MessageBusEventListener messageBusEventListener,
		Map<String, Object> properties) {

		_messageBusEventListeners.remove(properties);

		for (Destination destination : getDestinations()) {
			messageBusEventListener.destinationRemoved(destination);
		}
	}

	public void unregisterSingleDestinationMessageSenderFactory(
		SingleDestinationMessageSenderFactory
			singleDestinationMessageSenderFactory) {

		_singleDestinationMessageSenderFactory = null;
	}

	@Activate
	protected void activate(MessageBusConfiguration messageBusConfiguration) {
		_synchronousMessageSenderMode =
			messageBusConfiguration.synchronousMessageSenderMode();
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		DefaultMessageBus.class);

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private DestinationFactory _destinationFactory;

	private final Map<Map<String, Object>, Destination> _destinations =
		new ConcurrentSkipListMap<>(ServiceMaps.comparator().reversed());
	private final Map<Map<String, Object>, MessageBusEventListener>
		_messageBusEventListeners = new ConcurrentSkipListMap<>(
			ServiceMaps.comparator().reversed());
	private volatile SingleDestinationMessageSenderFactory
		_singleDestinationMessageSenderFactory;
	private SynchronousMessageSender.Mode _synchronousMessageSenderMode;

}
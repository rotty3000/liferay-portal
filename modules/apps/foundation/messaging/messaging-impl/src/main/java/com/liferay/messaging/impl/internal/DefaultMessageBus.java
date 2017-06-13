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

package com.liferay.messaging.impl.internal;

import com.liferay.messaging.BaseAsyncDestination;
import com.liferay.messaging.BaseDestination;
import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationEventListener;
import com.liferay.messaging.DestinationFactory;
import com.liferay.messaging.InboundMessageProcessorFactory;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageBusEventListener;
import com.liferay.messaging.MessageBusException;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageProcessorException;
import com.liferay.messaging.OutboundMessageProcessor;
import com.liferay.messaging.OutboundMessageProcessorFactory;
import com.liferay.messaging.impl.configuration.DestinationWorkerConfiguration;
import com.liferay.messaging.impl.configuration.MessageBusConfiguration;
import com.liferay.messaging.sender.SingleDestinationMessageSenderFactory;
import com.liferay.messaging.sender.SynchronousMessageSender;
import com.liferay.petra.io.convert.Conversions;
import com.liferay.petra.io.convert.Lists;
import com.liferay.petra.io.convert.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true,
	property = Constants.SERVICE_PID + "=com.liferay.portal.messaging.configuration.DestinationWorkerConfiguration",
	service = {
		DefaultMessageBus.class, ManagedServiceFactory.class, MessageBus.class
	}
)
public class DefaultMessageBus implements ManagedServiceFactory, MessageBus {

	@Override
	public void deleted(String factoryPid) {
		String destinationName = _factoryPidsToDestinationName.remove(
			factoryPid);

		_destinationWorkerConfigurations.remove(destinationName);
	}

	@Override
	public Destination getDestination(String destinationName) {
		return _destinations.get(destinationName);
	}

	@Override
	public int getDestinationCount() {
		return _destinations.size();
	}

	@Override
	public Collection<String> getDestinationNames() {
		return _destinations.keySet();
	}

	@Override
	public Collection<Destination> getDestinations() {
		return _destinations.values();
	}

	@Override
	public String getName() {
		return "Default Message Bus";
	}

	@Override
	public boolean hasDestination(String destinationName) {
		return _destinations.containsKey(destinationName);
	}

	@Override
	public boolean hasMessageListener(String destinationName) {
		Destination destination = _destinations.get(destinationName);

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
		target = "(destination.name=*)", unbind = "unregisterDestination"
	)
	public synchronized void registerDestination(
		Destination destination, Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		if (BaseDestination.class.isInstance(destination)) {
			BaseDestination baseDestination = (BaseDestination)destination;

			baseDestination.setName(destinationName);
			baseDestination.afterPropertiesSet();
			baseDestination.open();
		}

		if (_destinations.containsKey(destination.getName())) {
			replace(destination);
		}
		else {
			doAddDestination(destination);
		}

		DestinationWorkerConfiguration destinationWorkerConfiguration =
			_destinationWorkerConfigurations.get(destinationName);

		updateDestination(destination, destinationWorkerConfiguration);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unregisterDestinationConfiguration"
	)
	public synchronized void registerDestinationConfiguration(
		DestinationConfiguration destinationConfiguration,
		Map<String, Object> properties) {

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		properties = new HashMap<>(properties);

		properties.put(
			"destination.name", destinationConfiguration.getDestinationName());

		registerDestination(destination, properties);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)",
		unbind = "unregisterDestinationEventListener"
	)
	public synchronized void registerDestinationEventListener(
		DestinationEventListener destinationEventListener,
		Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			destination.register(destinationEventListener);

			return;
		}

		List<DestinationEventListener> queuedDestinationEventListeners =
			_queuedDestinationEventListeners.get(destinationName);

		if (queuedDestinationEventListeners == null) {
			queuedDestinationEventListeners = new ArrayList<>();

			_queuedDestinationEventListeners.put(
				destinationName, queuedDestinationEventListeners);
		}

		queuedDestinationEventListeners.add(destinationEventListener);

		if (_logger.isWarnEnabled()) {
			_logger.warn(
				"Queuing destination event listener until destination {} is " +
					"added",
				destinationName);
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)",
		unbind = "unregisterInboundMessageProcessorFactory"
	)
	public synchronized void registerInboundMessageProcessorFactory(
		InboundMessageProcessorFactory inboundMessageProcessorFactory,
		Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			destination.register(inboundMessageProcessorFactory);

			return;
		}

		List<InboundMessageProcessorFactory>
			queuedInboundMessageProcessorFactories =
				_queuedInboundMessageProcessorFactories.get(destinationName);

		if (queuedInboundMessageProcessorFactories == null) {
			queuedInboundMessageProcessorFactories = new ArrayList<>();

			_queuedInboundMessageProcessorFactories.put(
				destinationName, queuedInboundMessageProcessorFactories);
		}

		queuedInboundMessageProcessorFactories.add(
			inboundMessageProcessorFactory);

		if (_logger.isWarnEnabled()) {
			_logger.warn(
				"Queuing inbound processor factory until destination {} is " +
					"added",
				destinationName);
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unregisterMessageBusEventListener"
	)
	public synchronized void registerMessageBusEventListener(
		MessageBusEventListener messageBusEventListener) {

		_messageBusEventListeners.add(messageBusEventListener);

		for (Destination destination : getDestinations()) {
			messageBusEventListener.destinationAdded(destination);
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)", unbind = "unregisterMessageListener"
	)
	public synchronized void registerMessageListener(
		MessageListener messageListener, Map<String, Object> properties) {

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		try {
			ClassLoader operatingClassLoader = (ClassLoader)properties.get(
				"message.listener.operating.class.loader");

			if (operatingClassLoader != null) {
				currentThread.setContextClassLoader(operatingClassLoader);
			}

			String destinationName = Maps.getString(
				properties, "destination.name");

			registerMessageListener(destinationName, messageListener);
		}
		finally {
			currentThread.setContextClassLoader(contextClassLoader);
		}
	}

	public synchronized boolean registerMessageListener(
		String destinationName, MessageListener messageListener) {

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			return destination.register(messageListener);
		}

		List<MessageListener> queuedMessageListeners =
			_queuedMessageListeners.get(destinationName);

		if (queuedMessageListeners == null) {
			queuedMessageListeners = new ArrayList<>();

			_queuedMessageListeners.put(
				destinationName, queuedMessageListeners);
		}

		queuedMessageListeners.add(messageListener);

		if (_logger.isWarnEnabled()) {
			_logger.warn(
				"Queuing message listener until destination {} is added",
				destinationName);
		}

		return false;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)",
		unbind = "unregisterOutboundMessageProcessorFactory"
	)
	public synchronized void registerOutboundMessageProcessorFactory(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory,
		Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			destination.register(outboundMessageProcessorFactory);

			return;
		}

		List<OutboundMessageProcessorFactory>
			queuedOutboundMessageProcessorFactories =
				_queuedOutboundMessageProcessorFactories.get(destinationName);

		if (queuedOutboundMessageProcessorFactories == null) {
			queuedOutboundMessageProcessorFactories = new ArrayList<>();

			_queuedOutboundMessageProcessorFactories.put(
				destinationName, queuedOutboundMessageProcessorFactories);
		}

		queuedOutboundMessageProcessorFactories.add(
			outboundMessageProcessorFactory);

		if (_logger.isWarnEnabled()) {
			_logger.warn(
				"Queuing outbound processor factory until destination {} is " +
					"added",
				destinationName);
		}
	}

	public void replace(Destination destination) {
		replace(destination, true);
	}

	public synchronized void replace(
		Destination destination, boolean closeOnRemove) {

		Destination oldDestination = _destinations.get(destination.getName());

		oldDestination.copyDestinationEventListeners(destination);
		oldDestination.copyInboundMessageProcessorFactories(destination);
		oldDestination.copyMessageListeners(destination);
		oldDestination.copyOutboundMessageProcessorFactories(destination);

		removeDestination(oldDestination.getName(), closeOnRemove);

		doAddDestination(destination);

		destination.open();
	}

	@Override
	public void sendMessage(String destinationName, Message message) {
		Destination destination = _destinations.get(destinationName);

		if (destination == null) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					"Destination {} is not configured", destinationName);
			}

			return;
		}

		message.setDestinationName(destinationName);

		Message sendingMessage = message;

		List<OutboundMessageProcessor> outboundMessageProcessors =
			_getOutboundMessageProcessors(destination);

		try {
			for (OutboundMessageProcessor outboundMessageProcessor :
					outboundMessageProcessors) {

				try {
					sendingMessage = outboundMessageProcessor.beforeSend(
						sendingMessage);
				}
				catch (MessageProcessorException mpe) {
					throw new MessageBusException(
						"Unable to process message before sending " +
							sendingMessage,
						mpe);
				}
			}

			destination.send(sendingMessage);
		}
		finally {
			for (OutboundMessageProcessor outboundMessageProcessor :
					outboundMessageProcessors) {

				try {
					outboundMessageProcessor.afterSend(sendingMessage);
				}
				catch (MessageProcessorException mpe) {
					throw new MessageBusException(
						"Unable to process message after sending " +
							sendingMessage,
						mpe);
				}
			}
		}
	}

	@Override
	public void sendMessage(String destinationName, Object payload) {
		Message message = new Message();

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

		Message message = new Message();

		message.setResponseDestinationName(responseDestinationName);
		message.setPayload(payload);

		return sendSynchronousMessage(destinationName, message);
	}

	@Override
	public Object sendSynchronousMessage(
		String destinationName, Object payload, String responseDestinationName,
		long timeout) {

		Message message = new Message();

		message.setResponseDestinationName(responseDestinationName);
		message.setPayload(payload);

		return sendSynchronousMessage(destinationName, message, timeout);
	}

	public void shutdown() {
		shutdown(false);
	}

	public synchronized void shutdown(boolean force) {
		for (Destination destination : _destinations.values()) {
			destination.close(force);
		}
	}

	public synchronized void unregisterDestination(
		Destination destination, Map<String, Object> properties) {

		removeDestination(destination.getName(), true);

		destination.destroy();
	}

	public synchronized void unregisterDestinationConfiguration(
		DestinationConfiguration destinationConfiguration,
		Map<String, Object> properties) {

		Destination destination = getDestination(
			destinationConfiguration.getDestinationName());

		unregisterDestination(destination, properties);
	}

	public synchronized void unregisterDestinationEventListener(
		DestinationEventListener destinationEventListener,
		Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			destination.unregister(destinationEventListener);

			return;
		}

		List<DestinationEventListener> queuedDestinationEventListeners =
			_queuedDestinationEventListeners.get(destinationName);

		if (Lists.isEmpty(queuedDestinationEventListeners)) {
			return;
		}

		queuedDestinationEventListeners.remove(destinationEventListener);
	}

	public synchronized void unregisterInboundMessageProcessorFactory(
		InboundMessageProcessorFactory inboundMessageProcessorFactory,
		Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			destination.unregister(inboundMessageProcessorFactory);

			return;
		}

		List<InboundMessageProcessorFactory>
			queuedInboundMessageProcessorFactories =
				_queuedInboundMessageProcessorFactories.get(destinationName);

		if (Lists.isEmpty(queuedInboundMessageProcessorFactories)) {
			return;
		}

		queuedInboundMessageProcessorFactories.remove(
			inboundMessageProcessorFactory);
	}

	public synchronized void unregisterMessageBusEventListener(
		MessageBusEventListener messageBusEventListener) {

		_messageBusEventListeners.remove(messageBusEventListener);

		for (Destination destination : getDestinations()) {
			messageBusEventListener.destinationRemoved(destination);
		}
	}

	public synchronized void unregisterMessageListener(
		MessageListener messageListener, Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		unregisterMessageListener(destinationName, messageListener);
	}

	public synchronized boolean unregisterMessageListener(
		String destinationName, MessageListener messageListener) {

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			return destination.unregister(messageListener);
		}

		List<MessageListener> queuedMessageListeners =
			_queuedMessageListeners.get(destinationName);

		if (Lists.isEmpty(queuedMessageListeners)) {
			return false;
		}

		return queuedMessageListeners.remove(messageListener);
	}

	public synchronized void unregisterOutboundMessageProcessorFactory(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory,
		Map<String, Object> properties) {

		String destinationName = Maps.getString(properties, "destination.name");

		Destination destination = _destinations.get(destinationName);

		if (destination != null) {
			destination.unregister(outboundMessageProcessorFactory);

			return;
		}

		List<OutboundMessageProcessorFactory>
			queuedOutboundMessageProcessorFactories =
				_queuedOutboundMessageProcessorFactories.get(destinationName);

		if (Lists.isEmpty(queuedOutboundMessageProcessorFactories)) {
			return;
		}

		queuedOutboundMessageProcessorFactories.remove(
			outboundMessageProcessorFactory);
	}

	@Override
	public void updated(String factoryPid, Dictionary<String, ?> dictionary)
		throws ConfigurationException {

		DestinationWorkerConfiguration destinationWorkerConfiguration =
			Conversions.convert(
				dictionary).to(DestinationWorkerConfiguration.class);

		_factoryPidsToDestinationName.put(
			factoryPid, destinationWorkerConfiguration.destinationName());

		_destinationWorkerConfigurations.put(
			destinationWorkerConfiguration.destinationName(),
			destinationWorkerConfiguration);

		Destination destination = _destinations.get(
			destinationWorkerConfiguration.destinationName());

		updateDestination(destination, destinationWorkerConfiguration);
	}

	@Activate
	protected void activate(MessageBusConfiguration messageBusConfiguration) {
		_synchronousMessageSenderMode =
			messageBusConfiguration.synchronousMessageSenderMode();
	}

	@Deactivate
	protected void deactivate() {
		shutdown(true);

		for (Destination destination : _destinations.values()) {
			destination.destroy();
		}

		_messageBusEventListeners.clear();

		_destinations.clear();
	}

	protected void doAddDestination(Destination destination) {
		_destinations.put(destination.getName(), destination);

		for (MessageBusEventListener messageBusEventListener :
				_messageBusEventListeners) {

			messageBusEventListener.destinationAdded(destination);
		}

		List<DestinationEventListener> destinationEventListeners =
			_queuedDestinationEventListeners.remove(destination.getName());

		if (!Lists.isEmpty(destinationEventListeners)) {
			if (_logger.isDebugEnabled()) {
				_logger.debug(
					"Registering {} queued destination event listeners for " +
						"destination {}",
					destinationEventListeners.size(), destination.getName());
			}

			for (DestinationEventListener destinationEventListener :
					destinationEventListeners) {

				destination.register(destinationEventListener);
			}
		}

		List<InboundMessageProcessorFactory> inboundMessageProcessorFactories =
			_queuedInboundMessageProcessorFactories.remove(
				destination.getName());

		if (!Lists.isEmpty(inboundMessageProcessorFactories)) {
			if (_logger.isDebugEnabled()) {
				_logger.debug(
					"Registering {} queued inbound processor factories for " +
						"destination {}",
					inboundMessageProcessorFactories.size(),
					destination.getName());
			}

			for (InboundMessageProcessorFactory inboundMessageProcessorFactory :
					inboundMessageProcessorFactories) {

				destination.register(inboundMessageProcessorFactory);
			}
		}

		List<MessageListener> messageListeners = _queuedMessageListeners.remove(
			destination.getName());

		if (!Lists.isEmpty(messageListeners)) {
			if (_logger.isDebugEnabled()) {
				_logger.debug(
					"Registering {} queued message listeners for destination " +
						"{}",
					messageListeners.size(), destination.getName());
			}

			for (MessageListener messageListener : messageListeners) {
				destination.register(messageListener);
			}
		}

		List<OutboundMessageProcessorFactory>
			queuedOutboundMessageProcessorFactories =
				_queuedOutboundMessageProcessorFactories.remove(
					destination.getName());

		if (!Lists.isEmpty(queuedOutboundMessageProcessorFactories)) {
			if (_logger.isDebugEnabled()) {
				_logger.debug(
					"Registering {} queued outbound processor factories for " +
						"destination {}",
					queuedOutboundMessageProcessorFactories.size(),
					destination.getName());
			}

			for (OutboundMessageProcessorFactory
					outboundMessageProcessorFactory :
						queuedOutboundMessageProcessorFactories) {

				destination.register(outboundMessageProcessorFactory);
			}
		}
	}

	protected synchronized Destination removeDestination(
		String destinationName, boolean closeOnRemove) {

		Destination destination = _destinations.remove(destinationName);

		if (destination == null) {
			return null;
		}

		if (closeOnRemove) {
			destination.close(true);
		}

		destination.unregisterDestinationEventListeners();

		destination.unregisterInboundMessageProcessorFactories();

		destination.unregisterMessageListeners();

		destination.unregisterOutboundMessageProcessorFactories();

		for (MessageBusEventListener messageBusEventListener :
				_messageBusEventListeners) {

			messageBusEventListener.destinationRemoved(destination);
		}

		return destination;
	}

	protected void updateDestination(
		Destination destination,
		DestinationWorkerConfiguration destinationWorkerConfiguration) {

		if ((destination == null) || (destinationWorkerConfiguration == null)) {
			return;
		}

		if (destination instanceof BaseAsyncDestination) {
			BaseAsyncDestination baseAsyncDestination =
				(BaseAsyncDestination)destination;

			baseAsyncDestination.setMaximumQueueSize(
				destinationWorkerConfiguration.maxQueueSize());
			baseAsyncDestination.setWorkersCoreSize(
				destinationWorkerConfiguration.workerCoreSize());
			baseAsyncDestination.setWorkersMaxSize(
				destinationWorkerConfiguration.workerMaxSize());
		}
	}

	private List<OutboundMessageProcessor> _getOutboundMessageProcessors(
		Destination destination) {

		final Set<OutboundMessageProcessorFactory>
			outboundMessageProcessorFactories =
				destination.getOutboundMessageProcessorFactories();

		List<OutboundMessageProcessor> outboundMessageProcessors =
			new ArrayList<>();

		if (outboundMessageProcessorFactories == null) {
			return outboundMessageProcessors;
		}

		for (OutboundMessageProcessorFactory
				outboundMessageProcessorFactory :
					outboundMessageProcessorFactories) {

			outboundMessageProcessors.add(
				outboundMessageProcessorFactory.create());
		}

		return outboundMessageProcessors;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		DefaultMessageBus.class);

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private DestinationFactory _destinationFactory;

	private final Map<String, Destination> _destinations = new HashMap<>();
	private final Map<String, DestinationWorkerConfiguration>
		_destinationWorkerConfigurations = new ConcurrentHashMap<>();
	private final Map<String, String> _factoryPidsToDestinationName =
		new ConcurrentHashMap<>();
	private final Set<MessageBusEventListener> _messageBusEventListeners =
		ConcurrentHashMap.newKeySet();
	private final Map<String, List<DestinationEventListener>>
		_queuedDestinationEventListeners = new HashMap<>();
	private final Map<String, List<InboundMessageProcessorFactory>>
		_queuedInboundMessageProcessorFactories = new HashMap<>();
	private final Map<String, List<MessageListener>> _queuedMessageListeners =
		new HashMap<>();
	private final Map<String, List<OutboundMessageProcessorFactory>>
		_queuedOutboundMessageProcessorFactories = new HashMap<>();

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SingleDestinationMessageSenderFactory
		_singleDestinationMessageSenderFactory;

	private SynchronousMessageSender.Mode _synchronousMessageSenderMode;

}
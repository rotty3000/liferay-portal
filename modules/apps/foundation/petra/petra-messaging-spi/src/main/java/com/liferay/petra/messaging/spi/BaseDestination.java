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

package com.liferay.petra.messaging.spi;

import com.liferay.petra.messaging.api.DestinationEventListener;
import com.liferay.petra.messaging.api.DestinationStatistics;
import com.liferay.petra.messaging.api.InboundMessageProcessor;
import com.liferay.petra.messaging.api.InboundMessageProcessorFactory;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.api.OutboundMessageProcessor;
import com.liferay.petra.messaging.api.OutboundMessageProcessorFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.io.util.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public abstract class BaseDestination implements Destination {

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	public void addDestinationEventListener(
		DestinationEventListener destinationEventListener,
		Map<String, Object> properties) {

		destinationEventListeners.put(properties, destinationEventListener);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	public void addInboundMessageProcessorFactory(
		InboundMessageProcessorFactory inboundMessageProcessorFactory,
		Map<String, Object> properties) {

		inboundMessageProcessorFactories.put(
			properties, inboundMessageProcessorFactory);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	public void addMessageListener(
		MessageListener messageListener, Map<String, Object> properties) {

		ClassLoader operatingClassLoader = (ClassLoader)properties.get(
			"message.listener.operating.class.loader");

		if (operatingClassLoader == null) {
			Class<?> clazz = messageListener.getClass();

			operatingClassLoader = clazz.getClassLoader();
		}

		messageListeners.put(
			properties,
			new InvokerMessageListener(messageListener, operatingClassLoader));

		fireMessageListenerRegisteredEvent(messageListener);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	public void addOutboundMessageProcessorFactory(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory,
		Map<String, Object> properties) {

		outboundMessageProcessorFactories.put(
			properties, outboundMessageProcessorFactory);
	}

	public void afterPropertiesSet() {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException("Name is null");
		}
	}

	@Override
	public void close() {
		close(false);
	}

	@Override
	public void close(boolean force) {
	}

	@Override
	public int getDestinationEventListenerCount() {
		return destinationEventListeners.size();
	}

	@Override
	public Collection<DestinationEventListener> getDestinationEventListeners() {
		return Collections.unmodifiableCollection(
			destinationEventListeners.values());
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<InboundMessageProcessorFactory>
		getInboundMessageProcessorFactories() {

		return Collections.unmodifiableCollection(
			inboundMessageProcessorFactories.values());
	}

	@Override
	public int getInboundMessageProcessorFactoryCount() {
		return inboundMessageProcessorFactories.size();
	}

	public List<InboundMessageProcessor> getInboundMessageProcessors() {
		List<InboundMessageProcessor> processors = new ArrayList<>();

		for (InboundMessageProcessorFactory factory :
				getInboundMessageProcessorFactories()) {

			processors.add(factory.create());
		}

		return Collections.unmodifiableList(processors);
	}

	@Override
	public int getMessageListenerCount() {
		return messageListeners.size();
	}

	@Override
	public Collection<MessageListener> getMessageListeners() {
		return Collections.unmodifiableCollection(messageListeners.values());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<OutboundMessageProcessorFactory>
		getOutboundMessageProcessorFactories() {

		return Collections.unmodifiableCollection(
			outboundMessageProcessorFactories.values());
	}

	@Override
	public int getOutboundMessageProcessorFactoryCount() {
		return outboundMessageProcessorFactories.size();
	}

	public Collection<OutboundMessageProcessor> getOutboundMessageProcessors() {
		List<OutboundMessageProcessor> processors = new ArrayList<>();

		for (OutboundMessageProcessorFactory factory :
				getOutboundMessageProcessorFactories()) {

			processors.add(factory.create());
		}

		return Collections.unmodifiableList(processors);
	}

	@Override
	public boolean isRegistered() {
		if (getMessageListenerCount() > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void open() {
	}

	public void removeDestinationEventListener(
		DestinationEventListener destinationEventListener,
		Map<String, Object> properties) {

		destinationEventListeners.remove(properties, destinationEventListener);
	}

	public void removeInboundMessageProcessorFactory(
		InboundMessageProcessorFactory inboundMessageProcessorFactory,
		Map<String, Object> properties) {

		inboundMessageProcessorFactories.remove(
			properties, inboundMessageProcessorFactory);
	}

	public void removeMessageListener(
		MessageListener messageListener, Map<String, Object> properties) {

		messageListeners.remove(properties);

		fireMessageListenerUnregisteredEvent(messageListener);
	}

	public void removeOutboundMessageProcessorFactory(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory,
		Map<String, Object> properties) {

		outboundMessageProcessorFactories.remove(
			properties, outboundMessageProcessorFactory);
	}

	@Override
	public void send(Message message) {
		throw new UnsupportedOperationException();
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void fireMessageListenerRegisteredEvent(
		MessageListener messageListener) {

		for (DestinationEventListener listener :
				destinationEventListeners.values()) {

			listener.messageListenerRegistered(getName(), messageListener);
		}
	}

	protected void fireMessageListenerUnregisteredEvent(
		MessageListener messageListener) {

		for (DestinationEventListener listener :
				destinationEventListeners.values()) {

			listener.messageListenerUnregistered(getName(), messageListener);
		}
	}

	protected final Map<Map<String, Object>, DestinationEventListener>
		destinationEventListeners = new ConcurrentSkipListMap<>(
			ServiceMaps.comparator().reversed());
	protected final Map<Map<String, Object>, InboundMessageProcessorFactory>
		inboundMessageProcessorFactories = new ConcurrentSkipListMap<>(
			ServiceMaps.comparator().reversed());
	protected final Map<Map<String, Object>, MessageListener> messageListeners =
		new ConcurrentSkipListMap<>(ServiceMaps.comparator().reversed());
	protected String name = StringPool.BLANK;
	protected final Map<Map<String, Object>, OutboundMessageProcessorFactory>
		outboundMessageProcessorFactories = new ConcurrentSkipListMap<>(
			ServiceMaps.comparator().reversed());

}
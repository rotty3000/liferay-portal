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

package com.liferay.messaging;

import com.liferay.petra.io.StringPool;
import com.liferay.petra.io.validate.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public abstract class BaseDestination implements Destination {

	public boolean addDestinationEventListener(
		DestinationEventListener destinationEventListener) {

		return destinationEventListeners.add(destinationEventListener);
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
	public void copyDestinationEventListeners(Destination destination) {
		for (DestinationEventListener destinationEventListener :
				destinationEventListeners) {

			destination.register(destinationEventListener);
		}
	}

	@Override
	public void copyInboundMessageProcessorFactories(Destination destination) {
		for (InboundMessageProcessorFactory inboundMessageProcessorFactory :
				inboundMessageProcessorFactories) {

			destination.register(inboundMessageProcessorFactory);
		}
	}

	@Override
	public void copyMessageListeners(Destination destination) {
		for (MessageListener messageListener : messageListeners) {
			InvokerMessageListener invokerMessageListener =
				(InvokerMessageListener)messageListener;

			destination.register(
				invokerMessageListener.getMessageListener(),
				invokerMessageListener.getClassLoader());
		}
	}

	@Override
	public void copyOutboundMessageProcessorFactories(Destination destination) {
		for (OutboundMessageProcessorFactory outboundMessageProcessorFactory :
				outboundMessageProcessorFactories) {

			destination.register(outboundMessageProcessorFactory);
		}
	}

	@Override
	public void destroy() {
		close(true);

		removeDestinationEventListeners();

		unregisterMessageListeners();
	}

	@Override
	public int getDestinationEventListenerCount() {
		return destinationEventListeners.size();
	}

	@Override
	public Set<DestinationEventListener> getDestinationEventListeners() {
		return Collections.unmodifiableSet(destinationEventListeners);
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<InboundMessageProcessorFactory>
		getInboundMessageProcessorFactories() {

		return Collections.unmodifiableSet(inboundMessageProcessorFactories);
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
	public Set<MessageListener> getMessageListeners() {
		return Collections.unmodifiableSet(messageListeners);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<OutboundMessageProcessorFactory>
		getOutboundMessageProcessorFactories() {

		return Collections.unmodifiableSet(outboundMessageProcessorFactories);
	}

	@Override
	public int getOutboundMessageProcessorFactoryCount() {
		return outboundMessageProcessorFactories.size();
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

	@Override
	public boolean register(DestinationEventListener destinationEventListener) {
		return addDestinationEventListener(destinationEventListener);
	}

	@Override
	public boolean register(
		InboundMessageProcessorFactory inboundMessageProcessorFactory) {

		return inboundMessageProcessorFactories.add(
			inboundMessageProcessorFactory);
	}

	@Override
	public boolean register(MessageListener messageListener) {
		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener);

		return registerMessageListener(invokerMessageListener);
	}

	@Override
	public boolean register(
		MessageListener messageListener, ClassLoader classloader) {

		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener, classloader);

		return registerMessageListener(invokerMessageListener);
	}

	@Override
	public boolean register(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory) {

		return outboundMessageProcessorFactories.add(
			outboundMessageProcessorFactory);
	}

	public boolean removeDestinationEventListener(
		DestinationEventListener destinationEventListener) {

		return destinationEventListeners.remove(destinationEventListener);
	}

	public void removeDestinationEventListeners() {
		destinationEventListeners.clear();
	}

	@Override
	public void send(Message message) {
		throw new UnsupportedOperationException();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean unregister(
		DestinationEventListener destinationEventListener) {

		return removeDestinationEventListener(destinationEventListener);
	}

	@Override
	public boolean unregister(
		InboundMessageProcessorFactory inboundMessageProcessorFactory) {

		return inboundMessageProcessorFactories.remove(
			inboundMessageProcessorFactory);
	}

	@Override
	public boolean unregister(MessageListener messageListener) {
		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener);

		return unregisterMessageListener(invokerMessageListener);
	}

	public boolean unregister(
		MessageListener messageListener, ClassLoader classloader) {

		InvokerMessageListener invokerMessageListener =
			new InvokerMessageListener(messageListener, classloader);

		return unregisterMessageListener(invokerMessageListener);
	}

	@Override
	public boolean unregister(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory) {

		return outboundMessageProcessorFactories.remove(
			outboundMessageProcessorFactory);
	}

	@Override
	public void unregisterDestinationEventListeners() {
		removeDestinationEventListeners();
	}

	@Override
	public void unregisterInboundMessageProcessorFactories() {
		inboundMessageProcessorFactories.clear();
	}

	@Override
	public void unregisterMessageListeners() {
		for (MessageListener messageListener : messageListeners) {
			unregisterMessageListener((InvokerMessageListener)messageListener);
		}
	}

	@Override
	public void unregisterOutboundMessageProcessorFactories() {
		outboundMessageProcessorFactories.clear();
	}

	protected void fireMessageListenerRegisteredEvent(
		MessageListener messageListener) {

		for (DestinationEventListener destinationEventListener :
				destinationEventListeners) {

			destinationEventListener.messageListenerRegistered(
				getName(), messageListener);
		}
	}

	protected void fireMessageListenerUnregisteredEvent(
		MessageListener messageListener) {

		for (DestinationEventListener listener : destinationEventListeners) {
			listener.messageListenerUnregistered(getName(), messageListener);
		}
	}

	protected boolean registerMessageListener(
		InvokerMessageListener invokerMessageListener) {

		boolean registered = messageListeners.add(invokerMessageListener);

		if (registered) {
			fireMessageListenerRegisteredEvent(
				invokerMessageListener.getMessageListener());
		}

		return registered;
	}

	protected boolean unregisterMessageListener(
		InvokerMessageListener invokerMessageListener) {

		boolean unregistered = messageListeners.remove(invokerMessageListener);

		if (unregistered) {
			fireMessageListenerUnregisteredEvent(
				invokerMessageListener.getMessageListener());
		}

		return unregistered;
	}

	protected final Set<DestinationEventListener> destinationEventListeners =
		ConcurrentHashMap.newKeySet();
	protected final Set<InboundMessageProcessorFactory>
		inboundMessageProcessorFactories = ConcurrentHashMap.newKeySet();
	protected final Set<MessageListener> messageListeners =
		ConcurrentHashMap.newKeySet();
	protected String name = StringPool.BLANK;
	protected final Set<OutboundMessageProcessorFactory>
		outboundMessageProcessorFactories = ConcurrentHashMap.newKeySet();

}
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

import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class DestinationWrapper implements Destination {

	public DestinationWrapper(Destination destination) {
		this.destination = destination;
	}

	@Override
	public void close() {
		destination.close();
	}

	@Override
	public void close(boolean force) {
		destination.close(force);
	}

	@Override
	public void copyDestinationEventListeners(Destination destination) {
		this.destination.copyDestinationEventListeners(destination);
	}

	@Override
	public void copyInboundMessageProcessorFactories(Destination destination) {
		this.destination.copyInboundMessageProcessorFactories(destination);
	}

	@Override
	public void copyMessageListeners(Destination destination) {
		this.destination.copyMessageListeners(destination);
	}

	@Override
	public void copyOutboundMessageProcessorFactories(Destination destination) {
		this.destination.copyOutboundMessageProcessorFactories(destination);
	}

	@Override
	public void destroy() {
		this.destination.destroy();
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		return destination.getDestinationStatistics();
	}

	@Override
	public int getDestinationEventListenerCount() {
		return destination.getDestinationEventListenerCount();
	}

	@Override
	public Set<DestinationEventListener> getDestinationEventListeners() {
		return destination.getDestinationEventListeners();
	}

	@Override
	public Set<InboundMessageProcessorFactory>
		getInboundMessageProcessorFactories() {

		return destination.getInboundMessageProcessorFactories();
	}

	@Override
	public int getInboundMessageProcessorFactoryCount() {
		return destination.getDestinationEventListenerCount();
	}

	@Override
	public int getMessageListenerCount() {
		return destination.getMessageListenerCount();
	}

	@Override
	public Set<MessageListener> getMessageListeners() {
		return destination.getMessageListeners();
	}

	@Override
	public String getName() {
		return destination.getName();
	}

	@Override
	public Set<OutboundMessageProcessorFactory>
		getOutboundMessageProcessorFactories() {

		return destination.getOutboundMessageProcessorFactories();
	}

	@Override
	public int getOutboundMessageProcessorFactoryCount() {
		return destination.getOutboundMessageProcessorFactoryCount();
	}

	@Override
	public boolean isRegistered() {
		return destination.isRegistered();
	}

	@Override
	public void open() {
		destination.open();
	}

	@Override
	public boolean register(
		DestinationEventListener destinationEventListener) {

		return destination.register(destinationEventListener);
	}

	@Override
	public boolean register(
		InboundMessageProcessorFactory inboundMessageProcessorFactory) {

		return destination.register(inboundMessageProcessorFactory);
	}

	@Override
	public boolean register(MessageListener messageListener) {
		return destination.register(messageListener);
	}

	@Override
	public boolean register(
		MessageListener messageListener, ClassLoader classloader) {

		return destination.register(messageListener, classloader);
	}

	@Override
	public boolean register(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory) {

		return destination.register(outboundMessageProcessorFactory);
	}

	@Override
	public void send(Message message) {
		destination.send(message);
	}

	@Override
	public boolean unregister(
		DestinationEventListener destinationEventListener) {

		return destination.unregister(destinationEventListener);
	}

	@Override
	public boolean unregister(
		InboundMessageProcessorFactory inboundMessageProcessorFactory) {

		return destination.unregister(inboundMessageProcessorFactory);
	}

	@Override
	public boolean unregister(MessageListener messageListener) {
		return destination.unregister(messageListener);
	}

	@Override
	public boolean unregister(
		OutboundMessageProcessorFactory outboundMessageProcessorFactory) {

		return destination.unregister(outboundMessageProcessorFactory);
	}

	@Override
	public void unregisterDestinationEventListeners() {
		destination.unregisterDestinationEventListeners();
	}

	@Override
	public void unregisterInboundMessageProcessorFactories() {
		destination.unregisterInboundMessageProcessorFactories();
	}

	@Override
	public void unregisterMessageListeners() {
		destination.unregisterMessageListeners();
	}

	@Override
	public void unregisterOutboundMessageProcessorFactories() {
		destination.unregisterOutboundMessageProcessorFactories();
	}

	protected Destination destination;

}
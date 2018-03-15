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

package com.liferay.portal.kernel.messaging;

import com.liferay.petra.messaging.api.InboundMessageProcessorFactory;
import com.liferay.petra.messaging.api.OutboundMessageProcessorFactory;

import java.util.Collection;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class DestinationWrapper implements Destination {

	public DestinationWrapper(Destination destination) {
		this.destination = destination;
	}

	@Override
	public boolean addDestinationEventListener(
		DestinationEventListener destinationEventListener) {

		return destination.addDestinationEventListener(
			destinationEventListener);
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
	public void copyMessageListeners(Destination destination) {
		this.destination.copyMessageListeners(destination);
	}

	@Override
	public void destroy() {
		this.destination.destroy();
	}

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#getDestinationEventListenerCount()
	 */
	@Override
	public int getDestinationEventListenerCount() {

		// TODO Auto-generated method stub

		return 0;
	}

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#getDestinationEventListeners()
	 */
	@Override
	public Collection<com.liferay.petra.messaging.api.DestinationEventListener>
	getDestinationEventListeners() {

		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		return destination.getDestinationStatistics();
	}

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#getInboundMessageProcessorFactories()
	 */
	@Override
	public Collection<InboundMessageProcessorFactory>
	getInboundMessageProcessorFactories() {

		// TODO Auto-generated method stub

		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#getInboundMessageProcessorFactoryCount()
	 */
	@Override
	public int getInboundMessageProcessorFactoryCount() {

		// TODO Auto-generated method stub

		return 0;
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

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#getOutboundMessageProcessorFactories()
	 */
	@Override
	public Collection<OutboundMessageProcessorFactory>
	getOutboundMessageProcessorFactories() {

		// TODO Auto-generated method stub

		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#getOutboundMessageProcessorFactoryCount()
	 */
	@Override
	public int getOutboundMessageProcessorFactoryCount() {

		// TODO Auto-generated method stub

		return 0;
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
	public boolean register(MessageListener messageListener) {
		return destination.register(messageListener);
	}

	@Override
	public boolean register(
		MessageListener messageListener, ClassLoader classloader) {

		return destination.register(messageListener, classloader);
	}

	@Override
	public boolean removeDestinationEventListener(
		DestinationEventListener destinationEventListener) {

		return destination.removeDestinationEventListener(
			destinationEventListener);
	}

	@Override
	public void removeDestinationEventListeners() {
		destination.removeDestinationEventListeners();
	}

	/* (non-Javadoc)
	 * @see com.liferay.petra.messaging.spi.Destination#send(com.liferay.petra.messaging.api.Message)
	 */
	@Override
	public void send(com.liferay.petra.messaging.api.Message message) {

		// TODO Auto-generated method stub

	}

	@Override
	public void send(Message message) {
		destination.send(message);
	}

	@Override
	public boolean unregister(MessageListener messageListener) {
		return destination.unregister(messageListener);
	}

	@Override
	public void unregisterMessageListeners() {
		destination.unregisterMessageListeners();
	}

	protected Destination destination;

}
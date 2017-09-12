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

package com.liferay.messaging.spi;

import com.liferay.messaging.DestinationEventListener;
import com.liferay.messaging.DestinationStatistics;
import com.liferay.messaging.InboundMessageProcessorFactory;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.OutboundMessageProcessorFactory;

import java.util.Collection;

/**
 * @author Michael C. Han
 */
public interface Destination extends com.liferay.messaging.Destination {

	public void close();

	public void close(boolean force);

	public int getDestinationEventListenerCount();

	public Collection<DestinationEventListener> getDestinationEventListeners();

	public DestinationStatistics getDestinationStatistics();

	public Collection<InboundMessageProcessorFactory>
		getInboundMessageProcessorFactories();

	public int getInboundMessageProcessorFactoryCount();

	public int getMessageListenerCount();

	public Collection<MessageListener> getMessageListeners();

	public String getName();

	public Collection<OutboundMessageProcessorFactory>
		getOutboundMessageProcessorFactories();

	public int getOutboundMessageProcessorFactoryCount();

	public boolean isRegistered();

	public void open();

	public void send(Message message);

}
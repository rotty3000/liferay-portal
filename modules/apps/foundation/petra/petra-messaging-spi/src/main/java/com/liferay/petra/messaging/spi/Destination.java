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
import com.liferay.petra.messaging.api.InboundMessageProcessorFactory;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.api.OutboundMessageProcessorFactory;

import java.util.Collection;

/**
 * @author Michael C. Han
 */
public interface Destination extends com.liferay.petra.messaging.api.Destination {

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
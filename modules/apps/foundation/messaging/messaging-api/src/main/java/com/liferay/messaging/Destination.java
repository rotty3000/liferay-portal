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
 * @author Michael C. Han
 */
public interface Destination {

	public int getDestinationEventListenerCount();

	public Set<DestinationEventListener> getDestinationEventListeners();

	public DestinationStatistics getDestinationStatistics();

	public Set<InboundMessageProcessorFactory>
		getInboundMessageProcessorFactories();

	public int getInboundMessageProcessorFactoryCount();

	public int getMessageListenerCount();

	public Set<MessageListener> getMessageListeners();

	public String getName();

	public Set<OutboundMessageProcessorFactory>
		getOutboundMessageProcessorFactories();

	public int getOutboundMessageProcessorFactoryCount();

	public boolean isRegistered();

}
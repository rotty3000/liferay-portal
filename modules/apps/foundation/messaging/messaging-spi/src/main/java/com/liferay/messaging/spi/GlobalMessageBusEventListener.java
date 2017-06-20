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

import com.liferay.messaging.MessageBusEventListener;
import com.liferay.messaging.MessageListener;
import com.liferay.petra.io.SetUtil;

import java.util.List;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public class GlobalMessageBusEventListener implements MessageBusEventListener {

	@Override
	public void destinationAdded(com.liferay.messaging.Destination destination) {
		if (!_ignoredDestinations.contains(destination.getName()) &&
			(destination instanceof Destination)) {

			Destination spiDestination = (Destination)destination;

			spiDestination.register(_messageListener);
		}
	}

	@Override
	public void destinationRemoved(com.liferay.messaging.Destination destination) {
		if (!_ignoredDestinations.contains(destination.getName()) &&
			(destination instanceof Destination)) {

			Destination spiDestination = (Destination)destination;

			spiDestination.unregister(_messageListener);
		}
	}

	public void setIgnoredDestinations(List<String> ignoredDestinations) {
		_ignoredDestinations = SetUtil.fromCollection(ignoredDestinations);
	}

	public void setMessageListener(MessageListener messageListener) {
		_messageListener = messageListener;
	}

	private Set<String> _ignoredDestinations;
	private MessageListener _messageListener;

}
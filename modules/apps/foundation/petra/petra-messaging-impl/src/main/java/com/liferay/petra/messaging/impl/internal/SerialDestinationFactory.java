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

import com.liferay.petra.messaging.api.DestinationSettings;
import com.liferay.petra.messaging.spi.SerialDestination;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * <p>
 * Destination that delivers a message to a list of message listeners one at a
 * time.
 * </p>
 *
 * @author Raymond Augé
 */
@Component(
	factory = "serial.destination",
	service = SerialDestination.class
)
public class SerialDestinationFactory extends SerialDestination {

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setMaximumQueueSize(destinationSettings.maxQueueSize());
		setName(destinationSettings.destination_name());
		afterPropertiesSet();
		open();
	}

	@Deactivate
	protected void deactivate() {
		close();
	}

}
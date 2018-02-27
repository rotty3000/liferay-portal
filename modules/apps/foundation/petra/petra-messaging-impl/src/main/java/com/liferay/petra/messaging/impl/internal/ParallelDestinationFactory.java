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
import com.liferay.petra.messaging.spi.ParallelDestination;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * <p>
 * Destination that delivers a message to a list of message listeners in
 * parallel.
 * </p>
 *
 * @author Raymond Augé
 */
@Component(
	factory = "parallel.destination",
	service = ParallelDestination.class
)
public class ParallelDestinationFactory extends ParallelDestination {

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setMaximumQueueSize(destinationSettings.maxQueueSize());
		setName(destinationSettings.destination_name());
		setWorkersCoreSize(destinationSettings.workerCoreSize());
		setWorkersMaxSize(destinationSettings.workerMaxSize());
		afterPropertiesSet();
		open();
	}

	@Deactivate
	protected void deactivate() {
		close();
	}

}
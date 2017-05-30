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

package com.liferay.messaging.impl.internal;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationPrototype;
import com.liferay.messaging.SynchronousDestination;

/**
 * @author Michael C. Han
 */
public class SynchronousDestinationPrototype implements DestinationPrototype {

	@Override
	public Destination createDestination(
		DestinationConfiguration destinationConfiguration) {

		SynchronousDestination synchronousDestination =
			new SynchronousDestination();

		synchronousDestination.setName(
			destinationConfiguration.getDestinationName());

		synchronousDestination.afterPropertiesSet();

		return synchronousDestination;
	}

}
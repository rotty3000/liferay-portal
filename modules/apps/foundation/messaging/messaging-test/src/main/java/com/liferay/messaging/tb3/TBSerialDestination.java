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

package com.liferay.messaging.tb3;

import com.liferay.messaging.Destination;
import com.liferay.messaging.SerialDestination;
import com.liferay.messaging.interfaces.Config;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Raymond Augé
 */
@Component(
	property = {"destination.name=serial/test"}, scope = ServiceScope.SINGLETON,
	service = Destination.class
)
public class TBSerialDestination extends SerialDestination {

	@Activate
	protected void activate(Config config) {
		setName(config.destination_name());
	}

}
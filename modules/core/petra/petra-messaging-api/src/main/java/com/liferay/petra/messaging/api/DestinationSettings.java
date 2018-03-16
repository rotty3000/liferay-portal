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

package com.liferay.petra.messaging.api;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Raymond Augé
 */
@ObjectClassDefinition(
	id = "com.liferay.messaging.Destination", localization = "content/Language",
	name = "destination-configuration-name"
)
public @interface DestinationSettings {

	@AttributeDefinition(required = true)
	String destination_name();

	@AttributeDefinition(description = "max-queue-size-help", required = false)
	int maxQueueSize() default Integer.MAX_VALUE;

	@AttributeDefinition(
		description = "worker-core-size-help", required = false
	)
	int workerCoreSize() default 2;

	@AttributeDefinition(description = "worker-max-size-help", required = false)
	int workerMaxSize() default 5;

}
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

package com.liferay.messaging.impl.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Michael C. Han
 */
@ObjectClassDefinition(
	factoryPid = "com.liferay.portal.messaging.configuration.DestinationWorkerConfiguration",
	id = "com.liferay.portal.messaging.configuration.DestinationWorkerConfiguration",
	localization = "content/Language",
	name = "destination-workfer-configuration-name"
)
public @interface DestinationWorkerConfiguration {

	@AttributeDefinition(required = true)
	public String destinationName() default "";

	@AttributeDefinition(
		description = "max-queue-size-help", required = false
	)
	public int maxQueueSize() default -1;

	@AttributeDefinition(
		description = "worker-core-size-help", required = false
	)
	public int workerCoreSize() default 2;

	@AttributeDefinition(
		description = "worker-max-size-help", required = false
	)
	public int workerMaxSize() default 5;

}
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

import com.liferay.messaging.sender.SynchronousMessageSender;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Raymond Augé
 */
@ObjectClassDefinition(
	description = "default-message-bus-configuration-description",
	localization = "content/Language",
	name = "default-message-bus-configuration-name",
	pid = "com.liferay.messaging.impl.internal.DefaultMessageBus"
)
public @interface MessageBusConfiguration {

	@AttributeDefinition(description = "synchronous-message-sender-mode")
	SynchronousMessageSender.Mode synchronousMessageSenderMode()
		default SynchronousMessageSender.Mode.DEFAULT;

	@AttributeDefinition(description = "synchronous-message-sender-timeout")
	long timeout() default 10000;

}
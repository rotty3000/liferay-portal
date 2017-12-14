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

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.api.MessageBuilderFactory;
import com.liferay.petra.messaging.api.MessageBus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Raymond Augé
 */
@Component
public class DefaultMessageBuilderFactory implements MessageBuilderFactory {

	@Override
	public MessageBuilder create(String destinationName) {
		return new DefaultMessageBuilder(_messageBus, destinationName);
	}

	@Override
	public MessageBuilder createResponse(Message message) {
		return new ResponseMessageBuilder(_messageBus, message);
	}

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private MessageBus _messageBus;

}
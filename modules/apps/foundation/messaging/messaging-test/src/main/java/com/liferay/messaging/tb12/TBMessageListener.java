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

package com.liferay.messaging.tb12;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBuilder;
import com.liferay.messaging.MessageBuilderFactory;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Raymond Augé
 */
@Component(
	property = {
		"destination.name=builder/tb12"
	},
	scope = ServiceScope.SINGLETON,
	service = {Callable.class, MessageListener.class}
)
public class TBMessageListener implements Callable<Message>, MessageListener{

	@Override
	public void receive(Message message) throws MessageListenerException {
		_message = message;

		MessageBuilder builder = _messageBuilderFactory.createResponse(message);

		builder.setPayload(_message);

		builder.send();

		_latch.countDown();
	}

	@Override
	public Message call() throws Exception {
		_latch.await();

		return _message;
	}

	private final CountDownLatch _latch = new CountDownLatch(1);
	private volatile Message _message;
	@Reference
	private MessageBuilderFactory _messageBuilderFactory;

}
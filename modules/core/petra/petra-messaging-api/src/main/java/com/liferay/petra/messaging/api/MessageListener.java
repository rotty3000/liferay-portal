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

/**
 * Specifies the processing to take place when a message is received.
 *
 * <p>
 * Message listeners are registered with destinations. When a message is sent to
 * a destination, each of the destination's registered message listeners
 * receives the message.
 * </p>
 *
 * @author Michael C. Han
 */
public interface MessageListener {

	/**
	 * Specifies the processing to take place when a message is received.
	 *
	 * @param message the received message
	 * @throws MessageListenerException if the message could not be processed
	 */
	public void receive(Message message) throws MessageListenerException;

}
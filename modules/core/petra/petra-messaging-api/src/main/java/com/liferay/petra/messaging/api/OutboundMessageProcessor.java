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
 * <p>
 * Processes out-bound messages before and after being sent. Since each
 * execution creates a new processor instance, processor instances are thread
 * safe. Both before and after stages are guaranteed to be executed so it's
 * possible to implement "around" behavior.
 * </p>
 * <p>
 * In the case where the Message Bus was used to send a message over a remote
 * connection this processor will execute on the local side before sending.
 * </p>
 *
 * @author Raymond Augé
 */
public interface OutboundMessageProcessor {

	/**
	 * Process an out-bound message after passing it on for delivery.
	 *
	 * @param message the message which was delivered
	 */
	public void afterSend(Message message) throws MessageProcessorException;

	/**
	 * Process an out-bound message before passing it on for delivery. The
	 * message may be altered or replaced.
	 *
	 * @param  message the message being sent
	 * @return message the message to deliver
	 */
	public Message beforeSend(Message message) throws MessageProcessorException;

}
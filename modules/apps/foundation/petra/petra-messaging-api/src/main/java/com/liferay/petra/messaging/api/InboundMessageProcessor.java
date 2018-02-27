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
 * Processes in-bound messages before and after being received. Since each
 * execution creates a new processor instance, processor instances are thread
 * safe. Both before and after stages are guaranteed to be executed so it's
 * possible to implement "around" behavior.
 * </p>
 * <p>
 * In the case where the Message Bus was used to send a message over a remote
 * connection this processor will execute on the side having the thread pool.
 * </p>
 *
 * @author Raymond Augé
 */
public interface InboundMessageProcessor {

	/**
	 * Process an in-bound message after passing it to listeners.
	 *
	 * @param message the message which was received
	 */
	public void afterReceive(Message message) throws MessageProcessorException;

	/**
	 * Process an out-bound message after passing it on async delivery thread.
	 *
	 * @param message the message which was delivered
	 */
	public void afterThread(Message message, Thread dispatchThread)
		throws MessageProcessorException;

	/**
	 * Process an in-bound message before passing it to listeners. The message
	 * may be altered or replaced.
	 *
	 * @param  message the message being received
	 * @return message the message to deliver
	 */
	public Message beforeReceive(Message message)
		throws MessageProcessorException;

	/**
	 * Process an out-bound message before passing it on async delivery thread.
	 * The message may be altered or replaced.
	 *
	 * @param  message the message being sent
	 * @return message the message to deliver
	 */
	public Message beforeThread(Message message, Thread dispatchThread)
		throws MessageProcessorException;

}
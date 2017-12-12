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
 * Interface providing a factory for MessageBuilder instances.
 * </p>
 *
 * @author Raymond Augé
 */
public interface MessageBuilderFactory {

	/**
	 * Returns a new message builder using the provided destination name.
	 *
	 * @param destinationName the name of the destination with which the new
	 * message builder is configured
	 * @return a new message builder configured with the provided destination
	 * name
	 */
	public MessageBuilder create(String destinationName);

	/**
	 * Returns a new message builder using the response destination name of the
	 * provided message.
	 *
	 * @param message the message whose response destination name is used to
	 * configure the new message builder.
	 * @return a new message builder configured with the destination name of the
	 * provided message
	 */
	public MessageBuilder createResponse(Message message);

}
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
 * Specifies processing to take place when message listeners are added to or
 * removed from a destination.
 *
 * @author Michael C. Han
 */
public interface DestinationEventListener {

	/**
	 * Specifies processing to take place when a message listener is added to a
	 * destination
	 *
	 * @param destinationName the name of the destination to which a message
	 * listener was added
	 * @param messageListener the message listener that was added to the
	 * destination
	 */
	public void messageListenerRegistered(
		String destinationName, MessageListener messageListener);

	/**
	 * Specifies processing to take place when a message listener is removed
	 * from a destination
	 *
	 * @param destinationName the name of the destination from which a message
	 * listener was removed
	 * @param messageListener the message listener that was removed from the
	 * destination
	 */
	public void messageListenerUnregistered(
		String destinationName, MessageListener messageListener);

}
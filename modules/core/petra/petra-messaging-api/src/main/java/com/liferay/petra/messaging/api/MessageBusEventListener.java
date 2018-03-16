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
 * Specifies processing to take place when destinations are added to or removed
 * from the message bus.
 *
 * @author Shuyang Zhou
 */
public interface MessageBusEventListener {

	/**
	 * Performs some processing when a destination is added to the message bus.
	 *
	 * @param destination the destination that was added
	 */
	public void destinationAdded(Destination destination);

	/**
	 * Performs some processing when a destination is removed from the message bus.
	 *
	 * @param destination the destination that was removed
	 */
	public void destinationRemoved(Destination destination);

}
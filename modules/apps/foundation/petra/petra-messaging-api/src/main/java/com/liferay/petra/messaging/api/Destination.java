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
 * Represents a messaging endpoint.
 *
 * <p>
 * Messages are sent to a destination. One or more message listeners can be
 * registered with a destination. When a message is sent to a destination, all
 * of the destination's registered listeners receive the message.
 * </p>
 *
 * @author Michael C. Han
 */
public interface Destination {

	/**
	 * Returns the destination's destination statistics.
	 *
	 * @return the destination's destination statistics
	 */
	public DestinationStatistics getDestinationStatistics();

	/**
	 * Returns the name of the destination.
	 *
	 * @return the name of the destination
	 */
	public String getName();

	/**
	 * Returns <code>true</code> if the destination has at least one message
	 * listener.
	 *
	 * @return <code>true</code> if the destination has at least one message
	 *         listener; <code>false</code> otherwise
	 */
	public boolean isRegistered();

}
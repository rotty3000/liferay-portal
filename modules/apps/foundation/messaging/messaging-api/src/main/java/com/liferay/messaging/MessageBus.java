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

package com.liferay.messaging;

import java.util.Collection;

/**
 * @author Michael C. Han
 */
public interface MessageBus {

	public Destination getDestination(String destinationName);

	public int getDestinationCount();

	public Collection<String> getDestinationNames();

	public Collection<Destination> getDestinations();

	public boolean hasDestination(String destinationName);

	public boolean hasMessageListener(String destinationName);

	public void sendMessage(String destinationName, Message message);

	public void sendMessage(String destinationName, Object payload);

	public Object sendSynchronousMessage(
		String destinationName, Message message);

	public Object sendSynchronousMessage(
		String destinationName, Message message, long timeout);

	public Object sendSynchronousMessage(
		String destinationName, Object payload);

	public Object sendSynchronousMessage(
		String destinationName, Object payload, long timeout);

	public Object sendSynchronousMessage(
		String destinationName, Object payload, String responseDestinationName);

	public Object sendSynchronousMessage(
		String destinationName, Object payload, String responseDestinationName,
		long timeout);

}
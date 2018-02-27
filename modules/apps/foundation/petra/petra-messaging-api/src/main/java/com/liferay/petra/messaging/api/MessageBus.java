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

import java.util.Collection;

/**
 * Provides an API for retrieving destinations and destination information and
 * an API for sending messages.
 *
 * @author Michael C. Han
 */
public interface MessageBus {

	/**
	 * Returns the destination with the provided destination name
	 *
	 * @param destinationName the name of the destination to return
	 * @return the destination with the provided destination name or
	 * <code>null</code> if no destination with the provided name could be found
	 */
	public Destination getDestination(String destinationName);

	/**
	 * Returns the number of destinations registered with the message bus.
	 *
	 * @return the number of destinations registered with the message bus
	 */
	public int getDestinationCount();

	/**
	 * Returns a collection of the names of all the destinations registered with
	 * the message bus.
	 *
	 * @return a collection of the names of all the destinations registered with
	 * the message bus
	 */
	public Collection<String> getDestinationNames();

	/**
	 * Returns a collection of all the destinations registered with the message
	 * bus.
	 *
	 * @return a collection of all the destinations registered with the message
	 * bus
	 */
	public Collection<Destination> getDestinations();

	/**
	 * Returns <code>true</code> if a destination with the specified name is
	 * registered with the message bus; returns <code>false</code> otherwise.
	 *
	 * @param destinationName the destination name for which to check
	 * @return <code>true</code> if a destination with the specified
	 * name is registered with the message bus; <code>false</code> otherwise
	 */
	public boolean hasDestination(String destinationName);

	/**
	 * Returns <code>true</code> if a destination with the specified name is
	 * registered with the message bus and has at least one message listener;
	 * returns <code>false</code> otherwise.
	 *
	 * @param destinationName the destination name for which to check
	 * @return <code>true</code> if a destination with the specified name is
	 * registered with the message bus and has at least one message listener;
	 * returns <code>false</code> otherwise
	 */
	public boolean hasMessageListener(String destinationName);

	/**
	 * Sends the specified message to the specified destination.
	 *
	 * @param destinationName the name of the destination to which to send the message
	 * @param message the message to send
	 */
	public void sendMessage(String destinationName, Message message);

	/**
	 * Sends the specified message payload to the specified destination in a new
	 * message.
	 *
	 * @param destinationName the name of the destination to which to send the message
	 * @param payload the payload to send
	 */
	public void sendMessage(String destinationName, Object payload);

	/**
	 * Sends the specified message to the specified destination and returns a
	 * result.
	 *
	 * @param destinationName the name of the destination to which to send the message
	 * @param message the message to send
	 * @return the result or <code>null</code> if no result was received
	 * within a default amount of time
	 */
	public Object sendSynchronousMessage(
		String destinationName, Message message);

	/**
	 * Sends the specified message to the specified destination and returns a
	 * result.
	 *
	 * @param destinationName the name of the destination to which to send the message
	 * @param message the message to send
	 * @param timeout how long to wait for a response, in milliseconds
	 * @return the result or <code>null</code> if no result was received
	 * within the specified amount of time
	 */
	public Object sendSynchronousMessage(
		String destinationName, Message message, long timeout);

	/**
	 * Sends the specified payload in a new message to the specified destination
	 * and returns a result.
	 *
	 * @param destinationName the name of the destination to which to send the payload
	 * @param payload the payload to send
	 * @return the result or <code>null</code> if no result was received within
	 * a default amount of time
	 */
	public Object sendSynchronousMessage(
		String destinationName, Object payload);

	/**
	 * Sends the specified payload in a new message to the specified destination
	 * and returns a result.
	 *
	 * @param destinationName the name of the destination to which to send the payload
	 * @param payload the payload to send
	 * @param timeout how long to wait for a response, in milliseconds
	 * @return the result or <code>null</code> if no result was received within
	 * the specified amount of time
	 */
	public Object sendSynchronousMessage(
		String destinationName, Object payload, long timeout);

	/**
	 * Sends the specified payload in a new message to the specified destination
	 * and returns a result. The new message is configured with the specified
	 * response destination name.
	 *
	 * @param destinationName the name of the destination to which to send the payload
	 * @param payload the payload to send
	 * @param responseDestinationName the response destination name with which
	 * to configure the new message
	 * @return the result or <code>null</code> if no result was received within
	 * a default amount of time
	 */
	public Object sendSynchronousMessage(
		String destinationName, Object payload, String responseDestinationName);

	/**
	 * Sends the specified payload in a new message to the specified destination
	 * and returns a result. The new message is configured with the specified
	 * response destination name.
	 *
	 * @param destinationName the name of the destination to which to send the payload
	 * @param payload the payload to send
	 * @param responseDestinationName the response destination name with which
	 * to configure the new message
	 * @param timeout how long to wait for a response, in milliseconds
	 * @return the result or <code>null</code> if no result was received within
	 * the specified amount of time
	 */
	public Object sendSynchronousMessage(
		String destinationName, Object payload, String responseDestinationName,
		long timeout);

}
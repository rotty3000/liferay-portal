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

import java.io.Serializable;

import java.util.Map;

/**
 * Represents a general purpose message that can be used for inter-application
 * or intra-application communication.
 *
 * <p>
 * Messages can carry an object called a payload. The type of the payload is
 * unrestricted. Messages can also carry any number of name / value pairs.
 * </p>
 *
 * @author Raymond Augé
 */
public interface Message extends Cloneable, Serializable {

	/**
	 * Returns a clone of the current message.
	 *
	 * @return a clone of the current message.
	 */
	public Message clone();

	/**
	 * Returns <code>true</code> if the message contains the key in its map of
	 * key / value pairs; <code>false</code> otherwise.
	 *
	 * @param key the key for which to check the message's key / value pair map.
	 * @return <code>true</code> if the message contains the key in its map of
	 * key / value pairs; <code>false</code> otherwise
	 */
	public boolean contains(String key);

	/**
	 * Copies all the fields from the specified message to this message.
	 *
	 * @param message the message from which to copy all the fields
	 */
	public void copyFrom(Message message);

	/**
	 * Copies all the fields from this message to the specified message.
	 *
	 * @param message the message to which to copy all of this message's fields
	 */
	public void copyTo(Message message);

	/**
	 * Returns the value in this message associated with the key or
	 * <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value in this message associated with the key or
	 * <code>null</code> if the key could not be found
	 */
	public Object get(String key);

	/**
	 * Returns the value, interpreted as a boolean, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a boolean, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public boolean getBoolean(String key);

	/**
	 * Returns the name of this message's destination.
	 *
	 * @return the name of this message's destination
	 */
	public String getDestinationName();

	/**
	 * Returns the value, interpreted as a double, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a double, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public double getDouble(String key);

	/**
	 * Returns the value, interpreted as an integer, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as an integer, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public int getInteger(String key);

	/**
	 * Returns the value, interpreted as a long, in this message associated with
	 * the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a long, in this message associated with
	 * the key or <code>null</code> if the key could not be found.
	 */
	public long getLong(String key);

	/**
	 * Returns the message's payload.
	 *
	 * @return the message's payload
	 */
	public Object getPayload();

	/**
	 * Returns the message's response.
	 *
	 * @return the message's response.
	 */
	public Object getResponse();

	/**
	 * Returns the name of the destination to which a response to this message
	 * should be sent.
	 *
	 * @return the name of the destination to which a response to this message
	 * should be sent.
	 */
	public String getResponseDestinationName();

	/**
	 * Returns the message's response ID. The response ID associates a message
	 * response to the original message.
	 *
	 * @return the message's response ID
	 */
	public String getResponseId();

	/**
	 * Returns the value, interpreted as a string, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a string, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public String getString(String key);

	/**
	 * Returns the map of key / value pairs associated with this message.
	 *
	 * @return the map of key / value pairs associated with this message
	 */
	public Map<String, Object> getValues();

	/**
	 * Associates the key with the value in this message's key / value map.
	 *
	 * <p>
	 * If a <code>null</code> value is provided, the specified key is removed
	 * from the map.
	 * </p>
	 *
	 * @param key the key to map to the value
	 * @param value the value with which to associate the key
	 */
	public void put(String key, Object value);

	/**
	 * Removes the key from this message's key / value map.
	 *
	 * @param key the key to remove from this message's key / value map
	 */
	public void remove(String key);

	/**
	 * Sets the name of this message's destination.
	 *
	 * @param destinationName the new name of this message's destination
	 */
	public void setDestinationName(String destinationName);

	/**
	 * Sets the message's payload.
	 *
	 * @param payload the new payload of the message
	 */
	public void setPayload(Object payload);

	/**
	 * Sets the message's response.
	 *
	 * @param response the new response of the message
	 */
	public void setResponse(Object response);

	/**
	 * Sets the name of the destination to which a response to this message
	 * should be sent.
	 *
	 * @param responseDestinationName the new name of the destination to which a
	 * response to this message should be sent.
	 */
	public void setResponseDestinationName(String responseDestinationName);

	/**
	 * Sets the message's response ID. The response ID associates a message
	 * response to the original message.
	 *
	 * @param responseId the new response ID of the message
	 */
	public void setResponseId(String responseId);

	/**
	 * Sets the map of key / value pairs associated with this message.
	 *
	 * @param values the new map of key / value pairs associated with this message
	 */
	public void setValues(Map<String, Object> values);

	/**
	 * Returns a serialized representation of a message as raw bytes.
	 *
	 * @return a serialized representation of a message as raw bytes
	 */
	public byte[] toByteArray();

}
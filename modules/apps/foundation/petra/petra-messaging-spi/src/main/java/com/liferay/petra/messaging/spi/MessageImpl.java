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

package com.liferay.petra.messaging.spi;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.util.GetterUtil;
import com.liferay.petra.io.util.MapUtil;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.io.TransientValue;

import java.io.Serializable;

import java.nio.ByteBuffer;

import java.util.HashMap;
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
 * @author Brian Wing Shun Chan
 * @author Michael C. Han
 */
public class MessageImpl implements Message {

	/**
	 * Returns a message deserialized from raw bytes.
	 *
	 * @param bytes the bytes to deserialize into a message
	 * @return a message deserialized from raw bytes
	 * @throws ClassNotFoundException if the object could not be deserialized
	 */
	public static MessageImpl fromByteArray(byte[] bytes)
		throws ClassNotFoundException {

		Deserializer deserializer = new Deserializer(ByteBuffer.wrap(bytes));

		return deserializer.readObject();
	}

	/**
	 * Returns a clone of the current message.
	 *
	 * @return a clone of the current message.
	 */
	@Override
	public Message clone() {
		MessageImpl message = new MessageImpl();

		message._destinationName = _destinationName;
		message._payload = _payload;
		message._response = _response;
		message._responseDestinationName = _responseDestinationName;
		message._responseId = _responseId;

		if (_values != null) {
			message._values = new HashMap<>(_values);
		}

		return message;
	}

	/**
	 * Returns <code>true</code> if the message contains the key in its map of
	 * key / value pairs; <code>false</code> otherwise.
	 *
	 * @param key the key for which to check the message's key / value pair map.
	 * @return <code>true</code> if the message contains the key in its map of
	 * key / value pairs; <code>false</code> otherwise
	 */
	public boolean contains(String key) {
		if (_values == null) {
			return false;
		}
		else {
			return _values.containsKey(key);
		}
	}

	/**
	 * Copies all the fields from the specified message to this message.
	 *
	 * @param message the message from which to copy all the fields
	 */
	public void copyFrom(Message message) {
		_destinationName = message.getDestinationName();
		_payload = message.getPayload();
		_response = message.getResponse();
		_responseDestinationName = message.getResponseDestinationName();
		_responseId = message.getResponseId();

		if (message.getValues() != null) {
			_values = new HashMap<>(message.getValues());
		}
	}

	/**
	 * Copies all the fields from this message to the specified message.
	 *
	 * @param message the message to which to copy all of this message's fields
	 */
	public void copyTo(Message message) {
		message.setDestinationName(_destinationName);
		message.setPayload(_payload);
		message.setResponse(_response);
		message.setResponseDestinationName(_responseDestinationName);
		message.setResponseId(_responseId);

		if (_values != null) {
			message.setValues(new HashMap<>(_values));
		}
	}

	/**
	 * Returns the value in this message associated with the key or
	 * <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value in this message associated with the key or
	 * <code>null</code> if the key could not be found
	 */
	public Object get(String key) {
		if (_values == null) {
			return null;
		}

		Object value = _values.get(key);

		if (value instanceof TransientValue) {
			@SuppressWarnings("unchecked")
			TransientValue<Object> transientValue =
				(TransientValue<Object>)value;

			value = transientValue.getValue();
		}

		return value;
	}

	/**
	 * Returns the value, interpreted as a boolean, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a boolean, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public boolean getBoolean(String key) {
		boolean value = false;

		Object object = get(key);

		if (object instanceof Boolean) {
			value = ((Boolean)object).booleanValue();
		}
		else {
			value = GetterUtil.getBoolean(object);
		}

		return value;
	}

	/**
	 * Returns the name of this message's destination.
	 *
	 * @return the name of this message's destination
	 */
	public String getDestinationName() {
		return _destinationName;
	}

	/**
	 * Returns the value, interpreted as a double, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a double, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public double getDouble(String key) {
		double value = 0;

		Object object = get(key);

		if (object instanceof Number) {
			value = ((Number)object).doubleValue();
		}
		else {
			value = GetterUtil.getDouble(object);
		}

		return value;
	}

	/**
	 * Returns the value, interpreted as an integer, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as an integer, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public int getInteger(String key) {
		int value = 0;

		Object object = get(key);

		if (object instanceof Number) {
			value = ((Number)object).intValue();
		}
		else {
			value = GetterUtil.getInteger(object);
		}

		return value;
	}

	/**
	 * Returns the value, interpreted as a long, in this message associated with
	 * the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a long, in this message associated with
	 * the key or <code>null</code> if the key could not be found.
	 */
	public long getLong(String key) {
		long value = 0;

		Object object = get(key);

		if (object instanceof Number) {
			value = ((Number)object).longValue();
		}
		else {
			value = GetterUtil.getLong(object);
		}

		return value;
	}

	/**
	 * Returns the message's payload.
	 *
	 * @return the message's payload
	 */
	public Object getPayload() {
		return _payload;
	}

	/**
	 * Returns the message's response.
	 *
	 * @return the message's response.
	 */
	public Object getResponse() {
		return _response;
	}

	/**
	 * Returns the name of the destination to which a response to this message
	 * should be sent.
	 *
	 * @return the name of the destination to which a response to this message
	 * should be sent.
	 */
	public String getResponseDestinationName() {
		return _responseDestinationName;
	}

	/**
	 * Returns the message's response ID. The response ID associates a message
	 * response to the original message.
	 *
	 * @return the message's response ID
	 */
	public String getResponseId() {
		return _responseId;
	}

	/**
	 * Returns the value, interpreted as a string, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 *
	 * @param key the key for which to return the associated value
	 * @return the value, interpreted as a string, in this message associated
	 * with the key or <code>null</code> if the key could not be found.
	 */
	public String getString(String key) {
		return GetterUtil.getString(String.valueOf(get(key)));
	}

	/**
	 * Returns the map of key / value pairs associated with this message.
	 *
	 * @return the map of key / value pairs associated with this message
	 */
	public Map<String, Object> getValues() {
		return _values;
	}

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
	public void put(String key, Object value) {
		if (value == null) {
			if (_values != null) {
				_values.remove(key);
			}

			return;
		}

		if (_values == null) {
			_values = new HashMap<>();
		}

		if (!(value instanceof Serializable)) {
			value = new TransientValue<>(value);
		}

		_values.put(key, value);
	}

	/**
	 * Removes the key from this message's key / value map.
	 *
	 * @param key the key to remove from this message's key / value map
	 */
	public void remove(String key) {
		if (_values != null) {
			_values.remove(key);
		}
	}

	/**
	 * Sets the name of this message's destination.
	 *
	 * @param destinationName the new name of this message's destination
	 */
	public void setDestinationName(String destinationName) {
		_destinationName = destinationName;
	}

	/**
	 * Sets the message's payload.
	 *
	 * @param payload the new payload of the message
	 */
	public void setPayload(Object payload) {
		_payload = payload;
	}

	/**
	 * Sets the message's response.
	 *
	 * @param response the new response of the message
	 */
	public void setResponse(Object response) {
		_response = response;
	}

	/**
	 * Sets the name of the destination to which a response to this message
	 * should be sent.
	 *
	 * @param responseDestinationName the new name of the destination to which a
	 * response to this message should be sent.
	 */
	public void setResponseDestinationName(String responseDestinationName) {
		_responseDestinationName = responseDestinationName;
	}

	/**
	 * Sets the message's response ID. The response ID associates a message
	 * response to the original message.
	 *
	 * @param responseId the new response ID of the message
	 */
	public void setResponseId(String responseId) {
		_responseId = responseId;
	}

	/**
	 * Sets the map of key / value pairs associated with this message.
	 *
	 * @param values the new map of key / value pairs associated with this message
	 */
	public void setValues(Map<String, Object> values) {
		_values = values;
	}

	/**
	 * Returns a serialized representation of a message as raw bytes.
	 *
	 * @return a serialized representation of a message as raw bytes
	 */
	public byte[] toByteArray() {
		Serializer serializer = new Serializer();

		serializer.writeObject(this);

		ByteBuffer byteBuffer = serializer.toByteBuffer();

		return byteBuffer.array();
	}

	/**
	 * Returns a string representation of this message
	 *
	 * @return a string representation of this message
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(13);

		sb.append("{destinationName=");
		sb.append(_destinationName);
		sb.append(", response=");
		sb.append(_response);
		sb.append(", responseDestinationName=");
		sb.append(_responseDestinationName);
		sb.append(", responseId=");
		sb.append(_responseId);
		sb.append(", payload=");
		sb.append(_payload);
		sb.append(", values=");
		sb.append(MapUtil.toString(_values, null, ".*[pP]assword.*"));
		sb.append("}");

		return sb.toString();
	}

	private String _destinationName;
	private Object _payload;
	private Object _response;
	private String _responseDestinationName;
	private String _responseId;
	private Map<String, Object> _values;

}
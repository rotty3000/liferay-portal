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

package com.liferay.vulcan.test.message;

/**
 * Instances of this class represent a JSON object field, represented by a
 * String key and a value of type {@code T}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class Field<T> {

	public Field(String key, T value) {
		_key = key;
		_value = value;
	}

	/**
	 * Returns the key of this field.
	 *
	 * @return the key of the field
	 * @review
	 */
	public String getKey() {
		return _key;
	}

	/**
	 * Returns the value of this field.
	 *
	 * @return the value of the field
	 * @review
	 */
	public T getValue() {
		return _value;
	}

	private final String _key;
	private final T _value;

}
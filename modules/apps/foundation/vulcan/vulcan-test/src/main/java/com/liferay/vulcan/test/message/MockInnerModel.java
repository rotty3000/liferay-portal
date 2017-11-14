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

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class represent a mock inner model that can be written
 * using a {@link com.liferay.vulcan.message.json.SingleModelMessageMapper}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class MockInnerModel {

	/**
	 * Return the boolean field of this model.
	 *
	 * @return the boolean field of the model
	 * @review
	 */
	public Field<Boolean> getBooleanField() {
		return new Field<>("boolean", true);
	}

	/**
	 * Return the link of this model.
	 *
	 * @return the link of the model
	 * @review
	 */
	public Field<String> getLink() {
		return new Field<>("link", "www.liferay.com");
	}

	/**
	 * Return the number field of this model.
	 *
	 * @return the number field of the model
	 * @review
	 */
	public Field<Number> getNumberField() {
		return new Field<>("number", 42);
	}

	/**
	 * Return the string field of this model.
	 *
	 * @return the string field of the model
	 * @review
	 */
	public Field<String> getStringField() {
		return new Field<>("string", "A string");
	}

	/**
	 * Return the types of this model.
	 *
	 * @return the types of the model
	 * @review
	 */
	public List<String> getTypes() {
		return new ArrayList<String>() {
			{
				add("Type");
			}
		};
	}

	/**
	 * Return the url of this model.
	 *
	 * @return the url of the model
	 * @review
	 */
	public String getURL() {
		return "localhost:8080/inner";
	}

}
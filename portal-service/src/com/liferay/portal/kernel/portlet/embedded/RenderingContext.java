/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.portlet.embedded;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

/**
 * @author Tomas Polesovsky
 */
public class RenderingContext implements Serializable {

	public static RenderingContext fromString(String context) {
		if (Validator.isNull(context)) {
			return null;
		}

		String[] items = StringUtil.split(context);

		if (items.length != 3) {
			return null;
		}

		RenderingContext result = new RenderingContext();

		result.setParentClassName(items[0]);
		result.setId(items[1]);
		result.setTimestamp(Long.valueOf(items[2]));

		return result;
	}

	public String getId() {
		return _id;
	}

	public String getParentClassName() {
		return _parentClassName;
	}

	public long getTimestamp() {
		return _timestamp;
	}

	public void setId(String id) {
		this._id = id;
	}

	public void setParentClassName(String parentClassName) {
		this._parentClassName = parentClassName;
	}

	public void setTimestamp(long timestamp) {
		this._timestamp = timestamp;
	}

	@Override
	public String toString() {
		String[] items = new String[] {
			getParentClassName(), getId(), String.valueOf(getTimestamp())
		};

		return StringUtil.merge(items);
	}

	private String _id;
	private String _parentClassName;
	private long _timestamp;

}
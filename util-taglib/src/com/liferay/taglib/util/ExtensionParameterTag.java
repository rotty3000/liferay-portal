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

package com.liferay.taglib.util;


import com.liferay.taglib.TagSupport;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 * @author Carlos Sierra Andrés
 */
public class ExtensionParameterTag extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		Tag parentTag = getParent();

		if (!(parentTag instanceof ExtensionTag)) {
			throw new JspException(
				"Parameter tag can only be used as a child of extension tag");
		}

		ExtensionTag extensionTag = (ExtensionTag)parentTag;

		Object value = getValue();

		if (value == null) {
			value = pageContext.findAttribute(getKey());
		}

//		extensionTag.addExtensionParameter(getKey(), value);

		return super.doEndTag();
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String key) {
		_key = key;
	}

	public Object getValue() {
		return _value;
	}

	public void setValue(Object value) {
		_value = value;
	}

	private String _key;
	private Object _value;
}

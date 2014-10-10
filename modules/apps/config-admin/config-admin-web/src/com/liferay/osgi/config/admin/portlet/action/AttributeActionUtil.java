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

package com.liferay.osgi.config.admin.portlet.action;

import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import javax.portlet.PortletRequest;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 *
 */
public class AttributeActionUtil {

	public static Dictionary<String, Object> bindPropertiesFromRequest(
		PortletRequest portletRequest,
		ObjectClassDefinition objectClassDefinition,
		String ddmFormfieldNamespace) {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();

		AttributeDefinition[] attributeDefinitions =
						objectClassDefinition.getAttributeDefinitions(
							ObjectClassDefinition.ALL);

		for (AttributeDefinition attributeDefinition : attributeDefinitions) {
			String id = attributeDefinition.getID();
			int type = attributeDefinition.getType();
			int cardinality = attributeDefinition.getCardinality();

			String paramName = id.concat(ddmFormfieldNamespace);

			Object paramValue = null;

			if (cardinality == 0) {
				paramValue = typedParamValue(portletRequest, paramName, type);
			}
			else {
				if (cardinality < 1) {

					//TODO check if its OK to return a List
					Vector<Object> cardinalValues = new Vector<Object>();

					cardinalValues.addAll(
						Arrays.asList(
							typedParamValues(portletRequest, paramName, type)));

					paramValue = cardinalValues;
				}
				else if (cardinality >0) {
					paramValue = typedParamValues(
						portletRequest, paramName, type);
				}
			}

			properties.put(id, paramValue);
		}

		return properties;
	}

	public static Object typedParamValue(
		PortletRequest portletRequest, String paramName, int type) {

		switch(type) {
			case AttributeDefinition.BOOLEAN: {
				return ParamUtil.getBoolean(portletRequest, paramName);
			}

			case AttributeDefinition.LONG: {
				return ParamUtil.getLong(portletRequest, paramName);
			}

			case AttributeDefinition.DOUBLE: {
				return ParamUtil.getDouble(portletRequest, paramName);
			}

			case AttributeDefinition.FLOAT: {
				return ParamUtil.getFloat(portletRequest, paramName);
			}

			case AttributeDefinition.INTEGER: {
				return ParamUtil.getBoolean(portletRequest, paramName);
			}

			default: {
				return ParamUtil.getString(portletRequest, paramName);
			}
		}
	}

	public static Object typedParamValues(
		PortletRequest portletRequest, String paramName, int type) {

		switch(type) {
			case AttributeDefinition.BOOLEAN: {
				return ParamUtil.getBooleanValues(portletRequest, paramName);
			}

			case AttributeDefinition.LONG: {
				return ParamUtil.getLongValues(portletRequest, paramName);
			}

			case AttributeDefinition.DOUBLE: {
				return ParamUtil.getDoubleValues(portletRequest, paramName);
			}

			case AttributeDefinition.FLOAT: {
				return ParamUtil.getFloatValues(portletRequest, paramName);
			}

			case AttributeDefinition.INTEGER: {
				return ParamUtil.getIntegerValues(portletRequest, paramName);
			}

			default: {
				return ParamUtil.getParameterValues(portletRequest, paramName);
			}
		}
	}

}
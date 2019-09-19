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

package com.liferay.bean.portlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.service.cdi.annotations.BeanPropertyType;

/**
 *  This is an annotation that provides a way to associate a CDI bean annotated
 *  with @{@link org.osgi.service.cdi.annotations.Service} with a service type.
 *
 * @author Neil Griffin
 */
@BeanPropertyType
@Retention(RetentionPolicy.RUNTIME)
@Target(
	{
		ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
		ElementType.TYPE
	}
)
public @interface ServiceType {

	String type();

}
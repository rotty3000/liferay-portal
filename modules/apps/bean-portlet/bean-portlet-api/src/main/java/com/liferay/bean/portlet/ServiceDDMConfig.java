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
 *  with @{@link org.osgi.service.cdi.annotations.Service} with a DDM
 *  configuration.
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
public @interface ServiceDDMConfig {

	public static final String PREFIX_ = "ddm.";

	String data_provider_instance_id() default "";

	String data_provider_type() default "";

	String form_deserializer_type() default "";

	String form_field_types_serializer_type() default "";

	String form_field_type_name() default "";

	String form_instance_record_writer_type() default "";

	String form_layout_deserializer_type() default "";

	String form_layout_serializer_type() default "";

	String form_values_deserializer_type() default "";

	String form_values_serializer_type() default "";

	String form_serializer_type() default "";

	String storage_adapter_type() default "";

	String structure_indexer_class_name() default "";

}
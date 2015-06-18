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

package com.liferay.calendar.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Adam Brandizzi
 */
@Meta.OCD(
	id = "com.liferay.calendar.configuration.CalendarResourceServiceConfiguration"
)
public interface CalendarResourceServiceConfiguration {

	@Meta.AD(deflt = "true", required = false)
	public boolean forceAutogenerateCode();

	@Meta.AD(deflt = "0xD96666", required = false)
	public int defaultCalendarColor();
}

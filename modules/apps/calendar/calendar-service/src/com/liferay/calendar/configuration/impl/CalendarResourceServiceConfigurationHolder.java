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

package com.liferay.calendar.configuration.impl;

import com.liferay.calendar.configuration.CalendarResourceServiceConfiguration;

import aQute.bnd.annotation.metatype.Configurable;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Adam Brandizzi
 */
@Component(
	configurationPid = "com.liferay.calendar.configuration.CalendarResourceServiceConfiguration",
	immediate = true,
	service = CalendarResourceServiceConfigurationHolder.class
)
public class CalendarResourceServiceConfigurationHolder {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_calendarResourceServiceConfiguration = Configurable.createConfigurable(
			CalendarResourceServiceConfiguration.class, properties);
	}

	public CalendarResourceServiceConfiguration getCalendarResourceServiceConfiguration() {
		return _calendarResourceServiceConfiguration;
	}

	private CalendarResourceServiceConfiguration _calendarResourceServiceConfiguration;
}

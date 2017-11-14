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

package com.liferay.analytics.java.client.internal;

import com.liferay.analytics.model.AnalyticsEventsMessage;

import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Eduardo Garcia
 */
@Ignore
public class AnalyticsClientImplTest {

	@Test
	public void testAnalyticsEventCreation() {
		AnalyticsEventsMessage.Builder analyticsEventsMessageBuilder =
			AnalyticsEventsMessage.builder("ApplicationKey", "UserId");

		analyticsEventsMessageBuilder.contextProperty("languageId", "en_US");
		analyticsEventsMessageBuilder.contextProperty(
			"url", "http://www.liferay.com");

		AnalyticsEventsMessage.Event.Builder eventBuilder =
			AnalyticsEventsMessage.Event.builder("ApplicationId", "View");

		eventBuilder.property("elementId", "banner1");

		analyticsEventsMessageBuilder.event(eventBuilder.build());

		analyticsEventsMessageBuilder.protocolVersion("1.0");

		Response response = _analyticsClientImpl.sendAnalytics(
			analyticsEventsMessageBuilder.build());

		Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
	}

	private final AnalyticsClientImpl _analyticsClientImpl =
		new AnalyticsClientImpl();

}
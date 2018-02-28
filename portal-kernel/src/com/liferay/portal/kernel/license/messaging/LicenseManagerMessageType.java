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

package com.liferay.portal.kernel.license.messaging;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.api.MessageBuilderFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

/**
 * @author Igor Beslic
 */
public enum LicenseManagerMessageType {

	LCS_AVAILABLE, SUBSCRIPTION_VALID, VALIDATE_LCS, VALIDATE_SUBSCRIPTION;

	public static String MESSAGE_BUS_DESTINATION_REQUEST =
		"liferay/lcs_request";

	public static String MESSAGE_BUS_DESTINATION_STATUS = "liferay/lcs_status";

	public static JSONObject getMessagePayload(Message message) {
		return getMessagePayload(message.getPayload());
	}

	public static JSONObject getMessagePayload(Object object) {
		if (object instanceof String) {
			return getMessagePayload((String)object);
		}

		return null;
	}

	public static JSONObject getMessagePayload(String json) {
		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(json);

			valueOf(jsonObject);

			return jsonObject;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static LicenseManagerMessageType valueOf(JSONObject jsonObject) {
		String type = jsonObject.getString("type");

		return valueOf(type);
	}

	public Message createMessage() {
		MessageBuilderFactory messageBuilderFactory =
			getMessageBuilderFactory();

		MessageBuilder messageBuilder =
			messageBuilderFactory.create(getDestinationName());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("type", name());

		messageBuilder.setPayload(jsonObject.toString());

		Message message = messageBuilder.build();

		return message;
	}

	public Message createMessage(LCSPortletState lcsPortletState) {
		MessageBuilderFactory messageBuilderFactory =
			getMessageBuilderFactory();

		MessageBuilder messageBuilder =
			messageBuilderFactory.create(getDestinationName());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("state", lcsPortletState.intValue());
		jsonObject.put("type", name());

		messageBuilder.setPayload(jsonObject.toString());

		Message message = messageBuilder.build();

		return message;
	}

	public String getDestinationName() {
		if ((this == LCS_AVAILABLE) || (this == SUBSCRIPTION_VALID)) {
			return MESSAGE_BUS_DESTINATION_STATUS;
		}
		else if ((this == VALIDATE_LCS) || (this == VALIDATE_SUBSCRIPTION)) {
			return MESSAGE_BUS_DESTINATION_REQUEST;
		}

		return null;
	}

	private MessageBuilderFactory getMessageBuilderFactory() {
		try {
			ServiceTracker<MessageBuilderFactory, MessageBuilderFactory>
				messageBuilderFactoryTracker = getMessageBuilderFactoryTracker();

			MessageBuilderFactory messageBuilderFactory =
				messageBuilderFactoryTracker.waitForService(_timeout);

			return messageBuilderFactory;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	private ServiceTracker<MessageBuilderFactory, MessageBuilderFactory> getMessageBuilderFactoryTracker() {
		Registry registry = RegistryUtil.getRegistry();

		com.liferay.registry.Filter filter = registry.getFilter(
			"(objectClass=com.liferay.petra.messaging.api.MessageBuilderFactory)");

		ServiceTracker<MessageBuilderFactory, MessageBuilderFactory> messageBuilderFactoryTracker =
			registry.trackServices(filter);

		return messageBuilderFactoryTracker;
	}
	private static final int _timeout = 1000;

}
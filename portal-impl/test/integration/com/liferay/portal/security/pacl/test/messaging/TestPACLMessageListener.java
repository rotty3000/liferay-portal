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

package com.liferay.portal.security.pacl.test.messaging;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.api.MessageBuilderFactory;
import com.liferay.petra.messaging.spi.BaseMessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PortalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class TestPACLMessageListener extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		MessageBuilderFactory messageBuilderFactory =
			getMessageBuilderFactory();

		MessageBuilder messageBuilder =
			messageBuilderFactory.create(message.getResponseDestinationName());

		messageBuilder.setPayload(getResults(message));

		messageBuilder.send();
	}

	protected Map<String, Object> getResults(Message message) throws Exception {
		Map<String, Object> results = new HashMap<>();

		try {
			int buildNumber = PortalServiceUtil.getBuildNumber();

			results.put("PortalServiceUtil#getBuildNumber", buildNumber);
		}
		catch (SecurityException se) {
		}

		try {
			User user = UserLocalServiceUtil.getUser(
				TestPropsValues.getUserId());

			results.put("UserLocalServiceUtil#getUser", user);
		}
		catch (SecurityException se) {
		}

		return results;
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
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

package com.liferay.portal.security.pacl.test;

import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.api.MessageBuilderFactory;
import com.liferay.portal.test.rule.PACLTestRule;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import com.liferay.registry.ServiceTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Raymond Augé
 */
public class MessageBusTest {

	@ClassRule
	@Rule
	public static final PACLTestRule paclTestRule = new PACLTestRule();

	@Before
	public void setUp() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());
	}

	@Test
	public void testListen1() throws Exception {
		MessageBuilderFactory messageBuilderFactory =
			getMessageBuilderFactory();

		MessageBuilder messageBuilder =
			messageBuilderFactory.create("liferay/test_pacl_listen_failure");

		messageBuilder.setPayload("Listen Failure");

		Object value = messageBuilder.sendSynchronous();

		Assert.assertNull(value);
	}

	@Test
	public void testListen2() throws Exception {
		MessageBuilderFactory messageBuilderFactory =
			getMessageBuilderFactory();

		MessageBuilder messageBuilder =
			messageBuilderFactory.create("liferay/test_pacl_listen_success");

		messageBuilder.setPayload("Listen Success");

		Object value = messageBuilder.sendSynchronous();

		Assert.assertEquals("Listen Success", value);
	}

	@Test
	public void testSend1() throws Exception {
		try {
			MessageBuilderFactory messageBuilderFactory =
				getMessageBuilderFactory();

			MessageBuilder messageBuilder =
				messageBuilderFactory.create("liferay/test_pacl_send_failure");

			messageBuilder.setPayload("Send Failure");

			messageBuilder.send();

			Assert.fail();
		}
		catch (SecurityException se) {
		}
	}

	@Test
	public void testSend2() throws Exception {
		MessageBuilderFactory messageBuilderFactory =
			getMessageBuilderFactory();

		MessageBuilder messageBuilder =
			messageBuilderFactory.create("liferay/test_pacl_send_success");

		messageBuilder.setPayload("Send Success");

		messageBuilder.send();
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
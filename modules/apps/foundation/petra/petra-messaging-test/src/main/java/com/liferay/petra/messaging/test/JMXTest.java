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

package com.liferay.petra.messaging.test;

import com.liferay.petra.messaging.api.DestinationNames;

import java.lang.management.ManagementFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Raymond Augé
 */
public class JMXTest extends JMXUtil {

	@Test
	public void testMessagingBusManagerFromPlatform() throws Exception {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		testMessageBusManager(mBeanServer);
	}

	@Test
	public void testMessagingBusManagerFromRegistry() throws Exception {
		testMessageBusManager(mBeanServer);
	}

	protected void testMessageBusManager(MBeanServer mBeanServer)
		throws Exception {

		ObjectName objectName = new ObjectName(
			"com.liferay.portal.messaging:classification=message_bus," +
				"name=MessageBusManager");

		Set<ObjectInstance> mBeans = mBeanServer.queryMBeans(objectName, null);

		Assert.assertNotNull(mBeans);
		Assert.assertEquals(mBeans.toString(), 1, mBeans.size());

		Iterator<ObjectInstance> iterator = mBeans.iterator();

		ObjectInstance objectInstance = iterator.next();

		int destinationCount = (int)mBeanServer.getAttribute(
			objectInstance.getObjectName(), "DestinationCount");

		Assert.assertEquals(2, destinationCount);

		String[] destinationNames = (String[])mBeanServer.getAttribute(
			objectInstance.getObjectName(), "DestinationNames");

		Assert.assertEquals(
			Arrays.toString(destinationNames), 2, destinationNames.length);
		Assert.assertArrayEquals(
			new String[] {
				DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE,
				DestinationNames.MESSAGE_BUS_MESSAGE_STATUS
			},
			destinationNames);
	}

}
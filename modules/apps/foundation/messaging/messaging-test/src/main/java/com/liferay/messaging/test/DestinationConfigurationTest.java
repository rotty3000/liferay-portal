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

package com.liferay.messaging.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.messaging.Message;

import java.util.concurrent.Callable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
public class DestinationConfigurationTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		test("tb4.jar", "configuration/tb4");
	}

	@Test
	public void testSerial() throws Exception {
		test("tb5.jar", "configuration/tb5");
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb6.jar", "configuration/tb6");
	}

	protected void test(String bundle, String destination) throws Exception {
		Bundle tbBundle = install(bundle);

		try {
			tbBundle.start();

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destination));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Message message = new Message();

			messageBus.sendMessage(destination, message);

			assertEquals(message, callable.call());
		}
		finally {
			tbBundle.uninstall();
		}
	}

}
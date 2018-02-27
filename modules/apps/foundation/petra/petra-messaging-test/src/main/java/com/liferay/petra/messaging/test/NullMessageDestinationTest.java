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

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.test.tb2.TBParallelDestination;
import com.liferay.petra.messaging.test.tb3.TBSerialDestination;
import com.liferay.petra.messaging.test.tb1.TBSynchronousDestination;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Jesse Rao
 */
public class NullMessageDestinationTest extends TestUtil {

	@Test(expected = NullPointerException.class)
	public void testParallel() throws Exception {
		test("tb2.jar", TBParallelDestination.DESTINATION_NAME);
	}

	@Test(expected = NullPointerException.class)
	public void testSerial() throws Exception {
		test("tb3.jar", TBSerialDestination.DESTINATION_NAME);
	}

	@Test(expected = NullPointerException.class)
	public void testSynchronous() throws Exception {
		test("tb1.jar", TBSynchronousDestination.DESTINATION_NAME);
	}

	protected void test(String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			Assert.assertNotNull(callable);

			Message message = null;

			messageBus.sendMessage(destinationName, message);

			Assert.assertEquals(message, callable.call());
		}
		finally {
			tb.uninstall();
		}
	}

}
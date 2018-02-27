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

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.DestinationType;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.spi.MessageImpl;

/**
 * @author Raymond Augé
 * @author Jesse Rao
 */
public class DestinationConfigurationTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		testSend("tb4.jar", "configuration/tb4");
		testDestinationConfiguration(DestinationType.PARALLEL);
	}

	@Test
	public void testSerial() throws Exception {
		testSend("tb5.jar", "configuration/tb5");
		testDestinationConfiguration(DestinationType.SERIAL);
	}

	@Test
	public void testSynchronous() throws Exception {
		testSend("tb6.jar", "configuration/tb6");
		testDestinationConfiguration(DestinationType.SYNCHRONOUS);
	}

	protected void testDestinationConfiguration(DestinationType destinationType)
		throws Exception {

		String destinationName = null;
		DestinationConfiguration destinationConfiguration = null;

		if (destinationType == DestinationType.SYNCHRONOUS) {
			destinationName = "SYNCHRONOUS_DESTINATION";

			destinationConfiguration = new DestinationConfiguration(
				DestinationType.SYNCHRONOUS, destinationName);
		}
		else if (destinationType == DestinationType.PARALLEL) {
			destinationName = "PARALLEL_DESTINATION";

			destinationConfiguration = new DestinationConfiguration(
				DestinationType.PARALLEL, destinationName);
		}
		else if (destinationType == DestinationType.SERIAL) {
			destinationName = "SERIAL_DESTINATION";

			destinationConfiguration = new DestinationConfiguration(
				DestinationType.SERIAL, destinationName);
		}

		Assert.assertEquals(
			destinationName, destinationConfiguration.getDestinationName());

		Assert.assertEquals(
			destinationType, destinationConfiguration.getDestinationType());

		Assert.assertEquals(
			Integer.MAX_VALUE, destinationConfiguration.getMaximumQueueSize());

		if (destinationType == DestinationType.SERIAL) {
			Assert.assertEquals(
				1, destinationConfiguration.getWorkersCoreSize());
			Assert.assertEquals(
				1, destinationConfiguration.getWorkersMaxSize());
		}
		else if (destinationType == DestinationType.PARALLEL) {
			Assert.assertEquals(
				2, destinationConfiguration.getWorkersCoreSize());
			Assert.assertEquals(
				5, destinationConfiguration.getWorkersMaxSize());
		}

		destinationConfiguration.setMaximumQueueSize(20);
		destinationConfiguration.setWorkersCoreSize(3);
		destinationConfiguration.setWorkersMaxSize(6);

		Assert.assertEquals(20, destinationConfiguration.getMaximumQueueSize());
		Assert.assertEquals(3, destinationConfiguration.getWorkersCoreSize());
		Assert.assertEquals(6, destinationConfiguration.getWorkersMaxSize());

		Assert.assertEquals(
			destinationName.hashCode(), destinationConfiguration.hashCode());
	}

	protected void testSend(String bundle, String destinationName)
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
			
			Message message = new MessageImpl();

			messageBus.sendMessage(destinationName, message);

			Assert.assertEquals(message, callable.call());
		}
		finally {
			tb.uninstall();
		}
	}

}
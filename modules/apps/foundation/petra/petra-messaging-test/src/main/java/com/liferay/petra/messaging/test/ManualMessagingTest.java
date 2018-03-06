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

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.DestinationType;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;

/**
 * @author Raymond Augé
 * @author Jesse Rao
 */
public class ManualMessagingTest extends TestUtil {

	@Test
	public void testSend() throws Exception {
		// create and register destination configuration
		final String destinationName = "SYNCHRONOUS_DESTINATION_CONFIGURATION";
		
		DestinationConfiguration synchronousDestinationConfiguration =
			new DestinationConfiguration(DestinationType.SYNCHRONOUS, destinationName);
		
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		ServiceRegistration<DestinationConfiguration> destinationConfigurationServiceRegistration =
				bundleContext.registerService(
			DestinationConfiguration.class, synchronousDestinationConfiguration, null);

		// create and register message listener
		Collection<String> destinationNames = messageBus.getDestinationNames();

		Assert.assertTrue(destinationNames.contains(destinationName));
		
		CallableMessageListener synchronousDestinationMessageListener =
			new CallableMessageListener();
		
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("destination.name", destinationName);
		
		String[] clazzes = new String[2];
		clazzes[0] = "com.liferay.petra.messaging.api.MessageListener";
		clazzes[1] = "java.util.concurrent.Callable";
		
		ServiceRegistration<?> messageListenerServiceRegistration = bundleContext.registerService(
			clazzes, synchronousDestinationMessageListener, properties);
		
		Assert.assertTrue(messageBus.hasMessageListener(destinationName));
		
		// send message
		MessageBuilder messageBuilder =
				messageBuilderFactory.create(destinationName);
		
		messageBuilder.setPayload("payload2");
		
		Message message = messageBuilder.build();
		
		messageBuilder.send();
		
		// compare received message to sent message
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

		Message receivedMessage = callable.call();

		Assert.assertEquals(message, receivedMessage);
		
		// unregister message listener and destination configuration
		messageListenerServiceRegistration.unregister();
		
		destinationConfigurationServiceRegistration.unregister();
	}

}
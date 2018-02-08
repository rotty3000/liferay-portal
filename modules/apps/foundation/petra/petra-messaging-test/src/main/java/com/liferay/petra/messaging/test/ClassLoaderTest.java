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

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.DestinationType;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBus;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.spi.MessageImpl;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class ClassLoaderTest extends TestUtil {

	@Test
	public void testCustomClassLoader() throws InvalidSyntaxException {
		final TestClassLoader testClassLoader = new TestClassLoader();

		ServiceRegistration<?> configuration1 = registerService(
			DestinationConfiguration.class,
			new DestinationConfiguration(
				DestinationType.SYNCHRONOUS, "liferay/plugintest1"));

		ServiceRegistration<?> configuration2 = registerService(
			DestinationConfiguration.class,
			new DestinationConfiguration(
				DestinationType.PARALLEL, "liferay/plugintest2"));

		ServiceRegistration<?> listener = registerService(
			MessageListener.class,
			new TestClassLoaderMessageListener(testClassLoader),
			"destination.name", "liferay/plugintest1",
			"message.listener.operating.class.loader", testClassLoader);

		ServiceTracker<MessageBus, MessageBus> serviceTracker =
			new ServiceTracker<>(bundleContext, MessageBus.class, null);

		serviceTracker.open();

		try {
			while (serviceTracker.isEmpty()) {
				Thread.sleep(1000);
			}

			Collection<Destination> destinations = messageBus.getDestinations();
			
			Assert.assertEquals(
				destinations.toString(), 4, destinations.size());

			for (Destination destination : destinations) {
				String destinationName = destination.getName();

				Assert.assertTrue(
					destinationName.contains("plugintest") ||
					destinationName.startsWith("liferay/message_bus/"));

				if (destinationName.equals("liferay/plugintest1")) {
					Assert.assertTrue(destination.isRegistered());
				}

				Message message = new MessageImpl();

				messageBus.sendMessage(destinationName, message);
			}
		}
		catch (Exception e) {
			Assert.fail(getStackTrace(e));
		}
		finally {
			configuration1.unregister();
			configuration2.unregister();
			listener.unregister();
		}
	}

	@Test
	public void testDefaultClassLoader() throws InvalidSyntaxException {
		ServiceRegistration<?> configuration1 = registerService(
			DestinationConfiguration.class,
			new DestinationConfiguration(
				DestinationType.SYNCHRONOUS, "liferay/portaltest1"));

		ServiceRegistration<?> configuration2 = registerService(
			DestinationConfiguration.class,
			new DestinationConfiguration(
				DestinationType.PARALLEL, "liferay/portaltest2"));

		ServiceRegistration<?> listener1 = registerService(
			MessageListener.class,
			new TestMessageListener("liferay/portaltest1"), "destination.name",
			"liferay/portaltest1");

		ServiceRegistration<?> listener2 = registerService(
			MessageListener.class,
			new TestMessageListener("liferay/portaltest2"), "destination.name",
			"liferay/portaltest2");

		ServiceTracker<MessageBus, MessageBus> serviceTracker =
			new ServiceTracker<>(bundleContext, MessageBus.class, null);

		serviceTracker.open();

		try {
			while (serviceTracker.isEmpty()) {
				Thread.sleep(1000);
			}

			Collection<Destination> destinations = messageBus.getDestinations();

			Assert.assertEquals(
				destinations.toString(), 4, destinations.size());

			for (Destination destination : destinations) {
				String destinationName = destination.getName();

				Assert.assertTrue(
					destinationName.contains("portaltest") ||
					destinationName.startsWith("liferay/message_bus/"));

				if (destinationName.equals("liferay/portaltest1")) {
					Assert.assertTrue(destination.isRegistered());
				}

				Message message = new MessageImpl();

				messageBus.sendMessage(destinationName, message);
			}
		}
		catch (Exception e) {
			Assert.fail(getStackTrace(e));
		}
		finally {
			configuration1.unregister();
			configuration2.unregister();
			listener1.unregister();
			listener2.unregister();
		}
	}

	protected String getStackTrace(Throwable t) {
		String stackTrace = null;

		PrintWriter printWriter = null;

		try {
			StringWriter stringWriter = new StringWriter();

			printWriter = new PrintWriter(stringWriter);

			t.printStackTrace(printWriter);

			printWriter.flush();

			stackTrace = stringWriter.toString();
		}
		finally {
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}

		return stackTrace;
	}

	private static class TestClassLoader extends ClassLoader {
	}

	private static class TestClassLoaderMessageListener
		implements MessageListener {

		public TestClassLoaderMessageListener(TestClassLoader testClassLoader) {
			_testClassLoader = testClassLoader;
		}

		@Override
		public void receive(Message message) {
			Thread currentThread = Thread.currentThread();

			ClassLoader currentClassLoader =
				currentThread.getContextClassLoader();

			Assert.assertEquals(_testClassLoader, currentClassLoader);
		}

		private final ClassLoader _testClassLoader;

	}

	private static class TestMessageListener implements MessageListener {

		public TestMessageListener(String destinationName) {
			_destinationName = destinationName;
		}

		@Override
		public void receive(Message message) {
			Assert.assertEquals(_destinationName, message.getDestinationName());
		}

		private final String _destinationName;

	}

}
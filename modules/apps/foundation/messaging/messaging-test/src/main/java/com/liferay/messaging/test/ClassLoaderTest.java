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
import static org.junit.Assert.assertTrue;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageListener;

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
			DestinationConfiguration.createSynchronousDestinationConfiguration(
				"liferay/plugintest1"));

		ServiceRegistration<?> configuration2 = registerService(
			DestinationConfiguration.class,
			DestinationConfiguration.createParallelDestinationConfiguration(
				"liferay/plugintest2"));

		ServiceRegistration<?> listener = registerService(
			MessageListener.class, new TestClassLoaderMessageListener(testClassLoader),
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

			assertEquals(destinations.toString(), 4, destinations.size());

			for (Destination destination : destinations) {
				String destinationName = destination.getName();

				assertTrue(
					destinationName.contains("plugintest") ||
					destinationName.startsWith("liferay/message_bus/"));

				if (destinationName.equals("liferay/plugintest1")) {
					assertEquals(1, destination.getMessageListenerCount());
				}

				if (destination.getMessageListenerCount() > 0) {
					Message message = new Message();

					message.setDestinationName(destinationName);

					destination.send(message);
				}
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
			DestinationConfiguration.createSynchronousDestinationConfiguration(
				"liferay/portaltest1"));

		ServiceRegistration<?> configuration2 = registerService(
			DestinationConfiguration.class,
			DestinationConfiguration.createParallelDestinationConfiguration(
				"liferay/portaltest2"));

		ServiceRegistration<?> listener1 = registerService(
			MessageListener.class, new TestMessageListener("liferay/portaltest1"),
			"destination.name", "liferay/portaltest1");

		ServiceRegistration<?> listener2 = registerService(
			MessageListener.class, new TestMessageListener("liferay/portaltest2"),
			"destination.name", "liferay/portaltest2");

		ServiceTracker<MessageBus, MessageBus> serviceTracker =
			new ServiceTracker<>(bundleContext, MessageBus.class, null);

		serviceTracker.open();

		try {
			while (serviceTracker.isEmpty()) {
				Thread.sleep(1000);
			}

			Collection<Destination> destinations = messageBus.getDestinations();

			assertEquals(destinations.toString(), 4, destinations.size());

			for (Destination destination : destinations) {
				String destinationName = destination.getName();

				assertTrue(
					destinationName.contains("portaltest") ||
					destinationName.startsWith("liferay/message_bus/"));

				if (destinationName.equals("liferay/portaltest1")) {
					assertEquals(1, destination.getMessageListenerCount());
				}

				if (destination.getMessageListenerCount() > 0) {
					Message message = new Message();

					message.setDestinationName(destinationName);

					destination.send(message);
				}
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

	private String getStackTrace(Throwable t) {
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

}
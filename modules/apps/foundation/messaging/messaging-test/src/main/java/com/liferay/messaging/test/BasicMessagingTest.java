package com.liferay.messaging.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBus;

import java.util.concurrent.Callable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

public class BasicMessagingTest extends TestUtil{

	@Test
	public void testParallel() throws Exception {
		Bundle tbBundle = install("tb2.jar");

		try {
			tbBundle.start();

			Filter filter = bundleContext.createFilter(
				"(&(objectClass=java.util.concurrent.Callable)" +
					"(destination.name=parallel/test))");

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Message message = new Message();

			MessageBus messageBus = getMessageBus();

			messageBus.sendMessage("parallel/test", message);

			assertEquals(message, callable.call());
		}
		finally {
			tbBundle.uninstall();
		}
	}

	@Test
	public void testSerial() throws Exception {
		Bundle tbBundle = install("tb3.jar");

		try {
			tbBundle.start();

			Filter filter = bundleContext.createFilter(
				"(&(objectClass=java.util.concurrent.Callable)" +
					"(destination.name=serial/test))");

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Message message = new Message();

			MessageBus messageBus = getMessageBus();

			messageBus.sendMessage("serial/test", message);

			assertEquals(message, callable.call());
		}
		finally {
			tbBundle.uninstall();
		}
	}

	@Test
	public void testSynchronous() throws Exception {
		Bundle tbBundle = install("tb1.jar");

		try {
			tbBundle.start();

			Filter filter = bundleContext.createFilter(
				"(&(objectClass=java.util.concurrent.Callable)" +
					"(destination.name=synchronous/test))");

			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);

			callableST.open();

			Callable<Message> callable = callableST.waitForService(timeout);

			assertNotNull(callable);

			Message message = new Message();

			MessageBus messageBus = getMessageBus();

			messageBus.sendMessage("synchronous/test", message);

			assertEquals(message, callable.call());
		}
		finally {
			tbBundle.uninstall();
		}
	}

}
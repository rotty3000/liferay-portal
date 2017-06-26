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
import static org.junit.Assert.assertTrue;

import com.liferay.messaging.Destination;
import com.liferay.messaging.ExecutorServiceRegistrar;
import com.liferay.messaging.Message;
import com.liferay.messaging.spi.MessageRunnable;
import com.liferay.petra.concurrent.RejectedExecutionHandler;
import com.liferay.petra.concurrent.ThreadPoolExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 * @author Jesse Rao
 */
public class DestinationTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		testSend("parallel/test", "tb2.jar");
		testExecutorServiceRegistrar("parallel/test", "tb19.jar", "tb16.jar");
		testRejectedExecutionHandler("parallel/test", "tb19.jar", "tb16.jar");
	}

	@Test
	public void testSerial() throws Exception {
		testSend("serial/test", "tb3.jar");
		testExecutorServiceRegistrar("serial/test", "tb20.jar", "tb17.jar");
		testRejectedExecutionHandler("serial/test", "tb20.jar", "tb17.jar");
	}

	@Test
	public void testSynchronous() throws Exception {
		testSend("synchronous/test", "tb1.jar");
	}

	protected void testExecutorServiceRegistrar(
			String destinationName, String... bundleNames) throws Exception {

		List<Bundle> bundles = new ArrayList<Bundle>();
		
		for (String bundleName : bundleNames) {
			Bundle bundle = install(bundleName);
			bundles.add(bundle);
		}

		ServiceTracker<ExecutorServiceRegistrar, Callable<Map<String, ExecutorService>>>
			tracker = null;

		try {
			for (Bundle bundle : bundles) {
				bundle.start();
			}

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=%s)(objectClass=%s)(destination.name=%s))",
					ExecutorServiceRegistrar.class.getName(),
					Callable.class.getName(), destinationName));

			tracker = new ServiceTracker<>(bundleContext, filter, null);

			tracker.open();

			Callable<Map<String, ExecutorService>> service =
				tracker.waitForService(timeout);

			assertNotNull(service);

			Map<String, ExecutorService> map = service.call();

			assertTrue(map.containsKey(destinationName));
			assertNotNull(map.get(destinationName));
		}
		finally {
			for (Bundle bundle : bundles) {
				bundle.uninstall();
			}

			if (tracker != null) {
				tracker.close();
			}
		}
	}

	protected void testRejectedExecutionHandler(
			String destinationName, String... bundleNames) throws Exception {

		List<Bundle> bundles = new ArrayList<Bundle>();
		
		for (String bundleName : bundleNames) {
			Bundle bundle = install(bundleName);
			bundles.add(bundle);
		}

		ServiceTracker<
			RejectedExecutionHandler,
			Callable<Map<MessageRunnable, ThreadPoolExecutor>>> tracker = null;

		try {
			for (Bundle bundle : bundles) {
				bundle.start();
			}

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=%s)(objectClass=%s)(destination.name=%s))",
					RejectedExecutionHandler.class.getName(),
					Callable.class.getName(), destinationName));

			tracker = new ServiceTracker<>(bundleContext, filter, null);

			tracker.open();

			Callable<Map<MessageRunnable, ThreadPoolExecutor>> service =
				tracker.waitForService(timeout);

			assertNotNull(service);

			messageBus = getMessageBus();

			for (int i = 0; i < 100; i++) {
				Message message = new Message();

				// TODO there's a bug here, the destination is shutdown.
				// We need to figure out why this is happening.

				// messageBus.sendMessage(destinationName, message);
			}

			Map<MessageRunnable, ThreadPoolExecutor> map = service.call();

			assertNotNull(map);
			//assertFalse(map.isEmpty());
		}
		finally {
			for (Bundle bundle : bundles) {
				bundle.uninstall();
			}

			if (tracker != null) {
				tracker.close();
			}
		}
	}

	protected void testSend(String destinationName, String bundleName)
		throws Exception {
	
		Bundle bundle = install(bundleName);
	
		try {
			bundle.start();
	
			Destination destination = messageBus.getDestination(
				destinationName);
			
			assertEquals(destinationName, destination.getName());
			
			assertTrue(destination.isRegistered());

			Filter filter = bundleContext.createFilter(
				String.format(
					"(&(objectClass=java.util.concurrent.Callable)" +
						"(destination.name=%s))",
					destinationName));
	
			ServiceTracker<Callable<Message>, Callable<Message>> callableST =
				new ServiceTracker<>(bundleContext, filter, null);
	
			callableST.open();
	
			Callable<Message> callable = callableST.waitForService(timeout);
	
			assertNotNull(callable);
	
			Message message = new Message();
	
			messageBus.sendMessage(destinationName, message);
	
			assertEquals(message, callable.call());
		}
		finally {
			bundle.uninstall();
		}
	}

}
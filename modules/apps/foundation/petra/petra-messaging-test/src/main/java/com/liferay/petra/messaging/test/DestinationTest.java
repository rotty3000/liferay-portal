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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.petra.concurrent.RejectedExecutionHandler;
import com.liferay.petra.concurrent.ThreadPoolExecutor;
import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.ExecutorServiceRegistrar;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.api.MessageListenerException;
import com.liferay.petra.messaging.spi.BaseAsyncDestination;
import com.liferay.petra.messaging.spi.MessageImpl;
import com.liferay.petra.messaging.spi.MessageRunnable;
import com.liferay.petra.messaging.test.tb1.TBSynchronousDestination;
import com.liferay.petra.messaging.test.tb3.TBSerialDestination;
import com.liferay.petra.messaging.test.tb2.TBParallelDestination;

/**
 * @author Raymond Augé
 * @author Jesse Rao
 */
public class DestinationTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		testSend(TBParallelDestination.DESTINATION_NAME, "tb2.jar");
		testExecutorServiceRegistrar(TBParallelDestination.DESTINATION_NAME, "tb16.jar", "tb19.jar");
		testRejectedExecutionHandler(TBParallelDestination.DESTINATION_NAME, "tb16.jar", "tb19.jar");
	}

	@Test
	public void testSerial() throws Exception {
		testSend(TBSerialDestination.DESTINATION_NAME, "tb3.jar");
		testExecutorServiceRegistrar(TBSerialDestination.DESTINATION_NAME, "tb17.jar", "tb20.jar");
		testRejectedExecutionHandler(TBSerialDestination.DESTINATION_NAME, "tb17.jar", "tb20.jar");
	}

	@Test
	public void testSynchronous() throws Exception {
		testSend(TBSynchronousDestination.DESTINATION_NAME, "tb1.jar");
	}

	protected void testExecutorServiceRegistrar(
			String destinationName, String... bundleNames)
		throws Exception {

		List<Bundle> bundles = new ArrayList<>();

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

			Assert.assertNotNull(service);

			Map<String, ExecutorService> map = service.call();

			Assert.assertTrue(map.containsKey(destinationName));
			Assert.assertNotNull(map.get(destinationName));
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
			String destinationName, String... bundleNames)
		throws Exception {

		List<Bundle> bundles = new ArrayList<>();

		for (String bundleName : bundleNames) {
			Bundle bundle = install(bundleName);

			bundles.add(bundle);
		}

		ServiceTracker<RejectedExecutionHandler, Callable<Map<MessageRunnable, ThreadPoolExecutor>>>
			tracker = null;
		ServiceRegistration<MessageListener> listenerRegistration = null;

		try {
			final CountDownLatch latch = new CountDownLatch(1);

			MessageListener messageListener = new MessageListener() {

				@Override
				public void receive(Message message)
					throws MessageListenerException {

					try {
						latch.await(200, TimeUnit.MILLISECONDS);
					}
					catch (InterruptedException ie) {
						_logger.error("Interupted!", ie);
					}
				}

			};

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("destination.name", destinationName);

			listenerRegistration = registerService(
				MessageListener.class, messageListener, properties);

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

			Assert.assertNotNull(service);

			messageBus = getMessageBus();

			BaseAsyncDestination destination =
				(BaseAsyncDestination)messageBus.getDestination(
					destinationName);

			int concurrency = destination.getWorkersMaxSize() * 2;

			ExecutorService executorService = Executors.newWorkStealingPool(
				concurrency);

			Runnable messanger = () -> {
				for (int i = 0; i < 1000; i++) {
					Message message = new MessageImpl();

					try {
						messageBus.sendMessage(destinationName, message);
					}
					catch (Throwable e) {
						_logger.error("{}", e.getMessage());
					}
				}
			};

			IntStream.range(0, concurrency).forEach(
				i -> executorService.execute(messanger));

			latch.await(500, TimeUnit.MILLISECONDS);

			Map<MessageRunnable, ThreadPoolExecutor> map = service.call();

			Assert.assertNotNull(map);
			Assert.assertFalse(map.isEmpty());
		}
		finally {
			for (Bundle bundle : bundles) {
				bundle.uninstall();
			}

			if (tracker != null) {
				tracker.close();
			}

			if (listenerRegistration != null) {
				listenerRegistration.unregister();
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

			Assert.assertEquals(destinationName, destination.getName());

			Assert.assertTrue(destination.isRegistered());

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
			bundle.uninstall();
		}
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		DestinationTest.class);

}

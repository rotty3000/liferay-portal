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
import static org.junit.Assert.assertFalse;

import com.liferay.messaging.InboundMessageProcessor;
import com.liferay.messaging.InboundMessageProcessorFactory;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageProcessorException;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

/**
 * @author Raymond Augé
 */
public class InboundMessageProcessorFactoryTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		test("tb2.jar", "parallel/test");
	}

	@Test
	public void testSerial() throws Exception {
		test("tb3.jar", "serial/test");
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb1.jar", "synchronous/test");
	}

	protected void test(String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		final Deferred<Integer> afterReceive = new Deferred<>();
		final Deferred<Integer> afterThread = new Deferred<>();
		final Deferred<Integer> beforeReceive = new Deferred<>();
		final Deferred<Integer> beforeThread = new Deferred<>();
		final Deferred<Integer> called = new Deferred<>();

		InboundMessageProcessor inboundMessageProcessor =
			new InboundMessageProcessor() {

				@Override
				public void afterReceive(Message message)
					throws MessageProcessorException {

					afterReceive.resolve(5);
				}

				@Override
				public void afterThread(Message message, Thread dispatchThread)
					throws MessageProcessorException {

					afterThread.resolve(4);
				}

				@Override
				public Message beforeReceive(Message message)
					throws MessageProcessorException {

					beforeReceive.resolve(2);

					return message;
				}

				@Override
				public Message beforeThread(
						Message message, Thread dispatchThread)
					throws MessageProcessorException {

					beforeThread.resolve(3);

					return message;
				}

			};

		InboundMessageProcessorFactory factory =
			new InboundMessageProcessorFactory() {

				@Override
				public InboundMessageProcessor create() {
					called.resolve(1);

					return inboundMessageProcessor;
				}

			};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", destinationName);

		ServiceRegistration<InboundMessageProcessorFactory>
			serviceRegistration = bundleContext.registerService(
				InboundMessageProcessorFactory.class, factory, properties);

		try {
			tb.start();

			Promise<Integer> promiseToAfterReceive = afterReceive.getPromise();

			assertFalse(promiseToAfterReceive.isDone());

			Promise<Integer> promiseToAfterThread = afterThread.getPromise();

			assertFalse(promiseToAfterThread.isDone());

			Promise<Integer> promiseToBeforeReceive =
				beforeReceive.getPromise();

			assertFalse(promiseToBeforeReceive.isDone());

			Promise<Integer> promiseToBeforeThread = beforeThread.getPromise();

			assertFalse(promiseToBeforeThread.isDone());

			Message message = new Message();

			Promise<Integer> promiseToCalled = called.getPromise();

			assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage(destinationName, message);

			assertEquals(Integer.valueOf(1), promiseToCalled.getValue());
			assertEquals(Integer.valueOf(2), promiseToBeforeReceive.getValue());
			assertEquals(Integer.valueOf(3), promiseToBeforeThread.getValue());
			assertEquals(Integer.valueOf(4), promiseToAfterThread.getValue());
			assertEquals(Integer.valueOf(5), promiseToAfterReceive.getValue());

			tb.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
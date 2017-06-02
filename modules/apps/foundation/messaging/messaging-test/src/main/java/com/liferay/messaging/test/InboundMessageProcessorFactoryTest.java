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
import com.liferay.messaging.MessageBus;
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
		Bundle tbBundle = install("tb2.jar");

		final Deferred<Integer> afterReceive = new Deferred<>();
		final Deferred<Integer> afterThread = new Deferred<>();
		final Deferred<Integer> beforeReceive = new Deferred<>();
		final Deferred<Integer> beforeThread = new Deferred<>();
		final Deferred<Integer> called = new Deferred<>();

		InboundMessageProcessor inboundMessageProcessor = new InboundMessageProcessor() {

			@Override
			public void afterReceive(Message message) throws MessageProcessorException {
				afterReceive.resolve(5);
			}

			@Override
			public void afterThread(Message message, Thread dispatchThread) throws MessageProcessorException {
				afterThread.resolve(4);
			}

			@Override
			public Message beforeReceive(Message message) throws MessageProcessorException {
				beforeReceive.resolve(2);

				return message;
			}

			@Override
			public Message beforeThread(Message message, Thread dispatchThread) throws MessageProcessorException {
				beforeThread.resolve(3);

				return message;
			}

		};

		InboundMessageProcessorFactory factory = new InboundMessageProcessorFactory() {

			@Override
			public InboundMessageProcessor create() {
				called.resolve(1);

				return inboundMessageProcessor;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", "parallel/test");

		ServiceRegistration<InboundMessageProcessorFactory> serviceRegistration =
			bundleContext.registerService(
				InboundMessageProcessorFactory.class, factory, properties);

		try {
			tbBundle.start();

			Promise<Integer> promiseToAfterReceive = afterReceive.getPromise();

			assertFalse(promiseToAfterReceive.isDone());

			Promise<Integer> promiseToAfterThread = afterThread.getPromise();

			assertFalse(promiseToAfterThread.isDone());

			Promise<Integer> promiseToBeforeReceive = beforeReceive.getPromise();

			assertFalse(promiseToBeforeReceive.isDone());

			Promise<Integer> promiseToBeforeThread = beforeThread.getPromise();

			assertFalse(promiseToBeforeThread.isDone());

			Message message = new Message();

			Promise<Integer> promiseToCalled = called.getPromise();

			assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage("parallel/test", message);

			assertEquals(new Integer(1), promiseToCalled.getValue());
			assertEquals(new Integer(2), promiseToBeforeReceive.getValue());
			assertEquals(new Integer(3), promiseToBeforeThread.getValue());
			assertEquals(new Integer(4), promiseToAfterThread.getValue());
			assertEquals(new Integer(5), promiseToAfterReceive.getValue());

			tbBundle.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testSerial() throws Exception {
		Bundle tbBundle = install("tb3.jar");

		final Deferred<Integer> afterReceive = new Deferred<>();
		final Deferred<Integer> afterThread = new Deferred<>();
		final Deferred<Integer> beforeReceive = new Deferred<>();
		final Deferred<Integer> beforeThread = new Deferred<>();
		final Deferred<Integer> called = new Deferred<>();

		InboundMessageProcessor inboundMessageProcessor = new InboundMessageProcessor() {

			@Override
			public void afterReceive(Message message) throws MessageProcessorException {
				afterReceive.resolve(5);
			}

			@Override
			public void afterThread(Message message, Thread dispatchThread) throws MessageProcessorException {
				afterThread.resolve(4);
			}

			@Override
			public Message beforeReceive(Message message) throws MessageProcessorException {
				beforeReceive.resolve(2);

				return message;
			}

			@Override
			public Message beforeThread(Message message, Thread dispatchThread) throws MessageProcessorException {
				beforeThread.resolve(3);

				return message;
			}

		};

		InboundMessageProcessorFactory factory = new InboundMessageProcessorFactory() {

			@Override
			public InboundMessageProcessor create() {
				called.resolve(1);

				return inboundMessageProcessor;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", "serial/test");

		ServiceRegistration<InboundMessageProcessorFactory> serviceRegistration =
			bundleContext.registerService(
				InboundMessageProcessorFactory.class, factory, properties);

		try {
			tbBundle.start();

			Promise<Integer> promiseToAfterReceive = afterReceive.getPromise();

			assertFalse(promiseToAfterReceive.isDone());

			Promise<Integer> promiseToAfterThread = afterThread.getPromise();

			assertFalse(promiseToAfterThread.isDone());

			Promise<Integer> promiseToBeforeReceive = beforeReceive.getPromise();

			assertFalse(promiseToBeforeReceive.isDone());

			Promise<Integer> promiseToBeforeThread = beforeThread.getPromise();

			assertFalse(promiseToBeforeThread.isDone());

			Message message = new Message();

			MessageBus messageBus = getMessageBus();

			Promise<Integer> promiseToCalled = called.getPromise();

			assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage("serial/test", message);

			assertEquals(new Integer(1), promiseToCalled.getValue());
			assertEquals(new Integer(2), promiseToBeforeReceive.getValue());
			assertEquals(new Integer(3), promiseToBeforeThread.getValue());
			assertEquals(new Integer(4), promiseToAfterThread.getValue());
			assertEquals(new Integer(5), promiseToAfterReceive.getValue());

			tbBundle.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testSynchronous() throws Exception {
		Bundle tbBundle = install("tb1.jar");

		final Deferred<Integer> afterReceive = new Deferred<>();
		final Deferred<Integer> afterThread = new Deferred<>();
		final Deferred<Integer> beforeReceive = new Deferred<>();
		final Deferred<Integer> beforeThread = new Deferred<>();
		final Deferred<Integer> called = new Deferred<>();

		InboundMessageProcessor inboundMessageProcessor = new InboundMessageProcessor() {

			@Override
			public void afterReceive(Message message) throws MessageProcessorException {
				afterReceive.resolve(5);
			}

			@Override
			public void afterThread(Message message, Thread dispatchThread) throws MessageProcessorException {
				afterThread.resolve(4);
			}

			@Override
			public Message beforeReceive(Message message) throws MessageProcessorException {
				beforeReceive.resolve(2);

				return message;
			}

			@Override
			public Message beforeThread(Message message, Thread dispatchThread) throws MessageProcessorException {
				beforeThread.resolve(3);

				return message;
			}

		};

		InboundMessageProcessorFactory factory = new InboundMessageProcessorFactory() {

			@Override
			public InboundMessageProcessor create() {
				called.resolve(1);

				return inboundMessageProcessor;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", "synchronous/test");

		ServiceRegistration<InboundMessageProcessorFactory> serviceRegistration =
			bundleContext.registerService(
				InboundMessageProcessorFactory.class, factory, properties);

		try {
			tbBundle.start();

			Promise<Integer> promiseToAfterReceive = afterReceive.getPromise();

			assertFalse(promiseToAfterReceive.isDone());

			Promise<Integer> promiseToAfterThread = afterThread.getPromise();

			assertFalse(promiseToAfterThread.isDone());

			Promise<Integer> promiseToBeforeReceive = beforeReceive.getPromise();

			assertFalse(promiseToBeforeReceive.isDone());

			Promise<Integer> promiseToBeforeThread = beforeThread.getPromise();

			assertFalse(promiseToBeforeThread.isDone());

			Message message = new Message();

			MessageBus messageBus = getMessageBus();

			Promise<Integer> promiseToCalled = called.getPromise();

			assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage("synchronous/test", message);

			assertEquals(new Integer(1), promiseToCalled.getValue());
			assertEquals(new Integer(2), promiseToBeforeReceive.getValue());

			// Because it's a synchronous destination there's no thread
			// dispatching done.

			assertFalse(promiseToBeforeThread.isDone());
			assertFalse(promiseToAfterThread.isDone());

			assertEquals(new Integer(5), promiseToAfterReceive.getValue());

			tbBundle.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
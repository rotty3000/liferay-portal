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

import com.liferay.petra.messaging.api.InboundMessageProcessor;
import com.liferay.petra.messaging.api.InboundMessageProcessorFactory;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageProcessorException;
import com.liferay.petra.messaging.spi.MessageImpl;
import com.liferay.petra.messaging.test.tb1.TBSynchronousDestination;
import com.liferay.petra.messaging.test.tb2.TBParallelDestination;
import com.liferay.petra.messaging.test.tb3.TBSerialDestination;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
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
		test("tb2.jar", TBParallelDestination.DESTINATION_NAME);
	}

	@Test
	public void testSerial() throws Exception {
		test("tb3.jar", TBSerialDestination.DESTINATION_NAME);
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb1.jar", TBSynchronousDestination.DESTINATION_NAME);
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

			Assert.assertFalse(promiseToAfterReceive.isDone());

			Promise<Integer> promiseToAfterThread = afterThread.getPromise();

			Assert.assertFalse(promiseToAfterThread.isDone());

			Promise<Integer> promiseToBeforeReceive =
				beforeReceive.getPromise();

			Assert.assertFalse(promiseToBeforeReceive.isDone());

			Promise<Integer> promiseToBeforeThread = beforeThread.getPromise();

			Assert.assertFalse(promiseToBeforeThread.isDone());

			Message message = new MessageImpl();

			Promise<Integer> promiseToCalled = called.getPromise();

			Assert.assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage(destinationName, message);

			Assert.assertEquals(Integer.valueOf(1), promiseToCalled.getValue());
			Assert.assertEquals(
				Integer.valueOf(2), promiseToBeforeReceive.getValue());
			Assert.assertEquals(
				Integer.valueOf(3), promiseToBeforeThread.getValue());
			Assert.assertEquals(
				Integer.valueOf(4), promiseToAfterThread.getValue());
			Assert.assertEquals(
				Integer.valueOf(5), promiseToAfterReceive.getValue());

			tb.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
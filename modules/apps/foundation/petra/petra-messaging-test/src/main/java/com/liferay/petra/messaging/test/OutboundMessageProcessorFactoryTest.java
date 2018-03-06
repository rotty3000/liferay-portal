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

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageProcessorException;
import com.liferay.petra.messaging.api.OutboundMessageProcessor;
import com.liferay.petra.messaging.api.OutboundMessageProcessorFactory;
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
public class OutboundMessageProcessorFactoryTest extends TestUtil {

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

	protected void test(String bundle, String destination) throws Exception {
		Bundle tb = install(bundle);

		final Deferred<Integer> afterSend = new Deferred<>();
		final Deferred<Integer> beforeSend = new Deferred<>();
		final Deferred<Integer> called = new Deferred<>();

		OutboundMessageProcessor outboundMessageProcessor =
			new OutboundMessageProcessor() {

				@Override
				public void afterSend(Message message)
					throws MessageProcessorException {

					afterSend.resolve(3);
				}

				@Override
				public Message beforeSend(Message message)
					throws MessageProcessorException {

					beforeSend.resolve(2);

					return message;
				}

			};

		OutboundMessageProcessorFactory factory =
			new OutboundMessageProcessorFactory() {

				@Override
				public OutboundMessageProcessor create() {
					called.resolve(1);

					return outboundMessageProcessor;
				}

			};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", destination);

		ServiceRegistration<OutboundMessageProcessorFactory>
			serviceRegistration = bundleContext.registerService(
				OutboundMessageProcessorFactory.class, factory, properties);

		try {
			tb.start();

			Promise<Integer> promiseToAfterSend = afterSend.getPromise();

			Assert.assertFalse(promiseToAfterSend.isDone());

			Promise<Integer> promiseToBeforeSend = beforeSend.getPromise();

			Assert.assertFalse(promiseToBeforeSend.isDone());

			Message message = new MessageImpl();

			Promise<Integer> promiseToCalled = called.getPromise();

			Assert.assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage(destination, message);

			Assert.assertEquals(Integer.valueOf(1), promiseToCalled.getValue());
			Assert.assertEquals(
				Integer.valueOf(2), promiseToBeforeSend.getValue());
			Assert.assertEquals(
				Integer.valueOf(3), promiseToAfterSend.getValue());

			tb.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
package com.liferay.messaging.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageProcessorException;
import com.liferay.messaging.OutboundMessageProcessor;
import com.liferay.messaging.OutboundMessageProcessorFactory;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public class OutboundMessageProcessorFactoryTest extends TestUtil {

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

	protected void test(String bundle, String destination) throws Exception {
		Bundle tbBundle = install(bundle);

		final Deferred<Integer> afterSend = new Deferred<>();
		final Deferred<Integer> beforeSend = new Deferred<>();
		final Deferred<Integer> called = new Deferred<>();

		OutboundMessageProcessor outboundMessageProcessor = new OutboundMessageProcessor() {

			@Override
			public void afterSend(Message message) throws MessageProcessorException {
				afterSend.resolve(3);
			}

			@Override
			public Message beforeSend(Message message) throws MessageProcessorException {
				beforeSend.resolve(2);

				return message;
			}

		};

		OutboundMessageProcessorFactory factory = new OutboundMessageProcessorFactory() {

			@Override
			public OutboundMessageProcessor create() {
				called.resolve(1);

				return outboundMessageProcessor;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", destination);

		ServiceRegistration<OutboundMessageProcessorFactory> serviceRegistration =
			bundleContext.registerService(
				OutboundMessageProcessorFactory.class, factory, properties);

		try {
			tbBundle.start();

			Promise<Integer> promiseToAfterSend = afterSend.getPromise();

			assertFalse(promiseToAfterSend.isDone());

			Promise<Integer> promiseToBeforeSend = beforeSend.getPromise();

			assertFalse(promiseToBeforeSend.isDone());

			Message message = new Message();

			MessageBus messageBus = getMessageBus();

			Promise<Integer> promiseToCalled = called.getPromise();

			assertFalse(promiseToCalled.isDone());

			messageBus.sendMessage(destination, message);

			assertEquals(new Integer(1), promiseToCalled.getValue());
			assertEquals(new Integer(2), promiseToBeforeSend.getValue());
			assertEquals(new Integer(3), promiseToAfterSend.getValue());

			tbBundle.uninstall();
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
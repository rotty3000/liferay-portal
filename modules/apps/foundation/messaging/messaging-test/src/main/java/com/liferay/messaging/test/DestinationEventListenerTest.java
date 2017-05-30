package com.liferay.messaging.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.liferay.messaging.DestinationEventListener;
import com.liferay.messaging.MessageListener;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public class DestinationEventListenerTest extends TestUtil {

	@Test
	public void testBasic() throws Exception {
		Bundle tb1Bundle = install("tb1.jar");

		final Deferred<MessageListener> registeration = new Deferred<>();
		final Deferred<MessageListener> unregisteration = new Deferred<>();

		DestinationEventListener listener = new DestinationEventListener() {

			@Override
			public void messageListenerRegistered(
				String destinationName, MessageListener messageListener) {

				registeration.resolve(messageListener);
			}

			@Override
			public void messageListenerUnregistered(
				String destinationName, MessageListener messageListener) {

				unregisteration.resolve(messageListener);
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", "synchronous/test");

		ServiceRegistration<DestinationEventListener> serviceRegistration =
			bundleContext.registerService(
				DestinationEventListener.class, listener, properties);

		try {
			Promise<MessageListener> promiseToRegister = registeration.getPromise();

			assertFalse(promiseToRegister.isDone());

			tb1Bundle.start();

			assertNotNull(promiseToRegister.getValue());

			Promise<MessageListener> promiseToUnregister = unregisteration.getPromise();

			assertFalse(promiseToUnregister.isDone());

			tb1Bundle.uninstall();

			assertNotNull(promiseToUnregister.getValue());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
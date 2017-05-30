package com.liferay.messaging.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.liferay.messaging.Destination;
import com.liferay.messaging.MessageBusEventListener;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public class MessageBusEventListenerTest extends TestUtil {

	@Test
	public void testBasic() throws Exception {
		Bundle tb1Bundle = install("tb1.jar");

		final Deferred<Destination> registeration = new Deferred<>();
		final Deferred<Destination> unregisteration = new Deferred<>();

		MessageBusEventListener listener = new MessageBusEventListener() {

			@Override
			public void destinationAdded(Destination destination) {
				registeration.resolve(destination);
			}

			@Override
			public void destinationRemoved(Destination destination) {
				unregisteration.resolve(destination);
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", "synchronous/test");

		ServiceRegistration<MessageBusEventListener> serviceRegistration =
			bundleContext.registerService(
				MessageBusEventListener.class, listener, properties);

		try {
			Promise<Destination> promiseToRegister = registeration.getPromise();

			assertFalse(promiseToRegister.isDone());

			tb1Bundle.start();

			assertNotNull(promiseToRegister.getValue());

			Promise<Destination> promiseToUnregister = unregisteration.getPromise();

			assertFalse(promiseToUnregister.isDone());

			tb1Bundle.uninstall();

			assertNotNull(promiseToUnregister.getValue());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
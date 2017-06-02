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

/**
 * @author Raymond Augé
 */
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
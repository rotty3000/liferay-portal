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

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.MessageBusEventListener;
import com.liferay.petra.messaging.test.tb1.TBSynchronousDestination;

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
public class MessageBusEventListenerTest extends TestUtil {

	@Test
	public void testBasic() throws Exception {
		Bundle tb1Bundle = install("tb1.jar");

		final Deferred<Destination> registration = new Deferred<>();
		final Deferred<Destination> unregistration = new Deferred<>();

		MessageBusEventListener listener = new MessageBusEventListener() {

			@Override
			public void destinationAdded(Destination destination) {
				registration.resolve(destination);
			}

			@Override
			public void destinationRemoved(Destination destination) {
				unregistration.resolve(destination);
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("destination.name", TBSynchronousDestination.DESTINATION_NAME);

		ServiceRegistration<MessageBusEventListener> serviceRegistration =
			bundleContext.registerService(
				MessageBusEventListener.class, listener, properties);

		try {
			Promise<Destination> promiseToRegister = registration.getPromise();

			tb1Bundle.start();

			Assert.assertNotNull(promiseToRegister.getValue());

			Promise<Destination> promiseToUnregister =
				unregistration.getPromise();

			Assert.assertFalse(promiseToUnregister.isDone());

			tb1Bundle.uninstall();

			Assert.assertNotNull(promiseToUnregister.getValue());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}
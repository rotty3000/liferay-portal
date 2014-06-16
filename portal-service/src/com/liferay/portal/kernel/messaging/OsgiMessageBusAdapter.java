/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.messaging;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Michael C. Han
 */
public class OsgiMessageBusAdapter implements MessageBus {

	public void afterPropertiesSet() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			MessageBus.class,
			new MessageBusServiceTrackerCustomizer());

		_serviceTracker.open();

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Waiting for message bus registration");
			}

			if (_serviceTracker.isEmpty()) {
				_serviceTracker.waitForService(100);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Registered message bus");
			}
		}
		catch (InterruptedException ie) {
			if (_log.isDebugEnabled()) {
				_log.debug("Interrupted message bus registration", ie);
			}
		}

	}

	@Override
	public void addDestination(Destination destination) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.addDestination(destination);
	}

	@Override
	public void addDestinationEventListener(
		DestinationEventListener destinationEventListener) {

		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.addDestinationEventListener(destinationEventListener);
	}

	@Override
	public void addDestinationEventListener(
		String destinationName,
		DestinationEventListener destinationEventListener) {

		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.addDestinationEventListener(
			destinationName, destinationEventListener);
	}

	@Override
	public Destination getDestination(String destinationName) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return null;
		}

		return _messageBus.getDestination(destinationName);
	}

	@Override
	public int getDestinationCount() {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return 0;
		}

		return _messageBus.getDestinationCount();
	}

	@Override
	public Collection<String> getDestinationNames() {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return Collections.emptyList();
		}

		return _messageBus.getDestinationNames();
	}

	@Override
	public Collection<Destination> getDestinations() {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return Collections.emptyList();
		}

		return _messageBus.getDestinations();
	}

	@Override
	public boolean hasDestination(String destinationName) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return false;
		}

		return _messageBus.hasDestination(destinationName);
	}

	@Override
	public boolean hasMessageListener(String destinationName) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return false;
		}

		return _messageBus.hasMessageListener(destinationName);
	}

	@Override
	public boolean registerMessageListener(
		String destinationName, MessageListener messageListener) {

		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return false;
		}

		return _messageBus.registerMessageListener(
			destinationName, messageListener);
	}

	@Override
	public Destination removeDestination(String destinationName) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return null;
		}

		return _messageBus.removeDestination(destinationName);
	}

	@Override
	public void removeDestinationEventListener(
		DestinationEventListener destinationEventListener) {

		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.removeDestinationEventListener(destinationEventListener);
	}

	@Override
	public void removeDestinationEventListener(
		String destinationName,
		DestinationEventListener destinationEventListener) {

		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.removeDestinationEventListener(
			destinationName, destinationEventListener);
	}

	@Override
	public void replace(Destination destination) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.replace(destination);
	}

	@Override
	public void sendMessage(String destinationName, Message message) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.sendMessage(destinationName, message);
	}

	@Override
	public void shutdown() {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.shutdown();
	}

	@Override
	public void shutdown(boolean force) {
		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return;
		}

		_messageBus.shutdown(force);
	}

	@Override
	public boolean unregisterMessageListener(
		String destinationName, MessageListener messageListener) {

		if (_messageBus == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Message bus not initialized");
			}

			return false;
		}

		return _messageBus.unregisterMessageListener(
			destinationName, messageListener);
	}

	private static Log _log = LogFactoryUtil.getLog(OsgiMessageBusAdapter.class);
	
	private MessageBus _messageBus;
	private ServiceTracker<MessageBus, MessageBus> _serviceTracker;

	private class MessageBusServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<MessageBus, MessageBus> {
		@Override
		public MessageBus addingService(
			ServiceReference<MessageBus> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			_messageBus = registry.getService(serviceReference);

			return _messageBus;
		}

		@Override
		public void modifiedService(
			ServiceReference<MessageBus> serviceReference, MessageBus service) {

			Registry registry = RegistryUtil.getRegistry();

			_messageBus = registry.getService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<MessageBus> serviceReference, MessageBus service) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			_messageBus = null;
		}
	}

}

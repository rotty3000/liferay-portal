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

package com.liferay.messaging.config;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationEventListener;
import com.liferay.messaging.DestinationFactory;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageBusEventListener;
import com.liferay.messaging.MessageListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public abstract class AbstractMessagingConfigurator
	implements MessagingConfigurator {

	public static final String NULL = "null";

	public void afterPropertiesSet() {
	}

	@Override
	public void connect() {
	}

	@Override
	public void destroy() {
		_destinationConfigurations.clear();
		_destinationEventListeners.clear();
		_messageListeners.clear();

		for (Destination destination : _destinations) {
			destination.destroy();
		}

		_destinations.clear();
		_messageBusEventListeners.clear();

		MessagingConfiguratorRegistry.unregisterMessagingConfigurator(
			"" /* TODO */, this);
	}

	@Override
	public void disconnect() {
		for (Map.Entry<String, List<MessageListener>> messageListeners :
				_messageListeners.entrySet()) {

			String destinationName = messageListeners.getKey();

			for (MessageListener messageListener :
					messageListeners.getValue()) {

				_messageBus.unregisterMessageListener(
					destinationName, messageListener);
			}
		}
	}

	@Override
	public void setDestinationConfigurations(
		Set<DestinationConfiguration> destinationConfigurations) {

		_destinationConfigurations.addAll(destinationConfigurations);
	}

	@Override
	public void setDestinationEventListeners(
		Map<String, List<DestinationEventListener>> destinationEventListeners) {

		_destinationEventListeners.putAll(destinationEventListeners);
	}

	@Override
	public void setDestinations(List<Destination> destinations) {
		_destinations.addAll(destinations);
	}

	@Override
	public void setMessageBusEventListeners(
		List<MessageBusEventListener> messageBusEventListeners) {

		_messageBusEventListeners.addAll(messageBusEventListeners);
	}

	@Override
	public void setMessageListeners(
		Map<String, List<MessageListener>> messageListeners) {

		for (List<MessageListener> messageListenersList :
				messageListeners.values()) {

			for (MessageListener messageListener : messageListenersList) {
				Class<?> messageListenerClass = messageListener.getClass();

				try {
					Method setMessageBusMethod = messageListenerClass.getMethod(
						"setMessageBus", MessageBus.class);

					setMessageBusMethod.setAccessible(true);

					setMessageBusMethod.invoke(messageListener, _messageBus);

					continue;
				}
				catch (Exception e) {
				}

				try {
					Method setMessageBusMethod =
						messageListenerClass.getDeclaredMethod(
							"setMessageBus", MessageBus.class);

					setMessageBusMethod.setAccessible(true);

					setMessageBusMethod.invoke(messageListener, _messageBus);
				}
				catch (Exception e) {
				}
			}
		}

		_messageListeners.putAll(messageListeners);
	}

	/**
	 * @param      replacementDestinations
	 * @deprecated As of 1.0.0, replaced by {@link #setDestinations(List)}
	 */
	@Deprecated
	@Override
	public void setReplacementDestinations(
		List<Destination> replacementDestinations) {

		_destinations.addAll(replacementDestinations);
	}

	protected abstract ClassLoader getOperatingClassloader();

	protected void initialize() {
		registerMessageBusEventListeners();

		registerDestinations();

		registerDestinationEventListeners();

		connect();

		String servletContextName = ""; // TODO

		if ((servletContextName != null) &&
			!servletContextName.equals(NULL)) {

			MessagingConfiguratorRegistry.registerMessagingConfigurator(
				servletContextName, this);
		}
	}

	protected void registerDestinationEventListeners() {
		if (_destinationEventListeners.isEmpty()) {
			return;
		}
	}

	protected void registerDestinations() {
		for (DestinationConfiguration destinationConfiguration :
				_destinationConfigurations) {

			_destinations.add(
				_destinationFactory.createDestination(
					destinationConfiguration));
		}

		if (_destinations.isEmpty()) {
			return;
		}
	}

	protected void registerMessageBusEventListeners() {
		if (_messageBusEventListeners.isEmpty()) {
			return;
		}
	}

	private final Set<DestinationConfiguration> _destinationConfigurations =
		new HashSet<>();
	private final Map<String, List<DestinationEventListener>>
		_destinationEventListeners = new HashMap<>();
	private volatile DestinationFactory _destinationFactory;
	private final List<Destination> _destinations = new ArrayList<>();
	private volatile MessageBus _messageBus;
	private final List<MessageBusEventListener> _messageBusEventListeners =
		new ArrayList<>();
	private final Map<String, List<MessageListener>> _messageListeners =
		new HashMap<>();

}
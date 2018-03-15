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

package com.liferay.portal.messaging.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseAsyncDestination;
import com.liferay.portal.kernel.messaging.BaseDestination;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationEventListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusEventListener;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true,
	property = Constants.SERVICE_PID + "=com.liferay.portal.messaging.configuration.DestinationWorkerConfiguration",
	service = {ManagedServiceFactory.class, MessageBus.class}
)
public class DefaultMessageBus implements ManagedServiceFactory, MessageBus {

	@Override
	public synchronized void addDestination(Destination destination) {
		Dictionary<String, Object> properties = new HashMapDictionary<>();
		properties.put("destination.name", destination.getName());

		BundleContext bundleContext = getBundleContext();

		ServiceRegistration<com.liferay.petra.messaging.api.Destination> serviceRegistration =
			bundleContext.registerService(
				com.liferay.petra.messaging.api.Destination.class, destination,
				properties);

		_destinations.put(
			destination.getName(),
			new AbstractMap.SimpleImmutableEntry<>(
				destination, serviceRegistration));

		destination.open();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "removeMessageBusEventListener"
	)
	public boolean addMessageBusEventListener(
		MessageBusEventListener messageBusEventListener) {

		BundleContext bundleContext = getBundleContext();

		ServiceRegistration<com.liferay.petra.messaging.api.MessageBusEventListener>
			serviceRegistration = bundleContext.registerService(
			com.liferay.petra.messaging.api.MessageBusEventListener.class,
			messageBusEventListener, null);

		boolean registration = _messageBusEventListeners.add(
			new AbstractMap.SimpleImmutableEntry<>(messageBusEventListener,
				serviceRegistration));

		return registration;
	}

	@Override
	public void deleted(String factoryPid) {
		String destinationName = _factoryPidsToDestinationName.remove(
			factoryPid);

		_destinationWorkerConfigurations.remove(destinationName);
	}

	@Override
	public Destination getDestination(String destinationName) {
		return Optional.ofNullable(
			_destinations.get(destinationName)
		).map(
			entry -> entry.getKey()
		).orElse(
			null
		);
	}

	@Override
	public int getDestinationCount() {
		return _destinations.size();
	}

	@Override
	public Collection<String> getDestinationNames() {
		return _destinations.keySet();
	}

	@Override
	public Collection<Destination> getDestinations() {
		return _destinations.values().stream().map(
			entry -> entry.getKey()
		).collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return "Default Message Bus";
	}

	@Override
	public boolean hasDestination(String destinationName) {
		return _destinations.containsKey(destinationName);
	}

	@Override
	public boolean hasMessageListener(String destinationName) {
		return _messageListeners.stream().filter(
			entry -> destinationName.equals(entry.getValue().getReference().getProperty("destination.name"))
		).findFirst().isPresent();
	}

	@Override
	public synchronized boolean registerMessageListener(
		String destinationName, MessageListener messageListener) {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("destination.name", destinationName);

		ServiceRegistration<com.liferay.petra.messaging.api.MessageListener> serviceRegistration =
			getBundleContext().registerService(
				com.liferay.petra.messaging.api.MessageListener.class,
			messageListener, properties);

		_messageListeners.add(
			new AbstractMap.SimpleEntry<>(
				messageListener, serviceRegistration));

		return true;
	}

	@Override
	public Destination removeDestination(String destinationName) {
		return removeDestination(destinationName, true);
	}

	@Override
	public synchronized Destination removeDestination(
		String destinationName, boolean closeOnRemove) {

		Entry<Destination, ServiceRegistration<com.liferay.petra.messaging.api.Destination>> entry =
			_destinations.remove(destinationName);

		if (entry == null) {
			return null;
		}

		entry.getValue().unregister();

		Destination destination = entry.getKey();

		if (closeOnRemove) {
			destination.close(true);
		}

		destination.removeDestinationEventListeners();

		destination.unregisterMessageListeners();

		return destination;
	}

	@Override
	public boolean removeMessageBusEventListener(
		MessageBusEventListener messageBusEventListener) {

		return _messageBusEventListeners.removeIf(
			entry -> {
				if (entry.getKey().equals(messageBusEventListener)) {
					entry.getValue().unregister();

					return true;
				}

				return false;
			});
	}

	@Override
	public void replace(Destination destination) {
		replace(destination, true);
	}

	@Override
	public synchronized void replace(
		Destination destination, boolean closeOnRemove) {

		Destination oldDestination = getDestination(destination.getName());

		oldDestination.copyDestinationEventListeners(destination);
		oldDestination.copyMessageListeners(destination);

		removeDestination(oldDestination.getName(), closeOnRemove);

		addDestination(destination);
	}

	@Override
	public void sendMessage(String destinationName, Message message) {
		_messageBus.sendMessage(destinationName, message);
	}

	@Override
	public void shutdown() {
		shutdown(false);
	}

	@Override
	public synchronized void shutdown(boolean force) {
		_destinations.values().stream().forEach(entry -> entry.getKey().close(force));
	}

	@Override
	public synchronized boolean unregisterMessageListener(
		String destinationName, MessageListener messageListener) {

		return _messageListeners.removeIf(
			entry -> {
				if (entry.getKey().equals(messageListener)) {
					entry.getValue().unregister();

					return true;
				}

				return false;
			});
	}

	@Override
	public void updated(String factoryPid, Dictionary<String, ?> dictionary)
		throws ConfigurationException {

		DestinationWorkerConfiguration destinationWorkerConfiguration =
			ConfigurableUtil.createConfigurable(
				DestinationWorkerConfiguration.class, dictionary);

		_factoryPidsToDestinationName.put(
			factoryPid, destinationWorkerConfiguration.destinationName());

		_destinationWorkerConfigurations.put(
			destinationWorkerConfiguration.destinationName(),
			destinationWorkerConfiguration);

		Destination destination = getDestination(
			destinationWorkerConfiguration.destinationName());

		updateDestination(destination, destinationWorkerConfiguration);
	}

	@Deactivate
	protected void deactivate() {
		shutdown(true);

		for (Entry<Destination, ServiceRegistration<com.liferay.petra.messaging.api.Destination>> entry :
				_destinations.values()) {

			entry.getValue().unregister();
			entry.getKey().destroy();
		}

		_messageBusEventListeners.clear();

		_destinations.clear();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)", unbind = "unregisterDestination"
	)
	protected synchronized void registerDestination(
		Destination destination, Map<String, Object> properties) {

		String destinationName = MapUtil.getString(
			properties, "destination.name");

		if (BaseDestination.class.isInstance(destination)) {
			BaseDestination baseDestination = (BaseDestination)destination;

			baseDestination.setName(destinationName);
			baseDestination.afterPropertiesSet();
		}

		if (_destinations.containsKey(destination.getName())) {
			replace(destination);
		}
		else {
			addDestination(destination);
		}

		DestinationWorkerConfiguration destinationWorkerConfiguration =
			_destinationWorkerConfigurations.get(destinationName);

		updateDestination(destination, destinationWorkerConfiguration);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)",
		unbind = "unregisterDestinationEventListener"
	)
	protected synchronized void registerDestinationEventListener(
		DestinationEventListener destinationEventListener,
		Map<String, Object> properties) {

		String destinationName = MapUtil.getString(
			properties, "destination.name");

		Destination destination = getDestination(destinationName);

		if (destination == null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to unregister destination event listener for " +
						destinationName);
			}

			return;
		}

		BundleContext bundleContext = getBundleContext();

		Dictionary<String, Object> dictionaryProperties = new Hashtable<>();

		dictionaryProperties.put("destination.name", destinationName);

		ServiceRegistration<com.liferay.petra.messaging.api.DestinationEventListener>
			serviceRegistration = bundleContext.registerService(
			com.liferay.petra.messaging.api.DestinationEventListener.class,
			destinationEventListener, dictionaryProperties);

		_destinationEventListeners.add(new AbstractMap.SimpleImmutableEntry<>(
			destinationEventListener, serviceRegistration));
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)", unbind = "unregisterMessageListener"
	)
	protected synchronized void registerMessageListener(
		MessageListener messageListener, Map<String, Object> properties) {

		String destinationName = MapUtil.getString(
			properties, "destination.name");

		registerMessageListener(destinationName, messageListener);
	}

	protected synchronized void unregisterDestination(
		Destination destination, Map<String, Object> properties) {

		removeDestination(destination.getName());

		destination.destroy();
	}

	protected synchronized void unregisterDestinationEventListener(
		DestinationEventListener destinationEventListener,
		Map<String, Object> properties) {

		String destinationName = MapUtil.getString(
			properties, "destination.name");

		Destination destination = getDestination(destinationName);

		if (destination == null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Unable to unregister destination event listener for " +
						destinationName);
			}

			return;
		}

		_destinationEventListeners.removeIf(
			entry -> {
				if (entry.getKey().equals(destinationEventListener)) {
					entry.getValue().unregister();

					return true;
				}

				return false;
			});
	}

	protected synchronized void unregisterMessageListener(
		MessageListener messageListener, Map<String, Object> properties) {

		String destinationName = MapUtil.getString(
			properties, "destination.name");

		unregisterMessageListener(destinationName, messageListener);
	}

	protected void updateDestination(
		Destination destination,
		DestinationWorkerConfiguration destinationWorkerConfiguration) {

		if ((destination == null) || (destinationWorkerConfiguration == null)) {
			return;
		}

		if (destination instanceof BaseAsyncDestination) {
			BaseAsyncDestination baseAsyncDestination =
				(BaseAsyncDestination)destination;

			baseAsyncDestination.setMaximumQueueSize(
				destinationWorkerConfiguration.maxQueueSize());
			baseAsyncDestination.setWorkersCoreSize(
				destinationWorkerConfiguration.workerCoreSize());
			baseAsyncDestination.setWorkersMaxSize(
				destinationWorkerConfiguration.workerMaxSize());
		}
	}

	private BundleContext getBundleContext() {
		BundleContext bundleContext = FrameworkUtil.getBundle(
			getClass()).getBundleContext();

		return bundleContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultMessageBus.class);

	private final Set<Entry<DestinationEventListener,
		ServiceRegistration<com.liferay.petra.messaging.api.DestinationEventListener>>>
		_destinationEventListeners = Collections.newSetFromMap(
			new ConcurrentHashMap<>());
	private final Map<String, Entry<Destination,
		ServiceRegistration<com.liferay.petra.messaging.api.Destination>>>
		_destinations = new HashMap<>();
	private final Map<String, DestinationWorkerConfiguration>
		_destinationWorkerConfigurations = new ConcurrentHashMap<>();
	private final Map<String, String> _factoryPidsToDestinationName =
		new ConcurrentHashMap<>();

	@Reference
	private com.liferay.petra.messaging.api.MessageBus _messageBus;

	private final Set<Entry<MessageBusEventListener,
		ServiceRegistration<com.liferay.petra.messaging.api.MessageBusEventListener>>>
		_messageBusEventListeners = Collections.newSetFromMap(
			new ConcurrentHashMap<>());
	private final List<Entry<MessageListener, ServiceRegistration<com.liferay.petra.messaging.api.MessageListener>>>
		_messageListeners = new CopyOnWriteArrayList<>();

}
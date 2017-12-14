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

package com.liferay.petra.messaging.impl.internal;

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.DestinationType;
import com.liferay.petra.messaging.spi.DestinationFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(service = DestinationFactory.class)
public class DefaultDestinationFactory implements DestinationFactory {

	@Override
	public Destination createDestination(
		DestinationConfiguration destinationConfiguration,
		Map<String, Object> properties) {

		String destinationName = destinationConfiguration.getDestinationName();
		DestinationType destinationType =
			destinationConfiguration.getDestinationType();

		ComponentFactory componentFactory;

		switch (destinationType) {
			case PARALLEL:
				componentFactory = _parallelDestinationFactory;
				break;
			case SERIAL:
				componentFactory = _serialDestinationFactory;
				break;
			case SYNCHRONOUS:
				componentFactory = _synchronousDestinationFactory;
				break;
			default:
				componentFactory = _parallelDestinationFactory;
				break;
		}

		String targetFilter = String.format(
			CLMIConstants.FORMAT_DESTINATION_NAME_FILTER, destinationName);

		Dictionary<String, Object> dictionary = new Hashtable<>(properties);

		dictionary.put("destination.name", destinationName);
		dictionary.put("DestinationEventListener.target", targetFilter);
		dictionary.put("InboundMessageProcessorFactory.target", targetFilter);
		dictionary.put("MessageListener.target", targetFilter);
		dictionary.put("OutboundMessageProcessorFactory.target", targetFilter);

		if (destinationType != DestinationType.SYNCHRONOUS) {
			dictionary.put("ExecutorServiceRegistrar.target", targetFilter);
			dictionary.put("RejectedExecutionHandler.target", targetFilter);
		}

		ComponentInstance componentInstance = componentFactory.newInstance(
			dictionary);

		Destination destination = (Destination)componentInstance.getInstance();

		_instances.put(destination, componentInstance);

		return destination;
	}

	@Override
	public void dispose(Destination destination) {
		ComponentInstance componentInstance = _instances.remove(destination);

		if (componentInstance != null) {
			componentInstance.dispose();
		}
	}

	private final Map<Destination, ComponentInstance> _instances =
		new ConcurrentHashMap<>();

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(component.factory=parallel.destination)"
	)
	private ComponentFactory _parallelDestinationFactory;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(component.factory=serial.destination)"
	)
	private ComponentFactory _serialDestinationFactory;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(component.factory=synchronous.destination)"
	)
	private ComponentFactory _synchronousDestinationFactory;

}
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

package com.liferay.portal.messaging.internal.jmx;

import com.liferay.messaging.Destination;
import com.liferay.messaging.MessageBus;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 */
@Component(
	immediate = true,
	property = {
		"jmx.objectname=com.liferay.portal.messaging:classification=message_bus,name=MessageBusManager",
		"jmx.objectname.cache.key=MessageBusManager"
	},
	service = DynamicMBean.class
)
public class MessageBusManager
	extends StandardMBean implements MessageBusManagerMBean {

	public MessageBusManager() throws NotCompliantMBeanException {
		super(MessageBusManagerMBean.class);
	}

	@Override
	public int getDestinationCount() {
		return _messageBus.getDestinationCount();
	}

	@Override
	public int getMessageListenerCount(String destinationName) {
		Destination destination = _messageBus.getDestination(destinationName);

		if (destination == null) {
			return 0;
		}

		return destination.getMessageListenerCount();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		for (Destination destination : _queuedDestinations) {
			addDestination(destination);
		}

		_queuedDestinations.clear();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(destination.name=*)"
	)
	protected void addDestination(Destination destination) {
		if (_bundleContext == null) {
			_queuedDestinations.add(destination);

			return;
		}

		try {
			DestinationStatisticsManager destinationStatisticsManager =
				new DestinationStatisticsManager(destination);

			Dictionary<String, Object> mBeanProperties =
				new Hashtable<>();

			mBeanProperties.put(
				"jmx.objectname", destinationStatisticsManager.getObjectName());
			mBeanProperties.put(
				"jmx.objectname.cache.key",
				destinationStatisticsManager.getObjectNameCacheKey());

			ServiceRegistration<DynamicMBean> serviceRegistration =
				_bundleContext.registerService(
					DynamicMBean.class, destinationStatisticsManager,
					mBeanProperties);

			_mbeanServiceRegistrations.put(
				destination.getName(), serviceRegistration);
		}
		catch (NotCompliantMBeanException ncmbe) {
			if (_logger.isInfoEnabled()) {
				_logger.info("Unable to register destination mbean", ncmbe);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		_mbeanServiceRegistrations.clear();
	}

	protected void removeDestination(Destination destination) {
		ServiceRegistration<DynamicMBean> mbeanServiceRegistration =
			_mbeanServiceRegistrations.remove(destination.getName());

		if (mbeanServiceRegistration != null) {
			mbeanServiceRegistration.unregister();
		}
	}

	@Reference(unbind = "-")
	protected void setMBeanServer(MBeanServer mBeanServer) {
	}

	@Reference(unbind = "-")
	protected void setMessageBus(MessageBus messageBus) {
		_messageBus = messageBus;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		MessageBusManager.class);

	private BundleContext _bundleContext;
	private final Map<String, ServiceRegistration<DynamicMBean>>
		_mbeanServiceRegistrations = new ConcurrentHashMap<>();
	private MessageBus _messageBus;
	private final Set<Destination> _queuedDestinations =
		ConcurrentHashMap.newKeySet();

}
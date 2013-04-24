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

package com.liferay.portal.service.registry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Raymond Augé
 */
public class ServiceTrackerInvocationHandler<S, T>
	implements InvocationHandler, ServiceTrackerCustomizer<S, T> {

	/**
	 * Instantiates a new ServiceTrackerInvocationHandler.
	 *
	 * @param filter the filter
	 * @param bean the bean
	 */
	public ServiceTrackerInvocationHandler(Filter filter, T bean) {
		_filter = filter;
		_currentService = bean;
		_originalService = bean;

		_serviceTracker = ServiceRegistryUtil.trackServices(filter, this);

		_serviceTracker.open(true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T addingService(ServiceReference<S> serviceReference) {
		_currentService = (T)ServiceRegistryUtil.getService(serviceReference);

		return _currentService;
	}

	/**
	 * Invoke.
	 *
	 * @param proxy the proxy
	 * @param method the method
	 * @param arguments the arguments
	 * @return the object
	 * @throws Throwable the throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] arguments)
		throws Throwable {

		if (_currentService == null) {
			throw new IllegalStateException(
				"The tracker is still waiting for a service matching " +
					_filter.toString());
		}

		try {
			return method.invoke(_currentService, arguments);
		}
		catch (InvocationTargetException ite) {
			throw ite.getCause();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void modifiedService(
		ServiceReference<S> serviceReference, T oldService) {

		_currentService = (T)ServiceRegistryUtil.getService(serviceReference);
	}

	@Override
	public void removedService(
		ServiceReference<S> serviceReference, T service) {

		_currentService = _originalService;
	}

	private T _currentService;
	private Filter _filter;
	private T _originalService;
	private ServiceTracker<S, T> _serviceTracker;

}
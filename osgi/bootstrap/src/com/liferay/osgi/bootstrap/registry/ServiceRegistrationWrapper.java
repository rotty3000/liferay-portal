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

package com.liferay.osgi.bootstrap.registry;

import com.liferay.portal.service.registry.ServiceReference;
import com.liferay.portal.service.registry.ServiceRegistration;

import java.util.Map;

/**
 * @author Raymond Augé
 */
public class ServiceRegistrationWrapper<T> implements ServiceRegistration<T> {

	public ServiceRegistrationWrapper(
		org.osgi.framework.ServiceRegistration<T> serviceRegistration) {

		_serviceRegistration = serviceRegistration;
	}

	public ServiceReference<T> getReference() {
		return new ServiceReferenceWrapper<T>(
			_serviceRegistration.getReference());
	}

	public org.osgi.framework.ServiceRegistration<T> getServiceRegistration() {
		return _serviceRegistration;
	}

	public void setProperties(Map<String, Object> map) {
		_serviceRegistration.setProperties(new MapWrapper(map));
	}

	public void unregister() {
		_serviceRegistration.unregister();
	}

	private org.osgi.framework.ServiceRegistration<T> _serviceRegistration;

}
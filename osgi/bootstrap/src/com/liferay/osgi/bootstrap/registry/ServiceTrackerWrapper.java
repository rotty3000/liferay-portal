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
import com.liferay.portal.service.registry.ServiceTracker;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Raymond Augé
 */
public class ServiceTrackerWrapper<S, T> implements ServiceTracker<S, T> {

	public ServiceTrackerWrapper(
		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker) {

		_serviceTracker = serviceTracker;
	}

	public T addingService(ServiceReference<S> serviceReference) {
		org.osgi.framework.ServiceReference<S> wrappedServiceReference =
			getServiceReference(serviceReference);

		return _serviceTracker.addingService(wrappedServiceReference);
	}

	public void close() {
		_serviceTracker.close();
	}

	@Override
	public boolean equals(Object obj) {
		return _serviceTracker.equals(obj);
	}

	public T getService() {
		return _serviceTracker.getService();
	}

	public T getService(ServiceReference<S> serviceReference) {
		org.osgi.framework.ServiceReference<S> wrappedServiceReference =
			getServiceReference(serviceReference);

		return _serviceTracker.getService(wrappedServiceReference);
	}

	public ServiceReference<S> getServiceReference() {
		return new ServiceReferenceWrapper<S>(
			_serviceTracker.getServiceReference());
	}

	@SuppressWarnings("unchecked")
	public ServiceReference<S>[] getServiceReferences() {
		if (getTrackingCount() == _trackedCount) {
			return _cachedArray;
		}

		org.osgi.framework.ServiceReference<S>[] serviceReferences =
			_serviceTracker.getServiceReferences();

		if (serviceReferences == null) {
			return null;
		}

		ServiceReference<S>[] array =
			new ServiceReference[serviceReferences.length];

		for (int i = 0; i < serviceReferences.length; i++) {
			org.osgi.framework.ServiceReference<S> serviceReference =
				serviceReferences[i];

			array[i] = new ServiceReferenceWrapper<S>(serviceReference);
		}

		_cachedArray = array;
		_trackedCount = getTrackingCount();

		return array;
	}

	public Object[] getServices() {
		return _serviceTracker.getServices();
	}

	public T[] getServices(T[] array) {
		return _serviceTracker.getServices(array);
	}

	public org.osgi.util.tracker.ServiceTracker<S, T> getServiceTracker() {
		return _serviceTracker;
	}

	public SortedMap<ServiceReference<S>, T> getTracked() {
		if (getTrackingCount() == _trackedCount) {
			if (_cachedTrackedMap == null) {
				_cachedTrackedMap = new TreeMap<ServiceReference<S>, T>();
			}

			return _cachedTrackedMap;
		}

		SortedMap<ServiceReference<S>, T> trackedMap =
			new TreeMap<ServiceReference<S>, T>(Collections.reverseOrder());

		SortedMap<org.osgi.framework.ServiceReference<S>, T> curTrackedMap =
			_serviceTracker.getTracked();

		for (Entry<org.osgi.framework.ServiceReference<S>, T> entry :
				curTrackedMap.entrySet()) {

			org.osgi.framework.ServiceReference<S> key = entry.getKey();
			T value = entry.getValue();

			trackedMap.put(new ServiceReferenceWrapper<S>(key), value);
		}

		_cachedTrackedMap = trackedMap;
		_trackedCount = getTrackingCount();

		return trackedMap;
	}

	public int getTrackingCount() {
		return _serviceTracker.getTrackingCount();
	}

	@Override
	public int hashCode() {
		return _serviceTracker.hashCode();
	}

	public boolean isEmpty() {
		return _serviceTracker.isEmpty();
	}

	public void modifiedService(
		ServiceReference<S> serviceReference, T service) {

		org.osgi.framework.ServiceReference<S> wrappedServiceReference =
			getServiceReference(serviceReference);

		_serviceTracker.modifiedService(wrappedServiceReference, service);
	}

	public void open() {
		_serviceTracker.open();
	}

	public void open(boolean trackAllServices) {
		_serviceTracker.open(trackAllServices);
	}

	public void remove(ServiceReference<S> serviceReference) {
		org.osgi.framework.ServiceReference<S> wrappedServiceReference =
			getServiceReference(serviceReference);

		_serviceTracker.remove(wrappedServiceReference);
	}

	public void removedService(
		ServiceReference<S> serviceReference, T service) {

		org.osgi.framework.ServiceReference<S> wrappedServiceReference =
			getServiceReference(serviceReference);

		_serviceTracker.removedService(wrappedServiceReference, service);
	}

	public int size() {
		return _serviceTracker.size();
	}

	@Override
	public String toString() {
		return _serviceTracker.toString();
	}

	public T waitForService(long timeout) throws InterruptedException {
		return _serviceTracker.waitForService(timeout);
	}

	private org.osgi.framework.ServiceReference<S> getServiceReference(
		ServiceReference<S> serviceReference) {

		if (!(serviceReference instanceof ServiceReferenceWrapper)) {
			throw new IllegalArgumentException();
		}

		ServiceReferenceWrapper<S> serviceReferenceWrapper =
			(ServiceReferenceWrapper<S>)serviceReference;

		return serviceReferenceWrapper.getServiceReference();
	}

	private ServiceReference<S>[] _cachedArray;
	private SortedMap<ServiceReference<S>, T> _cachedTrackedMap;
	private org.osgi.util.tracker.ServiceTracker<S, T> _serviceTracker;
	private int _trackedCount;

}
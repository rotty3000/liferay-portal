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

import com.liferay.portal.service.registry.Filter;
import com.liferay.portal.service.registry.ServiceReference;
import com.liferay.portal.service.registry.ServiceRegistration;
import com.liferay.portal.service.registry.ServiceRegistry;
import com.liferay.portal.service.registry.ServiceTracker;
import com.liferay.portal.service.registry.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 */
public class ServiceRegistryImpl implements ServiceRegistry {

	public ServiceRegistryImpl(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	public Filter getFilter(String filterString) throws Exception {
		return new FilterWrapper(_bundleContext.createFilter(filterString));
	}

	public <T> T getService(Class<T> clazz) {
		org.osgi.framework.ServiceReference<T> serviceReference =
			_bundleContext.getServiceReference(clazz);

		return _bundleContext.getService(serviceReference);
	}

	public <T> T getService(ServiceReference<T> serviceReference) {
		if (!(serviceReference instanceof ServiceReferenceWrapper)) {
			throw new IllegalArgumentException();
		}

		ServiceReferenceWrapper<T> serviceReferenceWrapper =
			(ServiceReferenceWrapper<T>)serviceReference;

		return _bundleContext.getService(
			serviceReferenceWrapper.getServiceReference());
	}

	public Object getService(String className) {
		org.osgi.framework.ServiceReference<?> serviceReference =
			_bundleContext.getServiceReference(className);

		return _bundleContext.getService(serviceReference);
	}

	public <T> Collection<T> getServices(Class<T> clazz, String filter)
		throws Exception {

		Collection<org.osgi.framework.ServiceReference<T>> serviceReferences =
			_bundleContext.getServiceReferences(clazz, filter);

		if (serviceReferences.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> services = new ArrayList<T>();

		Iterator<org.osgi.framework.ServiceReference<T>> itr =
			serviceReferences.iterator();

		while (itr.hasNext()) {
			org.osgi.framework.ServiceReference<T> serviceReference =
				itr.next();

			T service = _bundleContext.getService(serviceReference);

			if (service != null) {
				services.add(service);
			}
		}

		return services;
	}

	public <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T service) {

		return registerService(clazz, service, null);
	}

	public <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T service, Map<String, Object> map) {

		org.osgi.framework.ServiceRegistration<T> serviceRegistration =
			_bundleContext.registerService(clazz, service, new MapWrapper(map));

		return new ServiceRegistrationWrapper<T>(serviceRegistration);
	}

	public ServiceRegistration<?> registerService(
		String className, Object service) {

		return registerService(className, service, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServiceRegistration<?> registerService(
		String className, Object service, Map<String, Object> map) {

		org.osgi.framework.ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				className, service, new MapWrapper(map));

		return new ServiceRegistrationWrapper(serviceRegistration);
	}

	public ServiceRegistration<?> registerService(
		String[] classNames, Object service) {

		return registerService(classNames, service, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServiceRegistration<?> registerService(
		String[] classNames, Object service, Map<String, Object> map) {

		org.osgi.framework.ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(
				classNames, service, new MapWrapper(map));

		return new ServiceRegistrationWrapper(serviceRegistration);
	}

	public <S, T> ServiceTracker<S, T> trackServices(Class<S> clazz) {
		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker =
			new org.osgi.util.tracker.ServiceTracker<S, T>(
				_bundleContext, clazz, null);

		return new ServiceTrackerWrapper<S, T>(serviceTracker);
	}

	public <S, T> ServiceTracker<S, T> trackServices(
		Class<S> clazz,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker =
			new org.osgi.util.tracker.ServiceTracker<S, T>(
				_bundleContext, clazz,
				new ServiceTrackerCustomizerAdapter<S, T>(
					serviceTrackerCustomizer));

		return new ServiceTrackerWrapper<S, T>(serviceTracker);
	}

	public <S, T> ServiceTracker<S, T> trackServices(Filter filter) {
		if (!(filter instanceof FilterWrapper)) {
			throw new IllegalArgumentException();
		}

		FilterWrapper filterWrapper = (FilterWrapper)filter;

		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker =
			new org.osgi.util.tracker.ServiceTracker<S, T>(
				_bundleContext, filterWrapper.getFilter(), null);

		return new ServiceTrackerWrapper<S, T>(serviceTracker);
	}

	public <S, T> ServiceTracker<S, T> trackServices(
		Filter filter,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		if (!(filter instanceof FilterWrapper)) {
			throw new IllegalArgumentException();
		}

		FilterWrapper filterWrapper = (FilterWrapper)filter;

		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker =
			new org.osgi.util.tracker.ServiceTracker<S, T>(
				_bundleContext, filterWrapper.getFilter(),
				new ServiceTrackerCustomizerAdapter<S, T>(
					serviceTrackerCustomizer));

		return new ServiceTrackerWrapper<S, T>(serviceTracker);
	}

	public <S, T> ServiceTracker<S, T> trackServices(String className) {
		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker =
			new org.osgi.util.tracker.ServiceTracker<S, T>(
				_bundleContext, className, null);

		return new ServiceTrackerWrapper<S, T>(serviceTracker);
	}

	public <S, T> ServiceTracker<S, T> trackServices(
		String className,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		org.osgi.util.tracker.ServiceTracker<S, T> serviceTracker =
			new org.osgi.util.tracker.ServiceTracker<S, T>(
				_bundleContext, className,
				new ServiceTrackerCustomizerAdapter<S, T>(
					serviceTrackerCustomizer));

		return new ServiceTrackerWrapper<S, T>(serviceTracker);
	}

	private BundleContext _bundleContext;

}
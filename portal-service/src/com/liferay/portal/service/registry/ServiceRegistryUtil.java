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

import java.util.Collection;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class ServiceRegistryUtil {

	public static Filter getFilter(String filterString) throws Exception {
		return _serviceRegistry.getFilter(filterString);
	}

	public static <T> T getService(Class<T> clazz) {
		return _serviceRegistry.getService(clazz);
	}

	public static Object getService(String className) {
		return _serviceRegistry.getService(className);
	}

	public static <S> S getService(ServiceReference<S> serviceReference) {
		return _serviceRegistry.getService(serviceReference);
	}

	public static ServiceRegistry getServiceRegistry() {
		return _serviceRegistry;
	}

	public static <T> Collection<T> getServices(Class<T> clazz, String filter)
		throws Exception {

		return _serviceRegistry.getServices(clazz, filter);
	}

	public static <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T service) {

		return _serviceRegistry.registerService(clazz, service);
	}

	public static <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T service, Map<String, Object> map) {

		return _serviceRegistry.registerService(clazz, service, map);
	}

	public static ServiceRegistration<?> registerService(
		String className, Object service) {

		return _serviceRegistry.registerService(className, service);
	}

	public static ServiceRegistration<?> registerService(
		String className, Object service, Map<String, Object> map) {

		return _serviceRegistry.registerService(className, service, map);
	}

	public static ServiceRegistration<?> registerService(
		String[] classNames, Object service) {

		return _serviceRegistry.registerService(classNames, service);
	}

	public static ServiceRegistration<?> registerService(
		String[] classNames, Object service, Map<String, Object> map) {

		return _serviceRegistry.registerService(classNames, service, map);
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		_serviceRegistry = serviceRegistry;
	}

	@SuppressWarnings("unchecked")
	public static <S, T> ServiceTracker<S, T> trackServices(Class<S> clazz) {
		return (ServiceTracker<S, T>)_serviceRegistry.trackServices(clazz);
	}

	public static <S, T> ServiceTracker<S, T> trackServices(
		Class<S> clazz,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		return _serviceRegistry.trackServices(clazz, serviceTrackerCustomizer);
	}

	@SuppressWarnings("unchecked")
	public static <S, T> ServiceTracker<S, T> trackServices(String className) {
		return (ServiceTracker<S, T>)_serviceRegistry.trackServices(className);
	}

	public static <S, T> ServiceTracker<S, T> trackServices(
		String className,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		return _serviceRegistry.trackServices(
			className, serviceTrackerCustomizer);
	}

	@SuppressWarnings("unchecked")
	public static <S, T> ServiceTracker<S, T> trackServices(Filter filter) {
		return (ServiceTracker<S, T>)_serviceRegistry.trackServices(filter);
	}

	public static <S, T> ServiceTracker<S, T> trackServices(
		Filter filter,
		ServiceTrackerCustomizer<S, T> serviceTrackerCustomizer) {

		return _serviceRegistry.trackServices(filter, serviceTrackerCustomizer);
	}

	private static ServiceRegistry _serviceRegistry;

}
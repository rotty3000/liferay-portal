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

import java.util.SortedMap;

/**
 * @author Raymond Augé
 */
public interface ServiceTracker<S, T> {

	public int hashCode();

	public boolean equals(Object obj);

	public void open();

	public void open(boolean trackAllServices);

	public String toString();

	public void close();

	public T addingService(ServiceReference<S> serviceReference);

	public void modifiedService(
		ServiceReference<S> serviceReference, T service);

	public void removedService(ServiceReference<S> serviceReference, T service);

	public T waitForService(long timeout) throws InterruptedException;

	public ServiceReference<S>[] getServiceReferences();

	public ServiceReference<S> getServiceReference();

	public T getService(ServiceReference<S> serviceReference);

	public Object[] getServices();

	public T getService();

	public void remove(ServiceReference<S> serviceReference);

	public int size();

	public int getTrackingCount();

	public SortedMap<ServiceReference<S>, T> getTracked();

	public boolean isEmpty();

	public T[] getServices(T[] array);

}
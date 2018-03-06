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

package com.liferay.petra.messaging.api;

import java.io.Serializable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;

/**
 * Represents a destination.
 *
 * <p>
 * Clients of the messaging API should create DestinationConfigurations instead
 * of Destinations. When a DestinationConfiguration is registered as a service,
 * a corresponding concrete Destination is registered with the Message Bus.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> When using this as a parent class to a Declarative
 * Services {@code @Cmponent} apply the instruction {@code
 * -dsannotations-options: inherit} in the bnd file.
 * </p>
 *
 * @author Michael C. Han
 */
public class DestinationConfiguration implements Serializable {

	/**
	 * Constructs a new DestinationConfiguration of the specified type with the
	 * specified name.
	 *
	 * @param destinationType the type of the new DestinationConfiguration
	 * @param destinationName the name of the new DestinationConfiguration
	 */
	public DestinationConfiguration(
		DestinationType destinationType, String destinationName) {

		_destinationType = destinationType;
		_destinationName = destinationName;

		if (_destinationType == DestinationType.SERIAL) {
			_workersCoreSize = 1;
			_workersMaxSize = 1;
		}
	}

	/**
	 * Returns <code>true</code> if the DestinationConfiguration equals the
	 * specified object.
	 *
	 * <p>
	 * Two DestinationConfiguration instances are considered equal if their
	 * names are equal.
	 * </p>
	 *
	 * @param  object the object against which to check for equality
	 * @return <code>true</code> if this DestinationConfiguration equals the
	 *         specified object; <code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DestinationConfiguration)) {
			return false;
		}

		DestinationConfiguration destinationConfiguration =
			(DestinationConfiguration)object;

		if (Objects.equals(
				_destinationName, destinationConfiguration._destinationName)) {

			return true;
		}

		return false;
	}

	/**
	 * Returns the DestinationConfiguration's name.
	 *
	 * @return the DestinationConfiguration's name
	 */
	public String getDestinationName() {
		return _destinationName;
	}

	/**
	 * Returns the DestinationConfiguration's destination type.
	 *
	 * <p>
	 * Possible destination types are DestinationType.SYNCHRONOUS,
	 * DestinationType.PARALLEL, or DestinationType.SERIAL. Both
	 * DestinationType.PARALLEL and DestinationType.SERIAL represent
	 * asynchronous destinations.
	 * </p>
	 *
	 * @return the DestinationConfiguration's destination type
	 */
	public DestinationType getDestinationType() {
		return _destinationType;
	}

	/**
	 * Returns the DestinationConfiguration's maximum queue size.
	 *
	 * <p>
	 * The maximum queue size limits the number of messages that can be queued
	 * up at a destination before they're dispatched on worker threads.
	 * </p>
	 *
	 * @return the DestinationConfiguration's maximum queue size
	 */
	public int getMaximumQueueSize() {
		return _maximumQueueSize;
	}

	/**
	 * Returns the DestinationConfiguration's core thread pool size.
	 *
	 * <p>
	 * The differences between thread pool size, core thread pool size, and
	 * maximum thread pool size are the same as those explained here:
	 * @link{http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html}
	 * </p>
	 *
	 * @return the DestinationConfiguration's core thread pool size
	 */
	public int getWorkersCoreSize() {
		return _workersCoreSize;
	}

	/**
	 * Returns the DestinationConfiguration's maximum thread pool size.
	 *
	 * <p>
	 * The differences between thread pool size, core thread pool size, and
	 * maximum thread pool size are the same as those explained here:
	 * @link{http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html}
	 * </p>
	 *
	 * @return the DestinationConfiguration's maximum thread pool size
	 */
	public int getWorkersMaxSize() {
		return _workersMaxSize;
	}

	/**
	 * Returns the hash code of the DestinationConfiguration's name.
	 *
	 * @return the hash code of the DestinationConfiguration's name
	 */
	@Override
	public int hashCode() {
		return _destinationName.hashCode();
	}

	/**
	 * Sets the DestinationConfiguration's maximum queue size.
	 *
	 * <p>
	 * The maximum queue size limits the number of messages that can be queued
	 * up at a destination before they're dispatched on worker threads.
	 * </p>
	 *
	 * @param maximumQueueSize the new maximum queue size of the
	 * DestinationConfiguration
	 */
	public void setMaximumQueueSize(int maximumQueueSize) {
		_maximumQueueSize = maximumQueueSize;
	}

	/**
	 * Sets the DestinationConfiguration's core thread pool size.
	 *
	 * <p>
	 * The differences between thread pool size, core thread pool size, and
	 * maximum thread pool size are the same as those explained here:
	 * @link{http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html}
	 * </p>
	 *
	 * @return the new core thread pool size of the DestinationConfiguration
	 */
	public void setWorkersCoreSize(int workersCoreSize) {
		_workersCoreSize = workersCoreSize;
	}

	/**
	 * Sets the DestinationConfiguration's maximum thread pool size.
	 *
	 * <p>
	 * The differences between thread pool size, core thread pool size, and
	 * maximum thread pool size are the same as those explained here:
	 * @link{http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html}
	 * </p>
	 *
	 * @return the new maximum thread pool size of the DestinationConfiguration
	 */
	public void setWorkersMaxSize(int workersMaxSize) {
		_workersMaxSize = workersMaxSize;
	}

	/**
	 * Returns a string representation of the DestinationConfiguration
	 *
	 * @return a string representation of the DestinationConfiguration
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{_destinationName=");
		sb.append(_destinationName);
		sb.append(", _destinationType=");
		sb.append(_destinationType);
		sb.append(", _maximumQueueSize=");
		sb.append(_maximumQueueSize);
		sb.append(", _workersCoreSize=");
		sb.append(_workersCoreSize);
		sb.append(", _workersMaxSize=");
		sb.append(_workersMaxSize);
		sb.append("}");

		return sb.toString();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		setMaximumQueueSize(
			_get(properties, "maxQueueSize", Integer.MAX_VALUE));
		setWorkersCoreSize(
			_get(properties, "workerCoreSize", _WORKERS_CORE_SIZE));
		setWorkersMaxSize(_get(properties, "workerMaxSize", _WORKERS_MAX_SIZE));
	}

	private <T> T _get(
		Map<String, Object> properties, String key, T defaultValue) {

		Set<Entry<String, Object>> entrySet = properties.entrySet();

		Stream<Entry<String, Object>> stream = entrySet.stream();

		return stream.filter(
			e -> e.getKey().equals(key)
		).map(
			e -> (T)e.getValue()
		).findFirst(
		).orElse(
			defaultValue
		);
	}

	private static final int _WORKERS_CORE_SIZE = 2;

	private static final int _WORKERS_MAX_SIZE = 5;

	private final String _destinationName;
	private final DestinationType _destinationType;
	private int _maximumQueueSize = Integer.MAX_VALUE;
	private int _workersCoreSize = _WORKERS_CORE_SIZE;
	private int _workersMaxSize = _WORKERS_MAX_SIZE;

}
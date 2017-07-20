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

package com.liferay.messaging;

import com.liferay.petra.concurrent.RejectedExecutionHandler;

import java.io.Serializable;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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
	 * Returns a new DestinationConfiguration of type DestinationType.PARALLEL
	 * with the specified name.
	 *
	 * @param  destinationName the name of the new DestinationConfiguration
	 * @return a new DestinationConfiguration of type DestinationType.PARALLEL
	 */
	public static DestinationConfiguration
		createParallelDestinationConfiguration(String destinationName) {

		return new DestinationConfiguration(
			DestinationType.PARALLEL, destinationName);
	}

	/**
	 * Returns a new DestinationConfiguration of type DestinationType.SERIAL
	 * with the specified name.
	 *
	 * @param  destinationName the name of the new DestinationConfiguration
	 * @return a new DestinationConfiguration of type DestinationType.SERIAL
	 */
	public static DestinationConfiguration createSerialDestinationConfiguration(
		String destinationName) {

		return new DestinationConfiguration(
			DestinationType.SERIAL, destinationName);
	}

	/**
	 * Returns a new DestinationConfiguration of type
	 * DestinationType.SYNCHRONOUS with the specified name.
	 *
	 * @param  destinationName the name of the new DestinationConfiguration
	 * @return a new DestinationConfiguration of type
	 *         DestinationType.SYNCHRONOUS
	 */
	public static DestinationConfiguration
		createSynchronousDestinationConfiguration(String destinationName) {

		return new DestinationConfiguration(
			DestinationType.SYNCHRONOUS, destinationName);
	}

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
	 * Returns the DestinationConfiguration's rejected execution handler.
	 *
	 * <p>
	 * A rejected execution handler determines what happens when the number of
	 * incoming messages exceeds the maximum queue size.
	 * </p>
	 *
	 * @return the DestinationConfiguration's rejected execution handler
	 */
	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return _rejectedExecutionHandler;
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
	 * Sets the DestinationConfiguration's rejected execution handler.
	 *
	 * <p>
	 * A rejected execution handler determines what happens when the number of
	 * incoming messages exceeds the maximum queue size.
	 * </p>
	 *
	 * @param rejectedExecutionHandler the new rejected execution handler of the
	 * DestinationConfiguration
	 */
	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policyOption = ReferencePolicyOption.GREEDY, unbind = "-"
	)
	public void setRejectedExecutionHandler(
		RejectedExecutionHandler rejectedExecutionHandler) {

		_rejectedExecutionHandler = rejectedExecutionHandler;
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
		sb.append(", _rejectedExecutionHandler=");
		sb.append(_rejectedExecutionHandler);
		sb.append(", _workersCoreSize=");
		sb.append(_workersCoreSize);
		sb.append(", _workersMaxSize=");
		sb.append(_workersMaxSize);
		sb.append("}");

		return sb.toString();
	}

	@Activate
	protected void activate(DestinationSettings destinationSettings) {
		setMaximumQueueSize(destinationSettings.maximumQueueSize());
		setWorkersCoreSize(destinationSettings.workersCoreSize());
		setWorkersMaxSize(destinationSettings.workersMaxSize());
	}

	private static final int _WORKERS_CORE_SIZE = 2;

	private static final int _WORKERS_MAX_SIZE = 5;

	private final String _destinationName;
	private final DestinationType _destinationType;
	private int _maximumQueueSize = Integer.MAX_VALUE;
	private RejectedExecutionHandler _rejectedExecutionHandler;
	private int _workersCoreSize = _WORKERS_CORE_SIZE;
	private int _workersMaxSize = _WORKERS_MAX_SIZE;

}
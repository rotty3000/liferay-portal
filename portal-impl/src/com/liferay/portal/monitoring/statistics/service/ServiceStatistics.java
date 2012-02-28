/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.monitoring.statistics.service;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.monitoring.RequestStatus;
import com.liferay.portal.kernel.monitoring.statistics.DataSampleProcessor;
import com.liferay.portal.kernel.monitoring.statistics.RequestStatistics;
import com.liferay.portal.kernel.util.MethodKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael C. Han
 */
public class ServiceStatistics
	implements DataSampleProcessor<ServiceRequestDataSample> {

	public ServiceStatistics(String className) {
		_className = className;
	}

	public long getAverageTime(String methodName, String[] parameterTypes)
		throws SystemException {

		try {
			MethodKey methodKey = new MethodKey(
				_className, methodName, parameterTypes);

			RequestStatistics requestStatistics = _methodRequestStatistics.get(
				methodKey);

			if (requestStatistics != null) {
				return requestStatistics.getAverageTime();
			}
		}
		catch (ClassNotFoundException cnfe) {
			throw new SystemException(cnfe);
		}

		return -1;
	}

	public long getErrorCount(String methodName, String[] parameterTypes)
		throws SystemException {

		try {
			MethodKey methodKey = new MethodKey(
				_className, methodName, parameterTypes);

			RequestStatistics requestStatistics = _methodRequestStatistics.get(
				methodKey);

			if (requestStatistics != null) {
				return requestStatistics.getErrorCount();
			}
		}
		catch (ClassNotFoundException cnfe) {
			throw new SystemException(cnfe);
		}

		return -1;
	}

	public long getMaxTime(String methodName, String[] parameterTypes)
		throws SystemException {

		try {
			MethodKey methodKey = new MethodKey(
				_className, methodName, parameterTypes);

			RequestStatistics requestStatistics = _methodRequestStatistics.get(
				methodKey);

			if (requestStatistics != null) {
				return requestStatistics.getMaxTime();
			}
		}
		catch (ClassNotFoundException cnfe) {
			throw new SystemException(cnfe);
		}

		return -1;
	}

	public long getMinTime(String methodName, String[] parameterTypes)
		throws SystemException {

		try {
			MethodKey methodKey = new MethodKey(
				_className, methodName, parameterTypes);

			RequestStatistics requestStatistics = _methodRequestStatistics.get(
				methodKey);

			if (requestStatistics != null) {
				return requestStatistics.getMinTime();
			}
		}
		catch (ClassNotFoundException cnfe) {
			throw new SystemException(cnfe);
		}

		return -1;
	}

	public long getRequestCount(String methodName, String[] parameterTypes)
		throws SystemException {

		try {
			MethodKey methodKey = new MethodKey(
				_className, methodName, parameterTypes);

			RequestStatistics requestStatistics = _methodRequestStatistics.get(
				methodKey);

			if (requestStatistics != null) {
				return requestStatistics.getRequestCount();
			}
		}
		catch (ClassNotFoundException cnfe) {
			throw new SystemException(cnfe);
		}

		return -1;
	}

	public void processDataSample(
		ServiceRequestDataSample serviceRequestDataSample) {

		MethodKey methodKey = serviceRequestDataSample.getMethodKey();

		RequestStatistics requestStatistics = _methodRequestStatistics.get(
			methodKey);

		if (requestStatistics == null) {
			requestStatistics = new RequestStatistics(methodKey.toString());

			_methodRequestStatistics.put(methodKey, requestStatistics);
		}

		RequestStatus requestStatus =
			serviceRequestDataSample.getRequestStatus();

		if (requestStatus == RequestStatus.ERROR) {
			requestStatistics.incrementError();
		}
		else if (requestStatus == RequestStatus.TIMEOUT) {
			requestStatistics.incrementTimeout();
		}
		else if (requestStatus == RequestStatus.SUCCESS) {
			requestStatistics.incrementSuccessDuration(
				serviceRequestDataSample.getDuration());
		}
	}

	private String _className;
	private Map<MethodKey, RequestStatistics> _methodRequestStatistics =
		new ConcurrentHashMap<MethodKey, RequestStatistics>();

}
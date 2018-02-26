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

package com.liferay.petra.messaging.test.tb17;

import com.liferay.petra.messaging.test.tb3.TBSerialDestination;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jesse Rao
 */
@Component(
	property = {"destination.name=" + TBSerialDestination.DESTINATION_NAME}, scope = ServiceScope.SINGLETON,
	service = {Callable.class, RejectedExecutionHandler.class}
)
public class RejectedExecutionHandlerImpl
	implements Callable<Map<Runnable, ThreadPoolExecutor>>,
			   RejectedExecutionHandler {

	@Override
	public Map<Runnable, ThreadPoolExecutor> call() throws Exception {
		return _map;
	}

	@Override
	public void rejectedExecution(
		Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

		_map.put(runnable, threadPoolExecutor);
	}

	private final Map<Runnable, ThreadPoolExecutor> _map =
		new ConcurrentHashMap<>();

}
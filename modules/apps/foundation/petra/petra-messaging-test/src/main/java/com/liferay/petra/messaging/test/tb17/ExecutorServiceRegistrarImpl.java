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

import com.liferay.petra.messaging.api.ExecutorServiceRegistrar;
import com.liferay.petra.messaging.test.tb3.TBSerialDestination;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jesse Rao
 */
@Component(
	property = {"destination.name=" + TBSerialDestination.DESTINATION_NAME}, scope = ServiceScope.SINGLETON,
	service = {Callable.class, ExecutorServiceRegistrar.class}
)
public class ExecutorServiceRegistrarImpl
	implements Callable<Map<String, ExecutorService>>,
			   ExecutorServiceRegistrar {

	@Override
	public Map<String, ExecutorService> call() throws Exception {
		return _executorServices;
	}

	@Override
	public <T extends ExecutorService> T registerExecutorService(
		String name, T executorService) {

		T existingRegistration = null;

		if (executorService == null) {
			existingRegistration = (T)_executorServices.remove(name);
		}
		else {
			existingRegistration = (T)_executorServices.put(
				name, executorService);
		}

		return existingRegistration;
	}

	private final Map<String, ExecutorService> _executorServices =
		new ConcurrentHashMap<>();

}
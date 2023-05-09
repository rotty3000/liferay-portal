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

package com.liferay.batch.engine.internal.executor;

import com.liferay.batch.engine.internal.unit.BatchEngineUnitProcessorImpl;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.executor.PortalExecutorConfig;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.NamedThreadFactory;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(service = PortalExecutorConfig.class)
public class BatchEnginePortalExecutorConfig extends PortalExecutorConfig {

	@Activate
	public BatchEnginePortalExecutorConfig(Map<String, Object> properties) {
		super(
			BatchEngineUnitProcessorImpl.class.getName(),
			MapUtil.getInteger(properties, "corePoolSize", 1),
			MapUtil.getInteger(properties, "maxPoolSize", 1),
			MapUtil.getLong(properties, "keepAliveTime", 60),
			TimeUnit.valueOf(
				MapUtil.getString(properties, "timeUnit", "SECONDS")),
			MapUtil.getInteger(properties, "maxQueueSize", Integer.MAX_VALUE),
			new NamedThreadFactory(
				BatchEngineUnitProcessorImpl.class.getName(),
				Thread.NORM_PRIORITY,
				BatchEngineUnitProcessorImpl.class.getClassLoader()),
			new ThreadPoolExecutor.AbortPolicy(),
			new ThreadPoolHandlerAdapter() {

				@Override
				public void afterExecute(
					Runnable runnable, Throwable throwable) {

					CentralizedThreadLocal.clearShortLivedThreadLocals();
				}

			});
	}

}
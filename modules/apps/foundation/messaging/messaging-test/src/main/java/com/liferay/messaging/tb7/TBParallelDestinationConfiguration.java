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

package com.liferay.messaging.tb7;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.interfaces.Config;
import com.liferay.petra.concurrent.RejectedExecutionHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Raymond Augé
 */
@Component(service = DestinationConfiguration.class)
public class TBParallelDestinationConfiguration
	extends DestinationConfiguration {

	public TBParallelDestinationConfiguration() {
		super(
			DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			"synchronous/send/tb7");
	}

	@Override
	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policyOption = ReferencePolicyOption.GREEDY, unbind = "-"
	)
	public void setRejectedExecutionHandler(
		RejectedExecutionHandler rejectedExecutionHandler) {

		super.setRejectedExecutionHandler(rejectedExecutionHandler);
	}

	@Activate
	protected void activate(Config config) {
		setMaximumQueueSize(config.maximum_queue_size());
		setWorkersCoreSize(config.workers_core_size());
		setWorkersMaxSize(config.workers_max_size());
	}

}
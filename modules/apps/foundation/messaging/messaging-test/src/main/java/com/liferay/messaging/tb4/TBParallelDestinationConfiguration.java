package com.liferay.messaging.tb4;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.petra.concurrent.RejectedExecutionHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@interface Config {
	int maximum_queue_size() default Integer.MAX_VALUE;
	int workers_core_size() default 2;
	int workers_max_size() default 5;
}

@Component(
	service = DestinationConfiguration.class
)
public class TBParallelDestinationConfiguration extends DestinationConfiguration {

	public TBParallelDestinationConfiguration() {
		super(
			DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			"configuration/tb4");
	}

	@Activate
	protected void activate(Config config) {
		setMaximumQueueSize(config.maximum_queue_size());
		setWorkersCoreSize(config.workers_core_size());
		setWorkersMaxSize(config.workers_max_size());
	}

	@Override
	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policyOption = ReferencePolicyOption.GREEDY
	)
	public void setRejectedExecutionHandler(
		RejectedExecutionHandler rejectedExecutionHandler) {

		super.setRejectedExecutionHandler(rejectedExecutionHandler);
	}

}
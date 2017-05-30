package com.liferay.messaging.tb5;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.petra.concurrent.RejectedExecutionHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@interface Config {
	int maximum_queue_size() default Integer.MAX_VALUE;
}

@Component(
	service = DestinationConfiguration.class
)
public class TBSerialDestinationConfiguration extends DestinationConfiguration {

	public TBSerialDestinationConfiguration() {
		super(
			DestinationConfiguration.DESTINATION_TYPE_SERIAL,
			"configuration/tb5");
	}

	@Activate
	protected void activate(Config config) {
		setMaximumQueueSize(config.maximum_queue_size());
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
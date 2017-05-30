package com.liferay.messaging.tb6;

import com.liferay.messaging.DestinationConfiguration;

import org.osgi.service.component.annotations.Component;

@Component(
	service = DestinationConfiguration.class
)
public class TBSynchronousDestinationConfiguration extends DestinationConfiguration {

	public TBSynchronousDestinationConfiguration() {
		super(
			DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS,
			"configuration/tb6");
	}

}
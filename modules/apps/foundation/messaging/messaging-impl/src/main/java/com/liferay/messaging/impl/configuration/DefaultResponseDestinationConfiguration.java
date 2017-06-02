package com.liferay.messaging.impl.configuration;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationNames;

import org.osgi.service.component.annotations.Component;

@Component(
	service = DestinationConfiguration.class
)
public class DefaultResponseDestinationConfiguration extends DestinationConfiguration {

	public DefaultResponseDestinationConfiguration() {
		super(
			DestinationConfiguration.DESTINATION_TYPE_SYNCHRONOUS,
			DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
	}

}
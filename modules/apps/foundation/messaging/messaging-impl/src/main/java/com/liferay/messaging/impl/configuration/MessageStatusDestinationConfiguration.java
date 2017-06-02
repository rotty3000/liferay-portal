package com.liferay.messaging.impl.configuration;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationNames;

import org.osgi.service.component.annotations.Component;

@Component(
	service = DestinationConfiguration.class
)
public class MessageStatusDestinationConfiguration extends DestinationConfiguration {

	public MessageStatusDestinationConfiguration() {
		super(
			DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			DestinationNames.MESSAGE_BUS_MESSAGE_STATUS);
	}

}
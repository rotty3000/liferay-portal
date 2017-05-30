package com.liferay.messaging.tb3;

import com.liferay.messaging.Destination;
import com.liferay.messaging.SerialDestination;
import com.liferay.messaging.interfaces.Config;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"destination.name=serial/test"
	},
	scope = ServiceScope.SINGLETON,
	service = Destination.class
)
public class TBSerialDestination extends SerialDestination {

	@Activate
	protected void activate(Config config) {
		setName(config.destination_name());
	}

}
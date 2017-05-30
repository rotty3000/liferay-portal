package com.liferay.messaging.tb1;

import com.liferay.messaging.Destination;
import com.liferay.messaging.SynchronousDestination;
import com.liferay.messaging.interfaces.Config;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"destination.name=synchronous/test"
	},
	scope = ServiceScope.SINGLETON,
	service = Destination.class
)
public class TBSynchronousDestination extends SynchronousDestination {

	@Activate
	protected void activate(Config config) {
		setName(config.destination_name());
	}

}
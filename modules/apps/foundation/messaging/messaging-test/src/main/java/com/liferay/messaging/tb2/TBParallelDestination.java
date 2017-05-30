package com.liferay.messaging.tb2;

import com.liferay.messaging.Destination;
import com.liferay.messaging.ParallelDestination;
import com.liferay.messaging.interfaces.Config;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"destination.name=parallel/test"
	},
	scope = ServiceScope.SINGLETON,
	service = Destination.class
)
public class TBParallelDestination extends ParallelDestination {

	@Activate
	protected void activate(Config config) {
		setName(config.destination_name());
	}

}
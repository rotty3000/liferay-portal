package com.liferay.messaging.tb2;

import com.liferay.messaging.Destination;
import com.liferay.messaging.ParallelDestination;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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
	void activate() {
		open();
	}

	@Deactivate
	void deactivate() {
		close();
	}

}
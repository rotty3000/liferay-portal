package com.liferay.messaging.tb1;

import com.liferay.messaging.Destination;
import com.liferay.messaging.SynchronousDestination;

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
}
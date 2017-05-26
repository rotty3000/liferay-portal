package com.liferay.messaging.tb3;

import com.liferay.messaging.Destination;
import com.liferay.messaging.SerialDestination;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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
	void activate() {
		open();
	}

	@Deactivate
	void deactivate() {
		close();
	}

}
package com.liferay.messaging.tb17;

import com.liferay.petra.concurrent.RejectedExecutionHandler;
import com.liferay.petra.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {"destination.name=serial/test"},
	scope = ServiceScope.SINGLETON,
	service = RejectedExecutionHandler.class
)
public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

	}

}

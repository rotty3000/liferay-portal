package com.liferay.messaging.tb17;

import com.liferay.messaging.ExecutorServiceRegistrar;

import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {"destination.name=serial/test"},
	scope = ServiceScope.SINGLETON,
	service = ExecutorServiceRegistrar.class
)
public class ExecutorServiceRegistrarImpl implements ExecutorServiceRegistrar {

	@Override
	public <T extends ExecutorService> T registerExecutorService(String name, T executorService) {
		return null;
	}

}

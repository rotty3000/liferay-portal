/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.runtime.internal.messaging;

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.DestinationEventListener;
import com.liferay.petra.messaging.api.DestinationNames;
import com.liferay.petra.messaging.api.DestinationType;
import com.liferay.petra.messaging.api.MessageBus;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.spi.proxy.ProxyMessageListener;
import com.liferay.portal.kernel.concurrent.CallerRunsPolicy;
import com.liferay.portal.kernel.concurrent.RejectedExecutionHandler;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListenerWrapper;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManager;
import com.liferay.portal.kernel.workflow.WorkflowEngineManager;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowLogManager;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.kernel.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.kernel.workflow.messaging.DefaultWorkflowDestinationEventListener;
import com.liferay.portal.workflow.kaleo.runtime.constants.KaleoRuntimeDestinationNames;
import com.liferay.portal.workflow.kaleo.runtime.internal.timer.messaging.TimerMessageListener;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = KaleoWorkflowMessagingConfigurator.class)
public class KaleoWorkflowMessagingConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		registerKaleoGraphWalkerDestination();

		registerWorkflowDefinitionLinkDestination();

		registerWorkflowMessageListeners();

		registerWorkflowTimerDestination();

		registerSchedulerEventMessageListener();
	}

	@Deactivate
	protected void deactivate() {
		unregisterKaleoWorkflowDestinations();

		unregisterWorkflowEngineDestinationListener();

		unregisterWorkflowMessageListeners();

		unregisterSchedulerEventMessageListener();

		_bundleContext = null;
	}

	protected void registerDestination(
		DestinationConfiguration kaleoGraphWalkerDestinationConfiguration) {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		String destinationName =
			kaleoGraphWalkerDestinationConfiguration.getDestinationName();

		properties.put("destination.name", destinationName);

		ServiceRegistration<DestinationConfiguration> serviceRegistration =
			_bundleContext.registerService(
				DestinationConfiguration.class,
				kaleoGraphWalkerDestinationConfiguration, properties);

		_destinationServiceRegistrations.put(destinationName, serviceRegistration);
	}

	protected void registerKaleoGraphWalkerDestination() {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationType.PARALLEL,
				KaleoRuntimeDestinationNames.KALEO_GRAPH_WALKER);

		destinationConfiguration.setMaximumQueueSize(_MAXIMUM_QUEUE_SIZE);

		RejectedExecutionHandler rejectedExecutionHandler =
			new CallerRunsPolicy() {

				@Override
				public void rejectedExecution(
					Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"The current thread will handle the request " +
								"because the graph walker's task queue is at " +
									"its maximum capacity");
					}

					super.rejectedExecution(runnable, threadPoolExecutor);
				}

			};

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("destination.name",
			KaleoRuntimeDestinationNames.KALEO_GRAPH_WALKER);

		ServiceRegistration<RejectedExecutionHandler> serviceRegistration =
			_bundleContext.registerService(RejectedExecutionHandler.class,
				rejectedExecutionHandler, properties);

		_rehServiceRegistrations.put(
			KaleoRuntimeDestinationNames.KALEO_GRAPH_WALKER,
			serviceRegistration);

		registerDestination(destinationConfiguration);
	}

	protected MessageListener registerProxyMessageListener(
		Object manager, String destinationName) {

		ProxyMessageListener proxyMessageListener = new ProxyMessageListener();

		proxyMessageListener.setManager(manager);
		proxyMessageListener.setMessageBus(_messageBus);

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("destination.name", destinationName);

		ServiceRegistration<MessageListener> serviceRegistration =
			_bundleContext.registerService(MessageListener.class,
				proxyMessageListener, properties);

		_proxyMessageListenerServiceRegistrations.add(serviceRegistration);

		return proxyMessageListener;
	}

	protected void registerSchedulerEventMessageListener() {
		SchedulerEventMessageListenerWrapper
			schedulerEventMessageListenerWrapper =
				new SchedulerEventMessageListenerWrapper();

		schedulerEventMessageListenerWrapper.setMessageListener(
			_timerMessageListener);

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put(
			"destination.name", KaleoRuntimeDestinationNames.WORKFLOW_TIMER);

		_schedulerEventMessageListenerServiceRegistration =
			_bundleContext.registerService(
				MessageListener.class, schedulerEventMessageListenerWrapper,
				properties);
	}

	protected void registerWorkflowDefinitionLinkDestination() {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(DestinationType.SYNCHRONOUS,
				KaleoRuntimeDestinationNames.WORKFLOW_DEFINITION_LINK);

		registerDestination(destinationConfiguration);
	}

	protected void registerWorkflowMessageListeners() {
		DefaultWorkflowDestinationEventListener
			defaultWorkflowDestinationEventListener =
			new DefaultWorkflowDestinationEventListener();

		defaultWorkflowDestinationEventListener.setWorkflowEngineName(
			"Liferay Kaleo Workflow Engine");

		MessageListener workflowComparatorMessageListener =
			registerProxyMessageListener(
				_workflowComparatorFactory,
				DestinationNames.WORKFLOW_COMPARATOR);

		defaultWorkflowDestinationEventListener.
			setWorkflowComparatorFactoryListener(
				workflowComparatorMessageListener);

		MessageListener workflowDefinitionManagerProxyMessageListener =
			registerProxyMessageListener(
				_workflowDefinitionManager,
				DestinationNames.WORKFLOW_DEFINITION);

		defaultWorkflowDestinationEventListener.
			setWorkflowDefinitionManagerListener(
				workflowDefinitionManagerProxyMessageListener);

		MessageListener workflowEngineManagerProxyMessageListener =
			registerProxyMessageListener(
				_workflowEngineManager, DestinationNames.WORKFLOW_ENGINE);

		defaultWorkflowDestinationEventListener.
			setWorkflowEngineManagerListener(
				workflowEngineManagerProxyMessageListener);

		MessageListener workflowInstanceManagerProxyMessageListener =
			registerProxyMessageListener(
				_workflowInstanceManager, DestinationNames.WORKFLOW_INSTANCE);

		defaultWorkflowDestinationEventListener.
			setWorkflowInstanceManagerListener(
				workflowInstanceManagerProxyMessageListener);

		MessageListener workflowLogManagerProxyMessageListener =
			registerProxyMessageListener(
				_workflowLogManagerk, DestinationNames.WORKFLOW_LOG);

		defaultWorkflowDestinationEventListener.setWorkflowLogManagerListener(
			workflowLogManagerProxyMessageListener);

		MessageListener workflowTaskManagerProxyMessageListener =
			registerProxyMessageListener(
				_workflowTaskManager, DestinationNames.WORKFLOW_TASK);

		defaultWorkflowDestinationEventListener.setWorkflowTaskManagerListener(
			workflowTaskManagerProxyMessageListener);

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("destination.name", _workflowEngineDestination.getName());

		ServiceRegistration<DestinationEventListener> serviceRegistration =
			_bundleContext.registerService(DestinationEventListener.class,
				defaultWorkflowDestinationEventListener, properties);
	}

	protected void registerWorkflowTimerDestination() {
		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(DestinationType.PARALLEL,
				KaleoRuntimeDestinationNames.WORKFLOW_TIMER);

		destinationConfiguration.setMaximumQueueSize(_MAXIMUM_QUEUE_SIZE);

		RejectedExecutionHandler rejectedExecutionHandler =
			new CallerRunsPolicy() {

				@Override
				public void rejectedExecution(
					Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

					if (_log.isWarnEnabled()) {
						_log.warn(
							"The current thread will handle the request " +
								"because the workflow timer task queue is at " +
									"its maximum capacity");
					}

					super.rejectedExecution(runnable, threadPoolExecutor);
				}

			};

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("destination.name",
			KaleoRuntimeDestinationNames.WORKFLOW_TIMER);

		ServiceRegistration<RejectedExecutionHandler> serviceRegistration =
			_bundleContext.registerService(RejectedExecutionHandler.class,
				rejectedExecutionHandler, properties);

		// TODO: save serviceRegistration

		registerDestination(destinationConfiguration);
	}

	protected void unregisterKaleoWorkflowDestinations() {
		for (ServiceRegistration<DestinationConfiguration> serviceRegistration :
				_destinationServiceRegistrations.values()) {

			DestinationConfiguration destinationConfiguration =
				_bundleContext.getService(serviceRegistration.getReference());

			serviceRegistration.unregister();

			// TODO: how to do this in petra messaging?
			//destination.destroy();
		}

		_destinationServiceRegistrations.clear();
	}

	protected void unregisterSchedulerEventMessageListener() {
		if (_schedulerEventMessageListenerServiceRegistration == null) {
			return;
		}

		_schedulerEventMessageListenerServiceRegistration.unregister();

		_schedulerEventMessageListenerServiceRegistration = null;
	}

	protected void unregisterWorkflowEngineDestinationListener() {
		_defaultWorkflowDestinationEventListenerServiceRegistration.unregister();
	}

	protected void unregisterWorkflowMessageListeners() {
		for (ServiceRegistration<MessageListener> serviceRegistration :
			_proxyMessageListenerServiceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private static final int _MAXIMUM_QUEUE_SIZE = 200;

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoWorkflowMessagingConfigurator.class);

	private BundleContext _bundleContext;

	@Reference
	private MessageBus _messageBus;

	private List<ServiceRegistration<MessageListener>>
		_proxyMessageListenerServiceRegistrations =
		new ArrayList<>();

	private ServiceRegistration<MessageListener>
		_defaultWorkflowDestinationEventListenerServiceRegistration;

	private ServiceRegistration<MessageListener>
		_schedulerEventMessageListenerServiceRegistration;

	private Map<String, ServiceRegistration<DestinationConfiguration>>
		_destinationServiceRegistrations = new HashMap<>();

	private Map<String, ServiceRegistration<RejectedExecutionHandler>>
		_rehServiceRegistrations = new HashMap<>();

	@Reference
	private TimerMessageListener _timerMessageListener;

	@Reference(target = "(proxy.bean=false)")
	private WorkflowComparatorFactory _workflowComparatorFactory;

	@Reference(target = "(proxy.bean=false)")
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Reference(
		target = "(destination.name=" + DestinationNames.WORKFLOW_ENGINE + ")"
	)
	private Destination _workflowEngineDestination;

	@Reference(target = "(proxy.bean=false)")
	private WorkflowEngineManager _workflowEngineManager;

	@Reference(target = "(proxy.bean=false)")
	private WorkflowInstanceManager _workflowInstanceManager;

	@Reference(target = "(proxy.bean=false)")
	private WorkflowLogManager _workflowLogManagerk;

	@Reference(target = "(proxy.bean=false)")
	private WorkflowTaskManager _workflowTaskManager;

}
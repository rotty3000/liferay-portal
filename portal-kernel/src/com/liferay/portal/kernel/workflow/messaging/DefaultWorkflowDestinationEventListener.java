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

package com.liferay.portal.kernel.workflow.messaging;

import com.liferay.petra.messaging.api.DestinationEventListener;
import com.liferay.petra.messaging.api.DestinationNames;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class DefaultWorkflowDestinationEventListener
	implements DestinationEventListener {

	@Override
	public void messageListenerRegistered(
		String destinationName, MessageListener messageListener) {

		if (_log.isInfoEnabled()) {
			_log.info(
				"Unregistering default workflow engine " + _workflowEngineName);
		}

		if (!isProceed(destinationName, messageListener)) {
			return;
		}

		unregisterMessageListener("workflowComparatorFactoryListener");

		unregisterMessageListener("workflowDefinitionManagerListener");

		unregisterMessageListener("workflowEngineManagerListener");

		unregisterMessageListener("workflowInstanceManagerListener");

		unregisterMessageListener("workflowLogManagerListener");

		unregisterMessageListener("workflowTaskManagerListener");
	}

	@Override
	public void messageListenerUnregistered(
		String destinationName, MessageListener messageListener) {

		if (!isProceed(destinationName, messageListener)) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Registering default workflow engine " + _workflowEngineName);
		}

		registerMessageListener(DestinationNames.WORKFLOW_COMPARATOR,
			"workflowComparatorFactoryListener", _workflowComparatorFactoryListener);

		registerMessageListener(DestinationNames.WORKFLOW_DEFINITION,
			"workflowDefinitionManagerListener", _workflowDefinitionManagerListener);

		registerMessageListener(DestinationNames.WORKFLOW_ENGINE,
			"workflowEngineManagerListener", _workflowEngineManagerListener);

		registerMessageListener(DestinationNames.WORKFLOW_INSTANCE,
			"workflowInstanceManagerListener", _workflowInstanceManagerListener);

		registerMessageListener(DestinationNames.WORKFLOW_LOG,
			"workflowLogManagerListener", _workflowLogManagerListener);

		registerMessageListener(DestinationNames.WORKFLOW_TASK,
			"workflowTaskManagerListener", _workflowTaskManagerListener);
	}

	public void setWorkflowComparatorFactoryListener(
		MessageListener workflowComparatorFactoryListener) {

		_workflowComparatorFactoryListener = workflowComparatorFactoryListener;
	}

	public void setWorkflowDefinitionManagerListener(
		MessageListener workflowDefinitionManagerListener) {

		_workflowDefinitionManagerListener = workflowDefinitionManagerListener;
	}

	public void setWorkflowEngineManagerListener(
		MessageListener workflowEngineManagerListener) {

		_workflowEngineManagerListener = workflowEngineManagerListener;
	}

	public void setWorkflowEngineName(String workflowEngineName) {
		_workflowEngineName = workflowEngineName;
	}

	public void setWorkflowInstanceManagerListener(
		MessageListener workflowInstanceManagerListener) {

		_workflowInstanceManagerListener = workflowInstanceManagerListener;
	}

	public void setWorkflowLogManagerListener(
		MessageListener workflowLogManagerListener) {

		_workflowLogManagerListener = workflowLogManagerListener;
	}

	public void setWorkflowTaskManagerListener(
		MessageListener workflowTaskManagerListener) {

		_workflowTaskManagerListener = workflowTaskManagerListener;
	}

	protected boolean isProceed(
		String destinationName, MessageListener messageListener) {

		if (messageListener.equals(_workflowEngineManagerListener)) {
			return false;
		}
		else {
			return true;
		}
	}

	private void registerMessageListener(
		String destinationName, String messageListenerName,
		MessageListener messageListener) {

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties =
			new HashMap<>();

		properties.put("destination.name", destinationName);
		properties.put("message.listener.name", messageListenerName);

		ServiceRegistration<MessageListener> serviceRegistration =
			registry.registerService(MessageListener.class,
				messageListener, properties);

		_messageListenerServiceRegistrations.put(
			messageListenerName, serviceRegistration);
	}

	private void unregisterMessageListener(String messageListenerName) {

		ServiceRegistration<MessageListener> serviceRegistration =
			_messageListenerServiceRegistrations.get(messageListenerName);

		serviceRegistration.unregister();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultWorkflowDestinationEventListener.class);

	private Map<String, ServiceRegistration<MessageListener>>
		_messageListenerServiceRegistrations = new HashMap<>();

	private MessageListener _workflowComparatorFactoryListener;
	private MessageListener _workflowDefinitionManagerListener;
	private MessageListener _workflowEngineManagerListener;
	private String _workflowEngineName;
	private MessageListener _workflowInstanceManagerListener;
	private MessageListener _workflowLogManagerListener;
	private MessageListener _workflowTaskManagerListener;

}
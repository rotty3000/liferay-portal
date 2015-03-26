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

package com.liferay.portal.cluster.internal;

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.portal.cluster.ClusterChannel;
import com.liferay.portal.cluster.ClusterChannelFactory;
import com.liferay.portal.cluster.ClusterReceiver;
import com.liferay.portal.cluster.configuration.ClusterLinkConfiguration;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterLink;
import com.liferay.portal.kernel.cluster.Priority;
import com.liferay.portal.kernel.executor.PortalExecutorManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	configurationPid = "com.liferay.portal.cluster.configuration.ClusterLinkConfiguration",
	immediate = true,
	property = {
		"channel.properties.transport.0=UDP(bind_addr=localhost;mcast_group_addr=239.255.0.2;mcast_port=23302):PING(timeout=2000;num_initial_members=20;break_on_coord_rsp=true):MERGE3(min_interval=10000;max_interval=30000):FD_SOCK:FD_ALL:VERIFY_SUSPECT(timeout=1500):pbcast.NAKACK2(xmit_interval=1000;xmit_table_num_rows=100;xmit_table_msgs_per_row=2000;xmit_table_max_compaction_time=30000;max_msg_batch_size=500;use_mcast_xmit=false;discard_delivered_msgs=true):UNICAST2(max_bytes=10M;xmit_table_num_rows=100;xmit_table_msgs_per_row=2000;xmit_table_max_compaction_time=60000;max_msg_batch_size=500):pbcast.STABLE(stability_delay=1000;desired_avg_gossip=50000;max_bytes=4M):pbcast.GMS(join_timeout=3000;print_local_addr=true;view_bundling=true):UFC(max_credits=2M;min_threshold=0.4):MFC(max_credits=2M;min_threshold=0.4):FRAG2(frag_size=61440):RSVP(resend_interval=2000;timeout=10000)"
	},
	service = ClusterLink.class
)
public class ClusterLinkImpl implements ClusterLink {

	@Override
	public boolean isEnabled() {
		return clusterLinkConfiguration.enabled();
	}

	@Override
	public void sendMulticastMessage(Message message, Priority priority) {
		if (!isEnabled()) {
			return;
		}

		ClusterChannel clusterChannel = getChannel(priority);

		clusterChannel.sendMulticastMessage(message);
	}

	@Override
	public void sendUnicastMessage(
		Address address, Message message, Priority priority) {

		if (!isEnabled()) {
			return;
		}

		if (_localTransportAddresses.contains(address)) {
			sendLocalMessage(message);

			return;
		}

		ClusterChannel clusterChannel = getChannel(priority);

		clusterChannel.sendUnicastMessage(message, address);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		clusterLinkConfiguration = Configurable.createConfigurable(
			ClusterLinkConfiguration.class, properties);

		initialize(properties);
	}

	@Deactivate
	protected void deactivate() {
		if (_transportChannels != null) {
			for (ClusterChannel clusterChannel : _transportChannels) {
				clusterChannel.close();
			}
		}

		_localTransportAddresses = null;
		_transportChannels = null;
		_clusterReceivers = null;

		if (_executorService != null) {
			_executorService.shutdownNow();
		}

		_executorService = null;
	}

	protected ClusterChannel getChannel(Priority priority) {
		int channelIndex =
			priority.ordinal() * _channelCount / MAX_CHANNEL_COUNT;

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Select channel number " + channelIndex + " for priority " +
					priority);
		}

		return _transportChannels.get(channelIndex);
	}

	protected ExecutorService getExecutorService() {
		return _executorService;
	}

	protected List<Address> getLocalTransportAddresses() {
		return _localTransportAddresses;
	}

	protected void initChannels(Map<String, Object> properties)
		throws Exception {

		Properties transportProperties = new Properties();

		for (String propertyKey : properties.keySet()) {
			if (propertyKey.startsWith(_CHANNEL_PROPERTIES_TRANSPORT)) {
				transportProperties.put(
					propertyKey, properties.get(propertyKey));
			}
		}

		_channelCount = transportProperties.size();

		if ((_channelCount <= 0) || (_channelCount > MAX_CHANNEL_COUNT)) {
			throw new IllegalArgumentException(
				"Channel count must be between 1 and " + MAX_CHANNEL_COUNT);
		}

		_localTransportAddresses = new ArrayList<>(_channelCount);
		_transportChannels = new ArrayList<>(_channelCount);
		_clusterReceivers = new ArrayList<>(_channelCount);

		List<String> keys = new ArrayList<>(_channelCount);

		for (Object key : transportProperties.keySet()) {
			keys.add((String)key);
		}

		Collections.sort(keys);

		String transportChannelNamePrefix =
			clusterLinkConfiguration.channelNamePrefix() + "transport-";

		for (int i = 0; i < keys.size(); i++) {
			String customName = keys.get(i);

			String value = transportProperties.getProperty(customName);

			ClusterReceiver clusterReceiver = new ClusterForwardReceiver(this);

			ClusterChannel clusterChannel =
				_clusterChannelFactory.createClusterChannel(
					value, transportChannelNamePrefix + i, clusterReceiver);

			_clusterReceivers.add(clusterReceiver);
			_localTransportAddresses.add(clusterChannel.getLocalAddress());
			_transportChannels.add(clusterChannel);
		}
	}

	protected void initialize(Map<String, Object> properties) {
		if (!isEnabled()) {
			return;
		}

		_executorService = _portalExecutorManager.getPortalExecutor(
			ClusterLinkImpl.class.getName());

		try {
			initChannels(properties);
		}
		catch (Exception e) {
			if (_log.isErrorEnabled()) {
				_log.error("Unable to initialize channels", e);
			}

			throw new IllegalStateException(e);
		}

		for (ClusterReceiver clusterReceiver : _clusterReceivers) {
			clusterReceiver.openLatch();
		}
	}

	@Modified
	protected synchronized void modified(Map<String, Object> properties) {
		clusterLinkConfiguration = Configurable.createConfigurable(
			ClusterLinkConfiguration.class, properties);

		if (!clusterLinkConfiguration.enabled() &&
			(_transportChannels != null)) {

			deactivate();
		}
		else if (clusterLinkConfiguration.enabled() &&
				 (_transportChannels == null)) {

			initialize(properties);
		}
	}

	protected void sendLocalMessage(Message message) {
		String destinationName = message.getDestinationName();

		if (Validator.isNotNull(destinationName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Sending local cluster link message " + message + " to " +
						destinationName);
			}

			ClusterInvokeThreadLocal.setEnabled(false);

			try {
				MessageBusUtil.sendMessage(destinationName, message);
			}
			finally {
				ClusterInvokeThreadLocal.setEnabled(true);
			}
		}
		else {
			_log.error(
				"Local cluster link message has no destination " + message);
		}
	}

	@Reference
	protected void setClusterChannelFactory(
		ClusterChannelFactory clusterChannelFactory) {

		_clusterChannelFactory = clusterChannelFactory;
	}

	@Reference
	protected void setPortalExecutorManager(
		PortalExecutorManager portalExecutorManager) {

		_portalExecutorManager = portalExecutorManager;
	}

	protected volatile ClusterLinkConfiguration clusterLinkConfiguration;

	private static final String _CHANNEL_PROPERTIES_TRANSPORT =
		"channel.properties.transport";

	private static final Log _log = LogFactoryUtil.getLog(
		ClusterLinkImpl.class);

	private int _channelCount;
	private ClusterChannelFactory _clusterChannelFactory;
	private List<ClusterReceiver> _clusterReceivers;
	private ExecutorService _executorService;
	private List<Address> _localTransportAddresses;
	private PortalExecutorManager _portalExecutorManager;
	private List<ClusterChannel> _transportChannels;

}
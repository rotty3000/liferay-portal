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

package com.liferay.messaging.impl.internal.sender;

import com.liferay.messaging.impl.internal.DefaultMessageBus;
import com.liferay.messaging.sender.SingleDestinationMessageSender;
import com.liferay.messaging.sender.SingleDestinationMessageSenderFactory;
import com.liferay.messaging.sender.SingleDestinationSynchronousMessageSender;
import com.liferay.messaging.sender.SynchronousMessageSender;
import com.liferay.petra.io.convert.Conversions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(
	property = {"timeout=10000"}
)
public class DefaultSingleDestinationMessageSenderFactory
	implements SingleDestinationMessageSenderFactory {

	@Override
	public SingleDestinationMessageSender createSingleDestinationMessageSender(
		String destinationName) {

		DefaultSingleDestinationMessageSender
			defaultSingleDestinationMessageSender =
				_defaultSingleDestinationMessageSenders.get(destinationName);

		if (defaultSingleDestinationMessageSender == null) {
			defaultSingleDestinationMessageSender =
				new DefaultSingleDestinationMessageSender();

			defaultSingleDestinationMessageSender.setDestinationName(
				destinationName);
			defaultSingleDestinationMessageSender.setMessageBus(_messageBus);

			_defaultSingleDestinationMessageSenders.put(
				destinationName, defaultSingleDestinationMessageSender);
		}

		return defaultSingleDestinationMessageSender;
	}

	@Override
	public SingleDestinationSynchronousMessageSender
		createSingleDestinationSynchronousMessageSender(
			String destinationName, SynchronousMessageSender.Mode mode) {

		DefaultSingleDestinationSynchronousMessageSender
			defaultSingleDestinationSynchronousMessageSender =
				_defaultSingleDestinationSynchronousMessageSenders.get(
					destinationName);

		if (defaultSingleDestinationSynchronousMessageSender == null) {
			SynchronousMessageSender synchronousMessageSender =
				_synchronousMessageSenders.get(mode);

			if (synchronousMessageSender == null) {
				throw new IllegalStateException(
					"No synchronous message sender configured for " + mode);
			}

			defaultSingleDestinationSynchronousMessageSender =
				new DefaultSingleDestinationSynchronousMessageSender();

			defaultSingleDestinationSynchronousMessageSender.setDestinationName(
				destinationName);
			defaultSingleDestinationSynchronousMessageSender.
				setSynchronousMessageSender(synchronousMessageSender);

			_defaultSingleDestinationSynchronousMessageSenders.put(
				destinationName,
				defaultSingleDestinationSynchronousMessageSender);
		}

		return defaultSingleDestinationSynchronousMessageSender;
	}

	@Override
	public int getModesCount() {
		return _synchronousMessageSenders.size();
	}

	@Override
	public SynchronousMessageSender getSynchronousMessageSender(
		SynchronousMessageSender.Mode mode) {

		return _synchronousMessageSenders.get(mode);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		long timeout = Conversions.getLong(properties.get("timeout"), 10000L);

		DefaultSynchronousMessageSender defaultSynchronousMessageSender =
			new DefaultSynchronousMessageSender();

		defaultSynchronousMessageSender.setMessageBus(_messageBus);
		defaultSynchronousMessageSender.setTimeout(timeout);

		_synchronousMessageSenders.put(
			SynchronousMessageSender.Mode.DEFAULT,
			defaultSynchronousMessageSender);

		DirectSynchronousMessageSender directSynchronousMessageSender =
			new DirectSynchronousMessageSender();

		directSynchronousMessageSender.setMessageBus(_messageBus);

		_synchronousMessageSenders.put(
			SynchronousMessageSender.Mode.DIRECT,
			directSynchronousMessageSender);
	}

	protected SynchronousMessageSender.Mode getMode(
		Map<String, Object> properties) {

		String mode = Conversions.getString(properties.get("mode"));

		return SynchronousMessageSender.Mode.valueOf(mode);
	}

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setSynchronousMessageSender(
		SynchronousMessageSender synchronousMessageSender,
		Map<String, Object> properties) {

		_synchronousMessageSenders.put(
			getMode(properties), synchronousMessageSender);
	}

	protected void unsetSynchronousMessageSender(
		SynchronousMessageSender synchronousMessageSender,
		Map<String, Object> properties) {

		_synchronousMessageSenders.remove(getMode(properties));
	}

	private final Map<String, DefaultSingleDestinationMessageSender>
		_defaultSingleDestinationMessageSenders = new ConcurrentHashMap<>();
	private final Map<String, DefaultSingleDestinationSynchronousMessageSender>
		_defaultSingleDestinationSynchronousMessageSenders =
			new ConcurrentHashMap<>();
	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private DefaultMessageBus _messageBus;
	private final Map<SynchronousMessageSender.Mode, SynchronousMessageSender>
		_synchronousMessageSenders = new HashMap<>();

}
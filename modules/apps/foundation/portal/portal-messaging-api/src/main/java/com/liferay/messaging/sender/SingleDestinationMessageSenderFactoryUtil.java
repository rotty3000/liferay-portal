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

package com.liferay.messaging.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Michael C. Han
 */
public class SingleDestinationMessageSenderFactoryUtil {

	public static SingleDestinationMessageSender
		createSingleDestinationMessageSender(String destinationName) {

		return _singleDestinationMessageSenderFactory.
			createSingleDestinationMessageSender(destinationName);
	}

	public static SingleDestinationSynchronousMessageSender
		createSingleDestinationSynchronousMessageSender(
			String destinationName, SynchronousMessageSender.Mode mode) {

		return _singleDestinationMessageSenderFactory.
			createSingleDestinationSynchronousMessageSender(
				destinationName, mode);
	}

	public static int getModeCount() {
		return _singleDestinationMessageSenderFactory.getModesCount();
	}

	public static SynchronousMessageSender getSynchronousMessageSender(
		SynchronousMessageSender.Mode mode) {

		return _singleDestinationMessageSenderFactory.
			getSynchronousMessageSender(mode);
	}

	protected SingleDestinationMessageSenderFactoryUtil() {
	}

	/**
	 * @deprecated As of 1.0.0, with no direct replacement
	 */
	@Deprecated
	protected SingleDestinationMessageSenderFactory
		getSingleDestinationMessageSenderFactory() {

		return _singleDestinationMessageSenderFactory;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		SingleDestinationMessageSenderFactoryUtil.class);

	private static volatile SingleDestinationMessageSenderFactory
		_singleDestinationMessageSenderFactory =
			ServiceProxyFactory.newServiceTrackedInstance(
				SingleDestinationMessageSenderFactory.class,
				SingleDestinationMessageSenderFactoryUtil.class,
				"_singleDestinationMessageSenderFactory", true);

}
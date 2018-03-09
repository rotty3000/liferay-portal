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

package com.liferay.portal.kernel.messaging;

/**
 * @author Shuyang Zhou
 */
public interface MessageBusEventListener extends com.liferay.petra.messaging.api.MessageBusEventListener {

	public void destinationAdded(Destination destination);

	public default void destinationAdded(com.liferay.petra.messaging.api.Destination destination) {
		destinationAdded((Destination)destination);
	}

	public void destinationRemoved(Destination destination);

	public default void destinationRemoved(com.liferay.petra.messaging.api.Destination destination) {
		destinationRemoved((Destination)destination);
	}

}
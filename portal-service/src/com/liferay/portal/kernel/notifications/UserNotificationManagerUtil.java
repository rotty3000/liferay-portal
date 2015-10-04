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

package com.liferay.portal.kernel.notifications;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.UserNotificationDeliveryConstants;
import com.liferay.portal.model.UserNotificationEvent;
import com.liferay.portal.service.ServiceContext;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Map;

/**
 * @author Jonathan Lee
 * @author Roberto Díaz
 */
public class UserNotificationManagerUtil {

	public static UserNotificationDefinition fetchUserNotificationDefinition(
		String portletId, long classNameId, int notificationType) {

		return _instance._fetchUserNotificationDefinition(
			portletId, classNameId, notificationType);
	}

	public static Map<String, List<UserNotificationDefinition>>
		getUserNotificationDefinitions() {

		Map<String, List<UserNotificationDefinition>>
			userNotificationDefinitionsMap = new ConcurrentHashMap<>();

		ServiceTrackerMap<String, List<UserNotificationDefinition>>
			userNotificationDefinitionsServiceTrackerMap =
				_instance._userNotificationDefinitions;

		for (String portletId :
				userNotificationDefinitionsServiceTrackerMap.keySet()) {

			userNotificationDefinitionsMap.put(
				portletId,
				userNotificationDefinitionsServiceTrackerMap.getService(
					portletId));
		}

		return Collections.unmodifiableMap(userNotificationDefinitionsMap);
	}

	public static Map<String, Map<String, UserNotificationHandler>>
		getUserNotificationHandlers() {

		return Collections.unmodifiableMap(_instance._userNotificationHandlers);
	}

	public static UserNotificationFeedEntry interpret(
			String selector, UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws PortalException {

		return _instance._interpret(
			selector, userNotificationEvent, serviceContext);
	}

	public static boolean isDeliver(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType)
		throws PortalException {

		return _instance._isDeliver(
			userId, StringPool.BLANK, portletId, classNameId, notificationType,
			deliveryType, null);
	}

	public static boolean isDeliver(
			long userId, String selector, String portletId, long classNameId,
			int notificationType, int deliveryType,
			ServiceContext serviceContext)
		throws PortalException {

		return _instance._isDeliver(
			userId, selector, portletId, classNameId, notificationType,
			deliveryType, serviceContext);
	}

	private UserNotificationManagerUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_userNotificationHandlerServiceTracker = registry.trackServices(
			UserNotificationHandler.class,
			new UserNotificationHandlerServiceTrackerCustomizer());

		_userNotificationHandlerServiceTracker.open();

		_userNotificationDefinitions.open();
	}

	private UserNotificationDefinition _fetchUserNotificationDefinition(
		String portletId, long classNameId, int notificationType) {

		List<UserNotificationDefinition> userNotificationDefinitions =
			_userNotificationDefinitions.getService(portletId);

		if (userNotificationDefinitions == null) {
			return null;
		}

		for (UserNotificationDefinition userNotificationDefinition :
				userNotificationDefinitions) {

			if ((userNotificationDefinition.getClassNameId() == classNameId) &&
				(userNotificationDefinition.getNotificationType() ==
					notificationType)) {

				return userNotificationDefinition;
			}
		}

		return null;
	}

	private UserNotificationFeedEntry _interpret(
			String selector, UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, UserNotificationHandler> userNotificationHandlers =
			_userNotificationHandlers.get(selector);

		if (userNotificationHandlers == null) {
			return null;
		}

		UserNotificationHandler userNotificationHandler =
			userNotificationHandlers.get(userNotificationEvent.getType());

		if (userNotificationHandler == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No interpreter found for " + userNotificationEvent);
			}

			return null;
		}

		return userNotificationHandler.interpret(
			userNotificationEvent, serviceContext);
	}

	private boolean _isDeliver(
			long userId, String selector, String portletId, long classNameId,
			int notificationType, int deliveryType,
			ServiceContext serviceContext)
		throws PortalException {

		Map<String, UserNotificationHandler> userNotificationHandlers =
			_userNotificationHandlers.get(selector);

		if (userNotificationHandlers == null) {
			return false;
		}

		UserNotificationHandler userNotificationHandler =
			userNotificationHandlers.get(portletId);

		if (userNotificationHandler == null) {
			if (deliveryType == UserNotificationDeliveryConstants.TYPE_EMAIL) {
				return true;
			}

			return false;
		}

		return userNotificationHandler.isDeliver(
			userId, classNameId, notificationType, deliveryType,
			serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserNotificationManagerUtil.class);

	private static final UserNotificationManagerUtil _instance =
		new UserNotificationManagerUtil();

	private final ServiceTrackerMap<String, List<UserNotificationDefinition>>
		_userNotificationDefinitions = ServiceTrackerCollections.multiValueMap(
			UserNotificationDefinition.class, "javax.portlet.name");
	private final Map<String, Map<String, UserNotificationHandler>>
		_userNotificationHandlers = new ConcurrentHashMap<>();
	private final
		ServiceTracker<UserNotificationHandler, UserNotificationHandler>
			_userNotificationHandlerServiceTracker;

	private class UserNotificationHandlerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<UserNotificationHandler, UserNotificationHandler> {

		@Override
		public UserNotificationHandler addingService(
			ServiceReference<UserNotificationHandler> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			UserNotificationHandler userNotificationHandler =
				registry.getService(serviceReference);

			String selector = userNotificationHandler.getSelector();

			Map<String, UserNotificationHandler> userNotificationHandlers =
				_userNotificationHandlers.get(selector);

			if (userNotificationHandlers == null) {
				userNotificationHandlers = new HashMap<>();

				_userNotificationHandlers.put(
					selector, userNotificationHandlers);
			}

			userNotificationHandlers.put(
				userNotificationHandler.getPortletId(),
				userNotificationHandler);

			return userNotificationHandler;
		}

		@Override
		public void modifiedService(
			ServiceReference<UserNotificationHandler> serviceReference,
			UserNotificationHandler userNotificationHandler) {
		}

		@Override
		public void removedService(
			ServiceReference<UserNotificationHandler> serviceReference,
			UserNotificationHandler userNotificationHandler) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			Map<String, UserNotificationHandler> userNotificationHandlers =
				_userNotificationHandlers.get(
					userNotificationHandler.getSelector());

			if (userNotificationHandlers == null) {
				return;
			}

			userNotificationHandlers.remove(
				userNotificationHandler.getPortletId());
		}

	}

}
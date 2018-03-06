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

package com.liferay.portal.servlet;

import com.liferay.petra.messaging.api.DestinationNames;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.api.MessageBuilderFactory;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthenticatedUserUUIDStoreUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.BasePortalLifecycle;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

/**
 * @author Michael Young
 */
public class PortalSessionDestroyer extends BasePortalLifecycle {

	public PortalSessionDestroyer(HttpSession httpSession) {
		_httpSession = httpSession;

		registerPortalLifecycle(METHOD_INIT);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #PortalSessionDestroyer(HttpSession)}
	 */
	@Deprecated
	public PortalSessionDestroyer(HttpSessionEvent httpSessionEvent) {
		this(httpSessionEvent.getSession());
	}

	@Override
	protected void doPortalDestroy() {
	}

	@Override
	protected void doPortalInit() {
		if (PropsValues.SESSION_DISABLED) {
			return;
		}

		PortalSessionContext.remove(_httpSession.getId());

		try {
			Long userIdObj = (Long)_httpSession.getAttribute(WebKeys.USER_ID);

			if (userIdObj == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("User id is not in the session");
				}
			}

			if (userIdObj == null) {
				return;
			}

			// Live users

			if (PropsValues.LIVE_USERS_ENABLED) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				ClusterNode clusterNode =
					ClusterExecutorUtil.getLocalClusterNode();

				if (clusterNode != null) {
					jsonObject.put(
						"clusterNodeId", clusterNode.getClusterNodeId());
				}

				jsonObject.put("command", "signOut");

				long userId = userIdObj.longValue();

				long companyId = CompanyLocalServiceUtil.getCompanyIdByUserId(
					userId);

				jsonObject.put("companyId", companyId);

				jsonObject.put("sessionId", _httpSession.getId());
				jsonObject.put("userId", userId);

				MessageBuilderFactory messageBuilderFactory =
					getMessageBuilderFactory();

				MessageBuilder messageBuilder =
					messageBuilderFactory.create(DestinationNames.LIVE_USERS);

				messageBuilder.setPayload(jsonObject.toString());

				messageBuilder.send();
			}

			if (PropsValues.AUTH_USER_UUID_STORE_ENABLED) {
				String userUUID = (String)_httpSession.getAttribute(
					WebKeys.USER_UUID);

				if (Validator.isNotNull(userUUID)) {
					AuthenticatedUserUUIDStoreUtil.unregister(userUUID);
				}
			}
		}
		catch (IllegalStateException ise) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Please upgrade to a Servlet 2.4 compliant container");
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Process session destroyed events

		try {
			EventsProcessorUtil.process(
				PropsKeys.SERVLET_SESSION_DESTROY_EVENTS,
				PropsValues.SERVLET_SESSION_DESTROY_EVENTS, _httpSession);
		}
		catch (ActionException ae) {
			_log.error(ae, ae);
		}
	}

	private MessageBuilderFactory getMessageBuilderFactory() {
		try {
			ServiceTracker<MessageBuilderFactory, MessageBuilderFactory>
				messageBuilderFactoryTracker = getMessageBuilderFactoryTracker();

			MessageBuilderFactory messageBuilderFactory =
				messageBuilderFactoryTracker.waitForService(_timeout);

			return messageBuilderFactory;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	private ServiceTracker<MessageBuilderFactory, MessageBuilderFactory> getMessageBuilderFactoryTracker() {
		Registry registry = RegistryUtil.getRegistry();

		com.liferay.registry.Filter filter = registry.getFilter(
			"(objectClass=com.liferay.petra.messaging.api.MessageBuilderFactory)");

		ServiceTracker<MessageBuilderFactory, MessageBuilderFactory> messageBuilderFactoryTracker =
			registry.trackServices(filter);

		return messageBuilderFactoryTracker;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalSessionDestroyer.class);

	private static final int _timeout = 1000;

	private final HttpSession _httpSession;

}
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.web.extender.internal.event;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.web.extender.internal.servlet.BundleServletContext;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class EventUtil
	implements ModuleFrameworkConstants,
		ServiceTrackerCustomizer<EventAdmin, EventAdmin> {

	public static final String DEPLOYED = "org/osgi/service/web/DEPLOYED";
	public static final String DEPLOYING = "org/osgi/service/web/DEPLOYING";
	public static final String FAILED = "org/osgi/service/web/FAILED";
	public static final String UNDEPLOYED = "org/osgi/service/web/UNDEPLOYED";
	public static final String UNDEPLOYING = "org/osgi/service/web/UNDEPLOYING";

	public static final String[] TOPICS = new String[] {
		DEPLOYED, DEPLOYING, FAILED, UNDEPLOYED, UNDEPLOYING
	};

	public static void close() {
		_instance._close();

		_instance = null;
	}

	public static void sendEvent(
		Bundle bundle, String eventTopic, Exception exception,
		boolean collision) {

		_instance._sendEvent(bundle, eventTopic, exception, collision);
	}

	public static void start(BundleContext bundleContext) {
		if (_instance != null) {
			return;
		}

		_instance = new EventUtil();

		_instance._start(bundleContext);
	}

	private EventUtil() {
	}

	public EventAdmin addingService(ServiceReference<EventAdmin> reference) {
		_eventAdmin = _bundleContext.getService(reference);

		return _eventAdmin;
	}

	public void modifiedService(
		ServiceReference<EventAdmin> reference, EventAdmin service) {

		// not needed
	}

	public void removedService(
		ServiceReference<EventAdmin> reference, EventAdmin service) {

		_eventAdmin = null;
	}

	public void _close() {
		_eventAdminTracker.close();

		_bundleContext = null;
		_eventAdminTracker = null;
		_webExtenderBundle = null;
	}

	public void _sendEvent(
		Bundle bundle, String eventTopic, Exception exception,
		boolean collision) {

		Dictionary<String,String> headers = bundle.getHeaders();
		String contextPath = headers.get(WEB_CONTEXTPATH);
		String servletContextName = BundleServletContext.getServletContextName(
			bundle);

		Map<String, Object> properties = new Hashtable<String, Object>();

		properties.put(BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
		properties.put(BUNDLE_ID, bundle.getBundleId());
		properties.put(BUNDLE, bundle);
		properties.put(BUNDLE_VERSION, bundle.getVersion());

		if (collision) {
			properties.put(COLLISION, headers.get(WEB_CONTEXTPATH));

			List<String> collidedIds = new ArrayList<String>();

			for (Bundle curBundle : bundle.getBundleContext().getBundles()) {
				if (curBundle.equals(bundle) ||
					(curBundle.getState() != Bundle.ACTIVE)) {

					continue;
				}

				Dictionary<String,String> curHeaders = bundle.getHeaders();

				String curContextPath = curHeaders.get(WEB_CONTEXTPATH);

				if ((curContextPath != null) &&
					curContextPath.equals(contextPath)) {

					collidedIds.add(String.valueOf(curBundle.getBundleId()));
				}
			}

			properties.put(COLLISION_BUNDLES, collidedIds);
		}

		properties.put(CONTEXT_PATH, contextPath);
		properties.put(EXTENDER_BUNDLE, _webExtenderBundle);
		properties.put(EXTENDER_BUNDLE_ID, _webExtenderBundle.getBundleId());
		properties.put(
			EXTENDER_BUNDLE_SYMBOLICNAME, _webExtenderBundle.getSymbolicName());
		properties.put(
			EXTENDER_BUNDLE_VERSION, _webExtenderBundle.getVersion());

		if (exception != null) {
			properties.put(EXCEPTION, exception);
		}

		properties.put(SERVLET_CONTEXT_NAME, servletContextName);
		properties.put(TIMESTAMP, System.currentTimeMillis());

		Event event = new Event(eventTopic, properties);

		if (_log.isInfoEnabled()) {
			_log.info(event);
		}

		if (_eventAdmin == null) {
			return;
		}

		_eventAdmin.sendEvent(event);
	}

	private void _start(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_webExtenderBundle = _bundleContext.getBundle();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put(EventConstants.EVENT_TOPIC, TOPICS);

		_eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(
			_bundleContext, EventAdmin.class.getName(), this);

		_eventAdminTracker.open();
	}

	private static EventUtil _instance;

	private static Log _log = LogFactoryUtil.getLog(EventUtil.class);

	private BundleContext _bundleContext;
	private EventAdmin _eventAdmin;
	private ServiceTracker<EventAdmin, EventAdmin> _eventAdminTracker;
	private Bundle _webExtenderBundle;

}
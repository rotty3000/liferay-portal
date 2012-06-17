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

package com.liferay.web.extender;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.module.framework.MFConstants;
import com.liferay.web.extender.servlet.BundleServletContext;

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
public class WebEventsUtil
	implements ServiceTrackerCustomizer<EventAdmin, EventAdmin> {

	public static final String DEPLOYED = "org/osgi/service/web/DEPLOYED";
	public static final String DEPLOYING = "org/osgi/service/web/DEPLOYING";
	public static final String FAILED = "org/osgi/service/web/FAILED";
	public static final String UNDEPLOYED = "org/osgi/service/web/UNDEPLOYED";
	public static final String UNDEPLOYING = "org/osgi/service/web/UNDEPLOYING";

	public static final String[] TOPICS = new String[] {
		DEPLOYED, DEPLOYING, FAILED, UNDEPLOYED, UNDEPLOYING
	};

	public WebEventsUtil(
		BundleContext bundleContext) {

		_bundleContext = bundleContext;
		_webExtenderBundle = _bundleContext.getBundle();

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put(EventConstants.EVENT_TOPIC, TOPICS);

		_eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(
			_bundleContext, EventAdmin.class.getName(), this);

		_eventAdminTracker.open();
	}

	public EventAdmin addingService(ServiceReference<EventAdmin> reference) {
		_eventAdmin = _bundleContext.getService(reference);

		return _eventAdmin;
	}

	public void close() {
		_eventAdminTracker.close();

		_eventAdminTracker = null;
	}

	public void modifiedService(
		ServiceReference<EventAdmin> reference, EventAdmin service) {
	}

	public void removedService(
		ServiceReference<EventAdmin> reference, EventAdmin service) {

		_eventAdmin = null;

		_bundleContext.ungetService(reference);
	}

	public void sendEvent(
		Bundle bundle, String eventTopic, Exception exception,
		boolean collision) {

		Dictionary<String,String> headers = bundle.getHeaders();
		String contextPath = headers.get(MFConstants.WEB_CONTEXTPATH);
		String servletContextName = BundleServletContext.getServletContextName(
			bundle);

		Map<String, Object> properties = new Hashtable<String, Object>();

		properties.put("bundle.symbolicName", bundle.getSymbolicName());
		properties.put("bundle.id", bundle.getBundleId());
		properties.put("bundle", bundle);
		properties.put("bundle.version", bundle.getVersion());
		properties.put("context.path", contextPath);
		properties.put("servlet.context.name", servletContextName);
		properties.put("timestamp", System.currentTimeMillis());
		properties.put("extender.bundle", _webExtenderBundle);
		properties.put("extender.bundle.id", _webExtenderBundle.getBundleId());
		properties.put(
			"extender.bundle.symbolicName", _webExtenderBundle.getBundleId());
		properties.put(
			"extender.bundle.version", _webExtenderBundle.getVersion());

		if (exception != null) {
			properties.put("exception", exception);
		}

		if (collision) {
			properties.put(
				"collision", headers.get(MFConstants.WEB_CONTEXTPATH));

			List<String> collidedIds = new ArrayList<String>();

			for (Bundle curBundle : bundle.getBundleContext().getBundles()) {
				Dictionary<String,String> curHeaders = bundle.getHeaders();

				String curContextPath = curHeaders.get(
					MFConstants.WEB_CONTEXTPATH);

				if ((curContextPath != null) &&
					curContextPath.equals(contextPath)) {

					collidedIds.add(String.valueOf(curBundle.getBundleId()));
				}
			}

			properties.put("collision.bundles", collidedIds);
		}

		Event event = new Event(eventTopic, properties);

		if (_log.isInfoEnabled()) {
			_log.info(event);
		}

		if (_eventAdmin == null) {
			return;
		}

		_eventAdmin.sendEvent(event);
	}

	private static ServiceTracker<EventAdmin, EventAdmin> _eventAdminTracker;

	private static Log _log = LogFactoryUtil.getLog(WebEventsUtil.class);

	private BundleContext _bundleContext;
	private EventAdmin _eventAdmin;
	private Bundle _webExtenderBundle;

}
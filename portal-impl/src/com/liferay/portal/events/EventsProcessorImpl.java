/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InstancePool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WeakValueConcurrentHashMap;
import com.liferay.portal.service.registry.Filter;
import com.liferay.portal.service.registry.ServiceRegistration;
import com.liferay.portal.service.registry.ServiceRegistryUtil;
import com.liferay.portal.service.registry.ServiceTracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Young
 */
public class EventsProcessorImpl implements EventsProcessor {

	public void process(
			String key, String[] classes, String[] ids,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session)
		throws ActionException {

		if (!_eventsMap.containsKey(key)) {
			for (int i = 0; i < classes.length; i++) {
				String className = classes[i];

				if (Validator.isNull(className)) {
					continue;
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Registering event " + className);
				}

				Object event = InstancePool.get(className);

				Map<String, Object> map = new HashMap<String, Object>();

				map.put("service.ranking", (classes.length - i) * 1000);

				registerEvent(key, event, map);
			}
		}

		Collection<Object> events = _getEvents(key);

		for (Object event : events) {
			processEvent(event, ids, request, response, session);
		}
	}

	public void processEvent(
			Object event, String[] ids, HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
		throws ActionException {

		if (event instanceof Action) {
			Action action = (Action)event;

			try {
				action.run(request, response);
			}
			catch (ActionException ae) {
				throw ae;
			}
			catch (Exception e) {
				throw new ActionException(e);
			}
		}
		else if (event instanceof SessionAction) {
			SessionAction sessionAction = (SessionAction)event;

			try {
				sessionAction.run(session);
			}
			catch (ActionException ae) {
				throw ae;
			}
			catch (Exception e) {
				throw new ActionException(e);
			}
		}
		else if (event instanceof SimpleAction) {
			SimpleAction simpleAction = (SimpleAction)event;

			simpleAction.run(ids);
		}
	}

	public void registerEvent(String key, Object event) {
		Map<String, Object> map = new HashMap<String, Object>();

		registerEvent(key, event, map);
	}

	public void registerEvent(
		String key, Object event, Map<String, Object> map) {

		map.put("lifecycle.event", key);

		ServiceRegistration<?> serviceRegistration =
			ServiceRegistryUtil.registerService(
				event.getClass().getName(), event, map);

		Map<Object, ServiceRegistration<?>> serviceRegistrations =
			_eventsMap.get(key);

		if (serviceRegistrations == null) {
			serviceRegistrations =
				new ConcurrentHashMap<Object, ServiceRegistration<?>>();

			_eventsMap.put(key, serviceRegistrations);
		}

		serviceRegistrations.put(event, serviceRegistration);
	}

	public void unregisterEvent(String key, Object event) {
		Map<Object, ServiceRegistration<?>> serviceRegistrations =
			_eventsMap.get(key);

		if (serviceRegistrations == null) {
			return;
		}

		ServiceRegistration<?> serviceRegistration =
			serviceRegistrations.remove(event);

		if (serviceRegistration == null) {
			return;
		}

		serviceRegistration.unregister();
	}

	private Collection<Object> _getEvents(String key) {
		ServiceTracker<?, ?> serviceTracker = _trackerMap.get(key);

		if (serviceTracker != null) {
			return _sortedListServices(serviceTracker);
		}

		StringBundler sb = new StringBundler();

		sb.append("(lifecycle.event=");
		sb.append(key);
		sb.append(")");

		Filter filter = null;

		try {
			filter = ServiceRegistryUtil.getFilter(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		serviceTracker = ServiceRegistryUtil.trackServices(filter);

		serviceTracker.open();

		_trackerMap.put(key, serviceTracker);

		return _sortedListServices(serviceTracker);
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> _sortedListServices(
		ServiceTracker<?, ?> serviceTracker) {

		SortedMap<?, ?> tracked = serviceTracker.getTracked();

		return (Collection<Object>)tracked.values();
	}

	private static Log _log = LogFactoryUtil.getLog(EventsProcessorImpl.class);

	private Map<String, Map<Object, ServiceRegistration<?>>> _eventsMap =
		new ConcurrentHashMap<String, Map<Object, ServiceRegistration<?>>>();
	private Map<String, ServiceTracker<?, ?>> _trackerMap =
		new WeakValueConcurrentHashMap<String, ServiceTracker<?, ?>>();

}
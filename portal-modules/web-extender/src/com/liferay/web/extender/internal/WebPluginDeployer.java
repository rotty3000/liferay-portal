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

package com.liferay.web.extender.internal;

import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.web.extender.internal.event.EventUtil;
import com.liferay.web.extender.internal.servlet.BundleServletContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Raymond Augé
 */
public class WebPluginDeployer
	implements BundleListener, ModuleFrameworkConstants {

	public WebPluginDeployer(
			BundleContext bundleContext, ServletContext portalServletContext,
			Servlet portletServlet)
		throws Exception {

		_bundleContext = bundleContext;
		_portalServletContext = portalServletContext;
		_portletServlet = portletServlet;
		_trackedRegistrations =
			new ConcurrentHashMap<String, ServiceRegistration<ServletContext>>();
		_eventUtil = new EventUtil(bundleContext);
		_trackedContexts =
			new ConcurrentHashMap<String, BundleServletContext>();
	}

	public void bundleChanged(BundleEvent bundleEvent) {
		int type = bundleEvent.getType();

		Bundle bundle = bundleEvent.getBundle();

		String servletContextName = BundleServletContext.getServletContextName(
			bundle);

		if (Validator.isNull(servletContextName)) {
			return;
		}

		try {
			if (type == BundleEvent.STARTED) {
				doStart(bundle, servletContextName);
			}
			else if (type == BundleEvent.STOPPED) {
				doStop(bundle, servletContextName);
			}
			else {
				_log.info("Did we miss something? " + bundleEvent.toString());
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	public void close() {
		for (Map.Entry<String, BundleServletContext> entry :
				_trackedContexts.entrySet()) {

			String servletContextName = entry.getKey();
			BundleServletContext bundleServletContext = entry.getValue();

			Bundle bundle = bundleServletContext.getBundle();

			doStop(bundle, servletContextName);
		}

		_portletServlet = null;
		_trackedContexts.clear();
		_trackedContexts = null;
		_trackedRegistrations.clear();
		_trackedRegistrations = null;
		_eventUtil.close();
		_eventUtil = null;

		_bundleContext = null;
	}

	public void doStart(Bundle bundle, String servletContextName) {
		if (bundle.getState() != Bundle.ACTIVE) {
			return;
		}

		_eventUtil.sendEvent(bundle, EventUtil.DEPLOYING, null, false);

		ServletContext servletContext = ServletContextPool.get(
			servletContextName);

		if (servletContext != null) {
			_eventUtil.sendEvent(bundle, EventUtil.FAILED, null, true);

			_collidedWabs.add(bundle);

			return;
		}

		BundleServletContext bundleServletContext = null;

		try {
			bundleServletContext = new BundleServletContext(
				bundle, _portalServletContext, _portletServlet);

			Dictionary<String,String> headers = bundle.getHeaders();

			String webContextPath = headers.get(WEB_CONTEXTPATH);

			Hashtable<String, Object> properties =
				new Hashtable<String, Object>();

			properties.put("osgi.web.symbolicname", bundle.getSymbolicName());
			properties.put("osgi.web.version", bundle.getVersion().toString());
			properties.put("osgi.web.contextpath", webContextPath);

			ServiceRegistration<ServletContext> registration =
				_bundleContext.registerService(
					ServletContext.class, bundleServletContext, properties);

			bundleServletContext.open();

			HotDeployUtil.fireDeployEvent(
				new HotDeployEvent(
					bundleServletContext,
					bundleServletContext.getClassLoader(), false));

			_trackedContexts.put(servletContextName, bundleServletContext);
			_trackedRegistrations.put(servletContextName, registration);

			_eventUtil.sendEvent(bundle, EventUtil.DEPLOYED, null, false);
		}
		catch (Exception e) {
			_eventUtil.sendEvent(bundle, EventUtil.FAILED, e, false);
		}
	}

	protected void doStop(Bundle bundle, String servletContextName) {
		_eventUtil.sendEvent(bundle, EventUtil.UNDEPLOYING, null, false);

		BundleServletContext bundleServletContext = _trackedContexts.get(
			servletContextName);
		ServiceRegistration<ServletContext> registration =
			_trackedRegistrations.get(servletContextName);

		if ((bundleServletContext == null) || (registration == null)) {
			return;
		}

		try {
			HotDeployUtil.fireUndeployEvent(
				new HotDeployEvent(
					bundleServletContext, bundleServletContext.getClassLoader(),
					false));
		}
		catch (Exception e) {
			_eventUtil.sendEvent(bundle, EventUtil.FAILED, null, false);
		}

		bundleServletContext.close();
		registration.unregister();

		_trackedContexts.remove(servletContextName);
		_trackedRegistrations.remove(servletContextName);

		_eventUtil.sendEvent(bundle, EventUtil.UNDEPLOYED, null, false);
	}

	protected void handleCollidedWabs(String servletContextName) {
		if (_collidedWabs.isEmpty()) {
			return;
		}

		Bundle candidate = null;

		Iterator<Bundle> iterator = _collidedWabs.iterator();

		while (iterator.hasNext()) {
			Bundle collidedWab = iterator.next();

			String curServletContextName =
				BundleServletContext.getServletContextName(collidedWab);

			if (servletContextName.equals(curServletContextName) &&
				((candidate == null) ||
				 (collidedWab.getBundleId() < collidedWab.getBundleId()))) {

				candidate = collidedWab;

				iterator.remove();
			}
		}

		if (candidate != null) {
			doStart(candidate, servletContextName);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebPluginDeployer.class);

	private BundleContext _bundleContext;
	private List<Bundle> _collidedWabs = Collections.synchronizedList(
		new ArrayList<Bundle>());
	private EventUtil _eventUtil;
	private Servlet _portletServlet;
	private ServletContext _portalServletContext;
	private Map<String, BundleServletContext> _trackedContexts;
	private Map<String, ServiceRegistration<ServletContext>>
		_trackedRegistrations;

}
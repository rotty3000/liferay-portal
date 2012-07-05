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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.web.extender.internal.event.EventUtil;
import com.liferay.web.extender.internal.servlet.BundleServletContext;
import com.liferay.web.extender.internal.servlet.WebExtenderServlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WebBundleDeployer
	implements BundleListener, ModuleFrameworkConstants {

	public WebBundleDeployer(WebExtenderServlet webExtenderServlet)
		throws Exception {

		_trackedContexts =
			new ConcurrentHashMap<String, BundleServletContext>();
		_trackedRegistrations =
			new ConcurrentHashMap<String, ServiceRegistration<ServletContext>>();
		_webExtenderServlet = webExtenderServlet;
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

		_webExtenderServlet = null;
		_trackedContexts.clear();
		_trackedContexts = null;
		_trackedRegistrations.clear();
		_trackedRegistrations = null;
	}

	public void doStart(Bundle bundle, String servletContextName) {
		if (bundle.getState() != Bundle.ACTIVE) {
			return;
		}

		EventUtil.sendEvent(bundle, EventUtil.DEPLOYING, null, false);

		ServletContext servletContext = ServletContextPool.get(
			servletContextName);

		if (servletContext != null) {
			EventUtil.sendEvent(bundle, EventUtil.FAILED, null, true);

			_collidedWabs.add(bundle);

			return;
		}

		BundleServletContext bundleServletContext = null;

		try {
			bundleServletContext = new BundleServletContext(
				bundle, _webExtenderServlet);

			Dictionary<String,String> headers = bundle.getHeaders();

			String webContextPath = headers.get(WEB_CONTEXTPATH);

			Hashtable<String, Object> properties =
				new Hashtable<String, Object>();

			properties.put("osgi.web.symbolicname", bundle.getSymbolicName());
			properties.put("osgi.web.version", bundle.getVersion().toString());
			properties.put("osgi.web.contextpath", webContextPath);

			ServiceRegistration<ServletContext> registration =
				Activator.getBundleContext().registerService(
					ServletContext.class, bundleServletContext, properties);

			// register in the current thread that the deployment process
			// has been started in the Module Framework

			// This is required in order to keep both deployment
			// mechanism

			bundleServletContext.open();

			_trackedContexts.put(servletContextName, bundleServletContext);
			_trackedRegistrations.put(servletContextName, registration);

			EventUtil.sendEvent(bundle, EventUtil.DEPLOYED, null, false);
		}
		catch (Exception e) {
			EventUtil.sendEvent(bundle, EventUtil.FAILED, e, false);
		}
	}

	protected void doStop(Bundle bundle, String servletContextName) {
		EventUtil.sendEvent(bundle, EventUtil.UNDEPLOYING, null, false);

		BundleServletContext bundleServletContext = _trackedContexts.get(
			servletContextName);
		ServiceRegistration<ServletContext> registration =
			_trackedRegistrations.get(servletContextName);

		if ((bundleServletContext == null) || (registration == null)) {
			EventUtil.sendEvent(bundle, EventUtil.UNDEPLOYED, null, false);

			return;
		}

		try {
			bundleServletContext.close();
		}
		catch (Exception e) {
			EventUtil.sendEvent(bundle, EventUtil.FAILED, null, false);
		}

		registration.unregister();

		_trackedContexts.remove(servletContextName);
		_trackedRegistrations.remove(servletContextName);

		EventUtil.sendEvent(bundle, EventUtil.UNDEPLOYED, null, false);
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
		WebBundleDeployer.class);

	private List<Bundle> _collidedWabs = Collections.synchronizedList(
		new ArrayList<Bundle>());
	private Map<String, BundleServletContext> _trackedContexts;
	private Map<String, ServiceRegistration<ServletContext>>
		_trackedRegistrations;
	private WebExtenderServlet _webExtenderServlet;

}
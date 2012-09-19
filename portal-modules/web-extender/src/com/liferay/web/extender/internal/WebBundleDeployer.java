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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class WebBundleDeployer implements ModuleFrameworkConstants {

	public WebBundleDeployer(WebExtenderServlet webExtenderServlet)
		throws Exception {

		_webExtenderServlet = webExtenderServlet;
	}

	public void close() {
		BundleContext bundleContext = _webExtenderServlet.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String servletContextName =
				BundleServletContext.getServletContextName(bundle);

			if (Validator.isNotNull(servletContextName)) {
				try {
					doStop(bundle, servletContextName);
				}
				catch (Exception e) {
					_log.error(e, e);
				}
			}
		}

		_webExtenderServlet = null;
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

			_collidedWabBundleIds.add(bundle.getBundleId());

			return;
		}

		try {
			BundleServletContext bundleServletContext =
				new BundleServletContext(bundle, _webExtenderServlet);

			bundleServletContext.open();

			EventUtil.sendEvent(bundle, EventUtil.DEPLOYED, null, false);
		}
		catch (Exception e) {
			EventUtil.sendEvent(bundle, EventUtil.FAILED, e, false);
		}
	}

	protected void doStop(Bundle bundle, String servletContextName) {
		EventUtil.sendEvent(bundle, EventUtil.UNDEPLOYING, null, false);

		BundleServletContext bundleServletContext = null;

		ServletContext servletContext = ServletContextPool.get(
			servletContextName);

		if ((servletContext != null) &&
			(servletContext instanceof BundleServletContext)) {

			bundleServletContext = (BundleServletContext)servletContext;
		}

		if (bundleServletContext == null) {
			EventUtil.sendEvent(bundle, EventUtil.UNDEPLOYED, null, false);

			return;
		}

		try {
			bundleServletContext.close();
		}
		catch (Exception e) {
			EventUtil.sendEvent(bundle, EventUtil.FAILED, null, false);
		}

		EventUtil.sendEvent(bundle, EventUtil.UNDEPLOYED, null, false);

		handleCollidedWabs(bundle, servletContextName);
	}

	protected void handleCollidedWabs(
		Bundle bundle, String servletContextName) {

		if (_collidedWabBundleIds.isEmpty()) {
			return;
		}

		Iterator<Long> iterator = _collidedWabBundleIds.iterator();

		BundleContext bundleContext = _webExtenderServlet.getBundleContext();

		while (iterator.hasNext()) {
			long bundleId = iterator.next();
			Bundle candidate = bundleContext.getBundle(bundleId);

			if (candidate == null) {
				iterator.remove();

				continue;
			}

			String curServletContextName =
				BundleServletContext.getServletContextName(candidate);

			if (servletContextName.equals(curServletContextName) &&
				(bundle.getBundleId() != candidate.getBundleId())) {

				iterator.remove();

				doStart(candidate, servletContextName);

				break;
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebBundleDeployer.class);

	private List<Long> _collidedWabBundleIds = Collections.synchronizedList(
		new ArrayList<Long>());
	private WebExtenderServlet _webExtenderServlet;

}
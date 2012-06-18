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
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.struts.StrutsActionRegistryUtil;
import com.liferay.web.extender.internal.http.PortalHttpContext;
import com.liferay.web.extender.internal.servlet.BundleServletConfig;
import com.liferay.web.extender.internal.servlet.BundleServletContext;
import com.liferay.web.extender.internal.servlet.WebExtenderServlet;
import com.liferay.web.extender.internal.webbundle.WebBundleURLStreamHandlerService;

import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class Activator
	implements BundleActivator, ModuleFrameworkConstants,
		ServiceTrackerCustomizer<ServletContext, ServletContext> {

	public ServletContext addingService(
		ServiceReference<ServletContext> serviceReference) {

		ServletContext servletContext = _bundleContext.getService(
			serviceReference);

		Hashtable<String, Object> handlerProperties =
			new Hashtable<String, Object>();

		handlerProperties.put(
			URLConstants.URL_HANDLER_PROTOCOL, new String[] {"webbundle"});

		Bundle systemBundle = _bundleContext.getBundle(0);

		ClassLoader classLoader = systemBundle.getClass().getClassLoader();

		_ushRegistration = _bundleContext.registerService(
			URLStreamHandlerService.class.getName(),
			new WebBundleURLStreamHandlerService(classLoader),
			handlerProperties);

		Hashtable<String, String> servletProperties =
			new Hashtable<String, String>();

		servletProperties.put(BEAN_ID, WebExtenderServlet.class.getName());
		servletProperties.put(ORIGINAL_BEAN, Boolean.TRUE.toString());
		servletProperties.put(SERVICE_VENDOR, ReleaseInfo.getVendor());

		ServletConfig servletConfig = new BundleServletConfig(
			servletContext, WebExtenderServlet.NAME, servletProperties,
			new PortalHttpContext(servletContext));

		try {
			_osgiServlet = new WebExtenderServlet(_bundleContext);

			_osgiServlet.init(servletConfig);

			StrutsActionRegistryUtil.register(MODULE_MAPPING, _osgiServlet);

			_webPluginDeployer = new WebPluginDeployer(
				_bundleContext, servletContext, _osgiServlet);

			_bundleContext.addBundleListener(_webPluginDeployer);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		checkStartableBundles();

		return servletContext;
	}

	public void modifiedService(
		ServiceReference<ServletContext> serviceReference,
		ServletContext servletContext) {
	}

	public void removedService(
		ServiceReference<ServletContext> serviceReference,
		ServletContext servletContext) {

		_bundleContext.removeBundleListener(_webPluginDeployer);

		StrutsActionRegistryUtil.unregister(MODULE_MAPPING);

		_webPluginDeployer.close();
		_webPluginDeployer = null;
		_osgiServlet.destroy();
		_osgiServlet = null;
	}

	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		StringBundler sb = new StringBundler(7);

		sb.append("(&(");
		sb.append(BEAN_ID);
		sb.append("=");
		sb.append(ServletContext.class.getName());
		sb.append(")(");
		sb.append(ORIGINAL_BEAN);
		sb.append("=*))");

		Filter filter = bundleContext.createFilter(sb.toString());

		_servletContextTracker =
			new ServiceTracker<ServletContext, ServletContext>(
				bundleContext, filter, this);

		_servletContextTracker.open();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		_servletContextTracker.close();
		_servletContextTracker = null;
		_ushRegistration.unregister();
		_ushRegistration = null;
	}

	protected void checkStartableBundles() {
		for (Bundle bundle : _bundleContext.getBundles()) {
			String servletContextName =
				BundleServletContext.getServletContextName(bundle);

			if (Validator.isNotNull(servletContextName)) {
				try {
					_webPluginDeployer.doStart(bundle, servletContextName);
				}
				catch (Exception e) {
					_log.error(e, e);
				}
			}
		}
	}

	private static Log _log = LogFactoryUtil.getLog(Activator.class);

	private BundleContext _bundleContext;
	private WebExtenderServlet _osgiServlet;
	private ServiceTracker<ServletContext, ServletContext> _servletContextTracker;
	private ServiceRegistration<?> _ushRegistration;
	private WebPluginDeployer _webPluginDeployer;

}
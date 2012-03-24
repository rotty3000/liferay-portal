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
import com.liferay.portal.osgi.OSGiConstants;
import com.liferay.portal.struts.StrutsActionRegistryUtil;
import com.liferay.web.extender.Constants;
import com.liferay.web.extender.internal.webbundle.WebBundleURLStreamHandlerService;
import com.liferay.web.extender.servlet.BundleServletConfig;
import com.liferay.web.extender.servlet.BundleServletContext;

import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class Activator
	implements BundleActivator,
		ServiceTrackerCustomizer<ServletContext, ServletContext> {

	public ServletContext addingService(
		ServiceReference<ServletContext> serviceReference) {

		ServletContext servletContext = _bundleContext.getService(
			serviceReference);

		Hashtable<String, String> properties = new Hashtable<String, String>();

		properties.put(OSGiConstants.BEAN_ID, OSGiServlet.class.getName());
		properties.put(OSGiConstants.ORIGINAL_BEAN, Boolean.TRUE.toString());
		properties.put(OSGiConstants.SERVICE_VENDOR, ReleaseInfo.getVendor());

		ServletConfig servletConfig = new BundleServletConfig(
			servletContext, "OSGi Servlet", properties,
			new PortalHttpContext(servletContext));

		try {
			_osgiServlet = new OSGiServlet(_bundleContext);

			_osgiServlet.init(servletConfig);

			StrutsActionRegistryUtil.register(
				Constants.OSGI_MAPPING, _osgiServlet);

			_webPluginDeployer = new WebPluginDeployer(
				_bundleContext, _osgiServlet);

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

		checkStoppableBundles();

		_bundleContext.removeBundleListener(_webPluginDeployer);

		StrutsActionRegistryUtil.unregister(Constants.OSGI_MAPPING);

		_osgiServlet.destroy();
	}

	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		properties.put(
			URLConstants.URL_HANDLER_PROTOCOL, new String[] {"webbundle"});

		Bundle systemBundle = _bundleContext.getBundle(0);

		ClassLoader classLoader = systemBundle.getClass().getClassLoader();

		bundleContext.registerService(
			URLStreamHandlerService.class.getName(),
			new WebBundleURLStreamHandlerService(classLoader), properties);

		StringBundler sb = new StringBundler(7);

		sb.append("(&(");
		sb.append(OSGiConstants.BEAN_ID);
		sb.append("=");
		sb.append(ServletContext.class.getName());
		sb.append(")(");
		sb.append(OSGiConstants.ORIGINAL_BEAN);
		sb.append("=*))");

		Filter filter = bundleContext.createFilter(sb.toString());

		_servletContextTracker =
			new ServiceTracker<ServletContext, ServletContext>(
				bundleContext, filter, this);

		_servletContextTracker.open();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		_servletContextTracker.close();
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

	protected void checkStoppableBundles() {
		for (final Bundle bundle : _bundleContext.getBundles()) {
			String servletContextName =
				BundleServletContext.getServletContextName(bundle);

			if (Validator.isNotNull(servletContextName)) {
				try {
					_webPluginDeployer.doStop(bundle, servletContextName);
				}
				catch (Exception e) {
					_log.error(e, e);
				}
			}
		}
	}

	private static Log _log = LogFactoryUtil.getLog(Activator.class);

	private BundleContext _bundleContext;
	private OSGiServlet _osgiServlet;
	private ServiceTracker<ServletContext, ServletContext> _servletContextTracker;
	private WebPluginDeployer _webPluginDeployer;

}
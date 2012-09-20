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
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.struts.StrutsActionRegistryUtil;
import com.liferay.web.extender.internal.artifact.WarArtifactListener;
import com.liferay.web.extender.internal.event.EventUtil;
import com.liferay.web.extender.internal.http.PortalHttpContext;
import com.liferay.web.extender.internal.servlet.BundleServletContext;
import com.liferay.web.extender.internal.servlet.WebExtenderServlet;
import com.liferay.web.extender.internal.webbundle.WebBundleURLStreamHandlerService;
import com.liferay.web.extender.servlet.BundleServletConfig;

import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.felix.fileinstall.ArtifactUrlTransformer;

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

		BundleContext bundleContext = getBundleContext();

		ServletContext servletContext = bundleContext.getService(
			serviceReference);

		Hashtable<String, Object> handlerProperties =
			new Hashtable<String, Object>();

		handlerProperties.put(
			URLConstants.URL_HANDLER_PROTOCOL, new String[] {"webbundle"});

		Bundle systemBundle = bundleContext.getBundle(0);

		ClassLoader classLoader = systemBundle.getClass().getClassLoader();

		bundleContext.registerService(
			URLStreamHandlerService.class.getName(),
			new WebBundleURLStreamHandlerService(classLoader),
			handlerProperties);

		ServletConfig servletConfig = new BundleServletConfig(
			servletContext, WebExtenderServlet.NAME, null,
			new PortalHttpContext(servletContext));

		try {
			_webExtenderServlet = new WebExtenderServlet(bundleContext);

			_webExtenderServlet.init(servletConfig);

			StrutsActionRegistryUtil.register(
				MODULE_MAPPING, _webExtenderServlet);

			_webPluginDeployer = new WebBundleDeployer(_webExtenderServlet);

			_bundleStartListener = new BundleStartListener(_webPluginDeployer);
			_bundleStopListener = new BundleStopListener(_webPluginDeployer);

			bundleContext.addBundleListener(_bundleStartListener);
			bundleContext.addBundleListener(_bundleStopListener);
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		Hashtable<String, Object> properties = new Hashtable<String, Object>();

		_artifactUrlTransformerRegistration = bundleContext.registerService(
			ArtifactUrlTransformer.class, new WarArtifactListener(),
			properties);

		checkStartableBundles();

		return servletContext;
	}

	public BundleContext getBundleContext() {
		return _bundleContext;
	}

	public void modifiedService(
		ServiceReference<ServletContext> serviceReference,
		ServletContext servletContext) {

		// not needed
	}

	public void removedService(
		ServiceReference<ServletContext> serviceReference,
		ServletContext servletContext) {

		_artifactUrlTransformerRegistration.unregister();

		_artifactUrlTransformerRegistration = null;

		BundleContext bundleContext = getBundleContext();

		bundleContext.removeBundleListener(_bundleStartListener);
		bundleContext.removeBundleListener(_bundleStopListener);

		StrutsActionRegistryUtil.unregister(MODULE_MAPPING);

		_webPluginDeployer.close();
		_webExtenderServlet.destroy();

		_bundleStartListener = null;
		_bundleStopListener = null;
		_webExtenderServlet = null;
		_webPluginDeployer = null;
	}

	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		EventUtil.start(_bundleContext);

		StringBundler sb = new StringBundler(3);

		sb.append("(&(bean.id=");
		sb.append(ServletContext.class.getName());
		sb.append(")(original.bean=*))");

		Filter filter = bundleContext.createFilter(sb.toString());

		_servletContextTracker =
			new ServiceTracker<ServletContext, ServletContext>(
				bundleContext, filter, this);

		_servletContextTracker.open();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		_servletContextTracker.close();

		EventUtil.close();

		_bundleContext = null;
		_servletContextTracker = null;
	}

	protected void checkStartableBundles() {
		for (Bundle bundle : getBundleContext().getBundles()) {
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

	private ServiceRegistration<ArtifactUrlTransformer>
		_artifactUrlTransformerRegistration;
	private BundleContext _bundleContext;
	private BundleStartListener _bundleStartListener;
	private BundleStopListener _bundleStopListener;
	private WebExtenderServlet _webExtenderServlet;
	private ServiceTracker<ServletContext, ServletContext>
		_servletContextTracker;
	private WebBundleDeployer _webPluginDeployer;

}
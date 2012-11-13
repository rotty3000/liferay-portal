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

package com.liferay.web.extender.internal.http;

import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.web.extender.internal.servlet.BundleServletContext;
import com.liferay.web.extender.internal.servlet.WebExtenderServlet;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;

/**
 * @author Raymond Augé
 */
public class HttpServiceFactory
	implements ModuleFrameworkConstants, ServiceFactory<HttpService> {

	public HttpServiceFactory(
		BundleContext bundleContext, WebExtenderServlet webExtenderServlet) {

		_bundleContext = bundleContext;
		_webExtenderServlet = webExtenderServlet;
	}

	public HttpService getService(
		Bundle bundle, ServiceRegistration<HttpService> serviceRegistration) {

		StringBundler sb = new StringBundler(13);

		sb.append("(&(");
		sb.append(BUNDLE_SYMBOLICNAME);
		sb.append("=");
		sb.append(bundle.getSymbolicName());
		sb.append(")(");
		sb.append(BUNDLE_VERSION);
		sb.append("=");
		sb.append(bundle.getVersion().toString());
		sb.append(")(");
		sb.append(BUNDLE_ID);
		sb.append("=");
		sb.append(bundle.getBundleId());
		sb.append(")(");
		sb.append(WEB_CONTEXTPATH);
		sb.append("=*))");

		try {
			Filter filter = _bundleContext.createFilter(sb.toString());

			Collection<ServiceReference<BundleServletContext>>
				serviceReferences = _bundleContext.getServiceReferences(
					BundleServletContext.class, filter.toString());

			Iterator<ServiceReference<BundleServletContext>> iterator =
				serviceReferences.iterator();

			if (iterator.hasNext()) {
				ServiceReference<BundleServletContext> servletContextReference =
					iterator.next();

				BundleServletContext bundleServletContext =
					_bundleContext.getService(servletContextReference);

				return new HttpServiceWrapper(bundleServletContext);
			}

			String bundleContextName =
				BundleServletContext.getServletContextName(bundle, true);

			ServletContext servletContext = ServletContextPool.get(
				bundleContextName);

			if (servletContext == null) {
				BundleServletContext bundleServletContext =
					new BundleServletContext(
						bundle, bundleContextName, _webExtenderServlet);

				bundleServletContext.setServletContextName(bundleContextName);

				ServletContextPool.put(bundleContextName, bundleServletContext);

				servletContext = bundleServletContext;
			}

			if (!(servletContext instanceof BundleServletContext)) {
				return null;
			}

			return new NonWABHttpServiceWrapper(
				(BundleServletContext)servletContext);
		}
		catch (ClassCastException cce) {
			cce.printStackTrace();

			return null;
		}
		catch (InvalidSyntaxException ise) {
			throw new IllegalStateException(ise);
		}
	}

	public void ungetService(
		Bundle bundle, ServiceRegistration<HttpService> serviceRegistration,
		HttpService httpService) {

		// Nothing to do here
	}

	private BundleContext _bundleContext;
	private WebExtenderServlet _webExtenderServlet;

}
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
import com.liferay.web.extender.internal.Activator;
import com.liferay.web.extender.internal.servlet.BundleServletContext;
import com.liferay.web.extender.internal.servlet.WebExtenderServlet;

import java.util.Collection;
import java.util.Dictionary;
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
public class HttpServiceFactory implements ServiceFactory<HttpService> {

	public HttpServiceFactory(WebExtenderServlet webExtenderServlet) {
		_webExtenderServlet = webExtenderServlet;
	}

	public HttpService getService(
		Bundle bundle, ServiceRegistration<HttpService> serviceRegistration) {

		ServiceReference<ServletContext> servletContextReference = null;

		try {
			Dictionary<String,String> headers = bundle.getHeaders();

			String webContextPath = headers.get("Web-ContextPath");

			StringBundler sb = new StringBundler(7);

			sb.append("(&(osgi.web.symbolicname=");
			sb.append(bundle.getSymbolicName());
			sb.append(")(osgi.web.version=");
			sb.append(bundle.getVersion().toString());
			sb.append(")(osgi.web.contextpath=");
			sb.append(webContextPath);
			sb.append("))");

			BundleContext bundleContext = Activator.getBundleContext();

			Filter filter = bundleContext.createFilter(sb.toString());

			Collection<ServiceReference<ServletContext>> serviceReferences =
				bundleContext.getServiceReferences(
					ServletContext.class, filter.toString());

			Iterator<ServiceReference<ServletContext>> iterator =
				serviceReferences.iterator();

			if (iterator.hasNext()) {
				servletContextReference = iterator.next();

				ServletContext servletContext = bundleContext.getService(
					servletContextReference);

				return new HttpServiceWrapper(
					(BundleServletContext)servletContext);
			}

			String bundleContextName = getBundleContextName(bundle);

			ServletContext servletContext = ServletContextPool.get(
				bundleContextName);

			if (servletContext == null) {
				BundleServletContext bundleServletContext =
					new BundleServletContext(bundle, _webExtenderServlet);

				bundleServletContext.setServletContextName(bundleContextName);

				ServletContextPool.put(bundleContextName, bundleServletContext);

				servletContext = bundleServletContext;
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

	protected String getBundleContextName(Bundle bundle) {
		String symbolicName = bundle.getSymbolicName();

		return symbolicName.replaceAll("\\W", "");
	}

	private WebExtenderServlet _webExtenderServlet;

}
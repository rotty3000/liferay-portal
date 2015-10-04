/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.layoutsadmin.util;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Map;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Eduardo Garcia
 */
@ProviderType
public class SitemapURLProviderRegistryUtil {

	public static SitemapURLProvider getSitemapURLProvider(String className) {
		return _instance._getSitemapURLProvider(className);
	}

	public static List<SitemapURLProvider> getSitemapURLProviders() {
		return _instance._getSitemapURLProviders();
	}

	private SitemapURLProviderRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			SitemapURLProvider.class,
			new SitemapURLProviderServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private SitemapURLProvider _getSitemapURLProvider(String className) {
		return _sitemapURLProviders.get(className);
	}

	private List<SitemapURLProvider> _getSitemapURLProviders() {
		Collection<SitemapURLProvider> values = _sitemapURLProviders.values();

		return ListUtil.fromCollection(values);
	}

	private static final SitemapURLProviderRegistryUtil _instance =
		new SitemapURLProviderRegistryUtil();

	private final
		ServiceTracker<SitemapURLProvider, SitemapURLProvider> _serviceTracker;
	private final Map<String, SitemapURLProvider>
		_sitemapURLProviders = new ConcurrentHashMap<>();

	private class SitemapURLProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<SitemapURLProvider, SitemapURLProvider> {

		@Override
		public SitemapURLProvider addingService(
			ServiceReference<SitemapURLProvider> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			SitemapURLProvider sitemapURLProvider = registry.getService(
				serviceReference);

			_sitemapURLProviders.put(
				sitemapURLProvider.getClassName(), sitemapURLProvider);

			return sitemapURLProvider;
		}

		@Override
		public void modifiedService(
			ServiceReference<SitemapURLProvider> serviceReference,
			SitemapURLProvider sitemapURLProvider) {
		}

		@Override
		public void removedService(
			ServiceReference<SitemapURLProvider> serviceReference,
			SitemapURLProvider sitemapURLProvider) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			_sitemapURLProviders.remove(sitemapURLProvider.getClassName());
		}

	}

}
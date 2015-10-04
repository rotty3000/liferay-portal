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

package com.liferay.portlet.exportimport.xstream;

import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Map;

/**
 * @author Mate Thurzo
 */
public class XStreamAliasRegistryUtil {

	public static Map<Class<?>, String> getAliases() {
		return _instance._getAliases();
	}

	public static ClassLoader getAliasesClassLoader(
		ClassLoader masterClassLoader) {

		Set<ClassLoader> classLoaders = new HashSet<>();

		Map<Class<?>, String> aliases = _instance._getAliases();

		for (Class<?> clazz : aliases.keySet()) {
			classLoaders.add(clazz.getClassLoader());
		}

		return AggregateClassLoader.getAggregateClassLoader(
			masterClassLoader,
			classLoaders.toArray(new ClassLoader[classLoaders.size()]));
	}

	private XStreamAliasRegistryUtil() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(
			XStreamAlias.class, new XStreamAliasServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	private Map<Class<?>, String> _getAliases() {
		return _xstreamAliases;
	}

	private static final XStreamAliasRegistryUtil _instance =
		new XStreamAliasRegistryUtil();

	private final ServiceTracker<XStreamAlias, XStreamAlias> _serviceTracker;
	private final Map<Class<?>, String> _xstreamAliases =
		new ConcurrentHashMap<>();

	private class XStreamAliasServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<XStreamAlias, XStreamAlias> {

		@Override
		public XStreamAlias addingService(
			ServiceReference<XStreamAlias> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			XStreamAlias xStreamAlias = registry.getService(serviceReference);

			_xstreamAliases.put(
				xStreamAlias.getClazz(), xStreamAlias.getName());

			return xStreamAlias;
		}

		@Override
		public void modifiedService(
			ServiceReference<XStreamAlias> serviceReference,
			XStreamAlias xStreamAlias) {
		}

		@Override
		public void removedService(
			ServiceReference<XStreamAlias> serviceReference,
			XStreamAlias xStreamAlias) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			_xstreamAliases.remove(xStreamAlias.getClazz());
		}

	}

}
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

package com.liferay.portal.kernel.search.facet.util;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.kernel.util.ClassLoaderPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Raymond Augé
 */
public class FacetFactoryUtil {

	public static Facet create(
			SearchContext searchContext, FacetConfiguration facetConfiguration)
		throws Exception {

		String className = facetConfiguration.getClassName();
		String pluginContext = facetConfiguration.getPluginContext();
		String cacheKey = className.concat(StringPool.AT).concat(pluginContext);
		
		Constructor<?> constructor = _constructorCache.get(cacheKey);

		if (constructor == null) {
			if (Validator.isNotNull(pluginContext)) {
				PortalRuntimePermission.checkGetClassLoader(pluginContext);
				
				constructor = Class.forName(className, true, 
						ClassLoaderPool.getClassLoader(pluginContext))
					.getConstructor(SearchContext.class);
			} else {
				constructor = Class.forName(className).getConstructor(
					SearchContext.class);
			}
			
			_constructorCache.put(cacheKey, constructor);
		}

		Facet facet = (Facet)constructor.newInstance(searchContext);

		facet.setFacetConfiguration(facetConfiguration);

		return facet;
	}

	private static Map<String, Constructor<?>> _constructorCache =
		new ConcurrentHashMap<String, Constructor<?>>();

}
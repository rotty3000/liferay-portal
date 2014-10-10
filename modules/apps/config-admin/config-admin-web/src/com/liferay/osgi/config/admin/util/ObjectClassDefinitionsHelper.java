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

package com.liferay.osgi.config.admin.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 * TODO cache to hold PID --> Bundle Map
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ObjectClassDefinitionsHelper {

	public static final int ALL = -1;

	public static final int FACTORY_PIDS = 1;

	public static final int PIDS = 0;

	public ObjectClassDefinitionsHelper(
		BundleContext bundleContext, String languageId) {

		_bundleContext = bundleContext;

		_metaTypeServiceReference = _bundleContext.getServiceReference(
	MetaTypeService.class.getName());

		_objectClassDefinitions = objectClassDefinitions(ALL, languageId);
	}

	public MetaTypeService getMetaTypeService() {
		return (MetaTypeService) _bundleContext.getService(
			_metaTypeServiceReference);
	}

	public ObjectClassDefinition getObjectClassDefinition(
		Bundle bundle, String pid, String locale) {

			MetaTypeService metaTypeService = getMetaTypeService();

			MetaTypeInformation metaTypeInformation =
							metaTypeService.getMetaTypeInformation(bundle);

			if (metaTypeInformation != null) {
				return metaTypeInformation.getObjectClassDefinition(
					pid, locale);
			}

		return null;
	}

	public ObjectClassDefinition getObjectClassDefinition(String pid) {
		return _objectClassDefinitions.get(pid);
	}

	protected Map<String, ObjectClassDefinition> objectClassDefinitions(
		int type, String locale) {

		Map<String, ObjectClassDefinition> ocdMap =
						new HashMap<String, ObjectClassDefinition>();

		Bundle[] bundles = _bundleContext.getBundles();

		MetaTypeService metaTypeService = getMetaTypeService();

		for (Bundle bundle : bundles) {
			MetaTypeInformation metaTypeInformation =
							metaTypeService.getMetaTypeInformation(bundle);

			if (metaTypeInformation == null) {
				continue;
			}

			String[] pids = new String[0];

			switch(type) {
				case PIDS: {

					pids = metaTypeInformation.getPids();

					_collectObjectDefintions(
						metaTypeInformation, ocdMap, pids, locale);

					break;
				}

				case FACTORY_PIDS: {

					pids = metaTypeInformation.getFactoryPids();

					_collectObjectDefintions(
						metaTypeInformation, ocdMap, pids, locale);

					break;
				}

				case ALL: {

					pids = metaTypeInformation.getPids();

					_collectObjectDefintions(
						metaTypeInformation, ocdMap, pids, locale);

					pids = metaTypeInformation.getFactoryPids();

					_collectObjectDefintions(
						metaTypeInformation, ocdMap, pids, locale);

					break;
				}
			}
		}

		return ocdMap;
	}

	private void _collectObjectDefintions(
		MetaTypeInformation metaTypeInformation,
		Map<String, ObjectClassDefinition> ocdMap,
		String[] pids, String locale) {

		for (String pid : pids) {
			ObjectClassDefinition ocd =
							metaTypeInformation.getObjectClassDefinition(
								pid, locale);

			if (ocd!= null) {
				ocdMap.put(pid, ocd);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
					ObjectClassDefinitionsHelper.class);

	private final BundleContext _bundleContext;
	private final ServiceReference _metaTypeServiceReference;
	private final Map<String, ObjectClassDefinition> _objectClassDefinitions;

}
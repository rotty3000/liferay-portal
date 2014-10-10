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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 */
public class ObjectClassDefinitonsIterator {

	public ObjectClassDefinitonsIterator(
		BundleContext bundleCcontext, MetaTypeService metaTypeService,
		String languageId) {

		_bundleCcontext = bundleCcontext;
		_metaTypeService = metaTypeService;
		_languageId = languageId;

		_objectClassDefinitions = _getObjectDefinitions();
	}

	public ObjectClassDefinition getObjectClassDefinition(String servicePID) {
		return _objectClassDefinitions.get(servicePID);
	}

	public List<ObjectClassDefinition> getResults(int start, int end)
		throws PortalException {

		List<ObjectClassDefinition> list = new ArrayList<>(
			_objectClassDefinitions.values());

		return ListUtil.subList(list, start, end);
	}

	public int getTotal() throws PortalException {
		return _objectClassDefinitions.size();
	}

	protected Map<String, ObjectClassDefinition> _getObjectDefinitions() {
		Bundle[] bundles = _bundleCcontext.getBundles();

		Map<String, ObjectClassDefinition> ocds =
			new TreeMap<String, ObjectClassDefinition>();

		for (Bundle bundle : bundles) {
			MetaTypeInformation metaTypeInformation =
				_metaTypeService.getMetaTypeInformation(bundle);

			if (metaTypeInformation == null) {
				continue;
			}

			String[] pids = metaTypeInformation.getPids();

			_collectObjectClassDefinition(ocds, metaTypeInformation, pids);

			String[] factoryPids = metaTypeInformation.getFactoryPids();

			_collectObjectClassDefinition(
				ocds, metaTypeInformation, factoryPids);
		}

		return ocds;
	}

	protected void _collectObjectClassDefinition(
		Map<String, ObjectClassDefinition> ocds,
		MetaTypeInformation metaTypeInformation, String... pids) {

		for (String pid : pids) {
			ObjectClassDefinition objectClassDefinition =
				metaTypeInformation.getObjectClassDefinition(pid, _languageId);

			if (objectClassDefinition != null) {
				ocds.put(pid, objectClassDefinition);
			}
		}
	}

	private BundleContext _bundleCcontext;
	private String _languageId;
	private MetaTypeService _metaTypeService;
	private Map<String, ObjectClassDefinition> _objectClassDefinitions;

}
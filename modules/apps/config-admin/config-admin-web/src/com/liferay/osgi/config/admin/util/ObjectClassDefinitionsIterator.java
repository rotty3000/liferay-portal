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
import com.liferay.portal.kernel.util.StringPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 */
public class ObjectClassDefinitionsIterator {

	public ObjectClassDefinitionsIterator(
		BundleContext bundleCcontext, MetaTypeService metaTypeService,
		String languageId) {

		_bundleContext = bundleCcontext;
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

	public String targetPID(String servicePID){

		return _targetPIDMap.get(servicePID);
	}

	public int getTotal() throws PortalException {
		return _objectClassDefinitions.size();
	}

	protected void _addToTargetPIDMap(Bundle bundle, String pid){

		StringBuilder targetPID = new StringBuilder(7);

		targetPID.append(pid);

		String bundleSymbolicName = bundle.getSymbolicName();

		targetPID.append(StringPool.PIPE);

		targetPID.append(bundleSymbolicName);

		Version bundleVersion = bundle.getVersion();

		targetPID.append(StringPool.PIPE);

		targetPID.append(bundleVersion.toString());

		String bundleLocation = bundle.getLocation();

		targetPID.append(StringPool.PIPE);

		targetPID.append(bundleLocation);

		_targetPIDMap.put(pid, targetPID.toString());
	}

	protected void _collectObjectClassDefinition(Bundle bundle,
		Map<String, ObjectClassDefinition> ocds,
		MetaTypeInformation metaTypeInformation, String... pids) {

		for (String pid : pids) {
			ObjectClassDefinition objectClassDefinition =
				metaTypeInformation.getObjectClassDefinition(pid, _languageId);

			if (objectClassDefinition != null) {
				_addToTargetPIDMap(bundle, pid);
				ocds.put(pid, objectClassDefinition);
			}
		}
	}

	protected Map<String, ObjectClassDefinition> _getObjectDefinitions() {
		Bundle[] bundles = _bundleContext.getBundles();

		Map<String, ObjectClassDefinition> ocds =
			new TreeMap<String, ObjectClassDefinition>();

		for (Bundle bundle : bundles) {
			MetaTypeInformation metaTypeInformation =
				_metaTypeService.getMetaTypeInformation(bundle);

			if (metaTypeInformation == null) {
				continue;
			}

			String[] pids = metaTypeInformation.getPids();

			_collectObjectClassDefinition(
				bundle,ocds, metaTypeInformation, pids);

			String[] factoryPids = metaTypeInformation.getFactoryPids();

			_collectObjectClassDefinition(
				bundle, ocds, metaTypeInformation, factoryPids);

		}

		return ocds;
	}

	private BundleContext _bundleContext;
	private String _languageId;
	private MetaTypeService _metaTypeService;
	private Map<String, ObjectClassDefinition> _objectClassDefinitions;
	private Map<String,String> _targetPIDMap =
					new ConcurrentHashMap<String, String>();

}
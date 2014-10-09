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
		BundleContext context, MetaTypeService metaTypeService) {

		_context = context;
		_metaTypeService = metaTypeService;
	}

	public List<ObjectClassDefinition> getResults(int start, int end)
		throws PortalException {

		return ListUtil.subList(objectDefinitions(), start, end);
	}

	public int getTotal() throws PortalException {
		return objectDefinitions().size();
	}

	public List<ObjectClassDefinition> objectDefinitions()
		throws PortalException {

		Bundle[] bundles = _context.getBundles();

		List<ObjectClassDefinition> ocdContainer =
			new ArrayList<ObjectClassDefinition>();

		for (Bundle bundle : bundles) {
			MetaTypeInformation mInfo = _metaTypeService.getMetaTypeInformation(
				bundle);

			if (mInfo != null) {
				String[] pids = mInfo.getPids();

				MetaTypeInfoUtil.fillOCD(mInfo, ocdContainer, pids);

				String[] factoryPids = mInfo.getFactoryPids();

				MetaTypeInfoUtil.fillOCD(mInfo, ocdContainer, factoryPids);
			}
		}

		return ocdContainer;
	}

	private BundleContext _context;
	private MetaTypeService _metaTypeService;

}
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

package com.liferay.vulcan.wiring.osgi.internal;

import com.liferay.vulcan.representor.ModelRepresentorMapper;

import org.osgi.framework.ServiceReference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class ModelRepresentorMapperTuple<T>
	implements Comparable<ModelRepresentorMapperTuple> {

	public ModelRepresentorMapperTuple(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference,
		ModelRepresentorMapper<T> modelRepresentorMapper) {

		_serviceReference = serviceReference;
		_modelRepresentorMapper = modelRepresentorMapper;
	}

	@Override
	public int compareTo(
		ModelRepresentorMapperTuple modelRepresentorMapperTuple) {

		return _serviceReference.compareTo(
			modelRepresentorMapperTuple._serviceReference);
	}

	public ModelRepresentorMapper<T> getModelRepresentorMapper() {
		return _modelRepresentorMapper;
	}

	private final ModelRepresentorMapper<T> _modelRepresentorMapper;
	private final ServiceReference<ModelRepresentorMapper<T>> _serviceReference;

}
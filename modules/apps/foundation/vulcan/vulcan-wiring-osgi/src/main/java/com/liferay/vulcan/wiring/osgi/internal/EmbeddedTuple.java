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

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class EmbeddedTuple<T, S> {

	public EmbeddedTuple(
		String key, Class<S> modelClass,
		Function<T, Optional<S>> modelFunction) {

		_key = key;
		_modelClass = modelClass;
		_modelFunction = modelFunction;
	}

	public String getKey() {
		return _key;
	}

	public Class<S> getModelClass() {
		return _modelClass;
	}

	public Function<T, Optional<S>> getModelFunction() {
		return _modelFunction;
	}

	private final String _key;
	private final Class<S> _modelClass;
	private final Function<T, Optional<S>> _modelFunction;

}
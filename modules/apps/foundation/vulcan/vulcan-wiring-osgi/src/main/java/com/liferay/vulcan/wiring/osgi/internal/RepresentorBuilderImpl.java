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

import com.liferay.vulcan.representor.builder.RepresentorBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class RepresentorBuilderImpl<T> implements RepresentorBuilder<T> {

	public RepresentorBuilderImpl(
		Class<T> modelClass,
		Map<String, Function<?, String>> identifierFunctions,
		Map<String, Function<?, Object>> fieldFunctions,
		List<EmbeddedTuple<?, ?>> embeddedTuples, List<String> types) {

		_modelClass = modelClass;
		_identifierFunctions = identifierFunctions;
		_fieldFunctions = fieldFunctions;
		_embeddedTuples = embeddedTuples;
		_types = types;
	}

	@Override
	public FirstStep<T> getFirstStep(Function<T, String> identifierFunction) {
		_identifierFunctions.put(_modelClass.getName(), identifierFunction);

		return new FirstStep<T>() {

			@Override
			public <S> FirstStep<T> addEmbedded(
				String key, Class<S> modelClass,
				Function<T, Optional<S>> modelFunction) {

				_embeddedTuples.add(
					new EmbeddedTuple<>(key, modelClass, modelFunction));

				return this;
			}

			@Override
			public FirstStep<T> addField(
				String key, Function<T, Object> fieldFunction) {

				_fieldFunctions.put(key, fieldFunction);

				return this;
			}

			@Override
			public FirstStep<T> addType(String type) {
				_types.add(type);

				return this;
			}

		};
	}

	private final List<EmbeddedTuple<?, ?>> _embeddedTuples;
	private final Map<String, Function<?, Object>> _fieldFunctions;
	private final Map<String, Function<?, String>> _identifierFunctions;
	private final Class<T> _modelClass;
	private final List<String> _types;

}
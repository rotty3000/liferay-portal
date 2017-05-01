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

package com.liferay.vulcan.wiring.osgi;

import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.wiring.osgi.internal.EmbeddedTuple;
import com.liferay.vulcan.wiring.osgi.internal.GenericUtil;
import com.liferay.vulcan.wiring.osgi.internal.ModelRepresentorMapperTuple;
import com.liferay.vulcan.wiring.osgi.internal.RepresentorBuilderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = RepresentorManager.class)
public class RepresentorManager {

	public RepresentorManager() {
		Bundle bundle = FrameworkUtil.getBundle(RepresentorManager.class);

		_bundleContext = bundle.getBundleContext();
	}

	public <T, V> List<EmbeddedTuple<T, V>> getEmbeddedTuples(
		Class<T> modelClass) {

		return (List)_embeddedTuples.get(modelClass.getName());
	}

	public <T> Map<String, Function<T, Object>> getFieldFunctions(
		Class<T> modelClass) {

		return (Map)_fieldFunctions.get(modelClass.getName());
	}

	public <T> String getIdentifier(Class<T> modelClass, T model) {
		Function<T, String> identifierFunction =
			(Function<T, String>)_identifierFunctions.get(modelClass.getName());

		return identifierFunction.apply(model);
	}

	public <T> Optional<ModelRepresentorMapper<T>>
		getModelRepresentorMapperOptional(Class<T> modelClass) {

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMappers.get(modelClass.getName());

		Optional<TreeSet<ModelRepresentorMapperTuple<?>>>
			modelRepresentorMapperTuplesOptional = Optional.ofNullable(
				modelRepresentorMapperTuples);

		Optional<ModelRepresentorMapperTuple<?>>
			firstModelRepresentorMapperTuple =
				modelRepresentorMapperTuplesOptional.map(TreeSet::first);

		return firstModelRepresentorMapperTuple.map(
			modelRepresentorMapperTuple ->
				(ModelRepresentorMapper<T>)modelRepresentorMapperTuple.
					getModelRepresentorMapper());
	}

	public <T> List<String> getTypes(Class<T> modelClass) {
		return _types.get(modelClass.getName());
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected <T> void setServiceReference(
			ServiceReference<ModelRepresentorMapper<T>> serviceReference)
		throws IllegalArgumentException {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Class<T> modelClass = _getModelClass(modelRepresentorMapper);

		_addModelRepresentorMapper(
			serviceReference, modelRepresentorMapper, modelClass);

		_buildRepresentor(modelRepresentorMapper, modelClass);
	}

	protected <T> void unsetServiceReference(
			ServiceReference<ModelRepresentorMapper<T>> serviceReference)
		throws IllegalArgumentException {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Class<T> modelClass = _getModelClass(modelRepresentorMapper);

		_removeModelRepresentorMapper(modelRepresentorMapper, modelClass);

		_removeRepresentor(modelClass);

		Optional<ModelRepresentorMapper<T>> optional =
			getModelRepresentorMapperOptional(modelClass);

		optional.ifPresent(
			firstModelRepresentorMapper ->
				_buildRepresentor(firstModelRepresentorMapper, modelClass));
	}

	private <T> void _addModelRepresentorMapper(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference,
		ModelRepresentorMapper<T> modelRepresentorMapper, Class<T> modelClass) {

		_modelRepresentorMappers.computeIfAbsent(
			modelClass.getName(), name -> new TreeSet<>());

		ModelRepresentorMapperTuple<T> modelRepresentorMapperTuple =
			new ModelRepresentorMapperTuple<>(
				serviceReference, modelRepresentorMapper);

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMappers.get(modelClass.getName());

		modelRepresentorMapperTuples.add(modelRepresentorMapperTuple);
	}

	private <T> void _buildRepresentor(
		ModelRepresentorMapper<T> modelRepresentorMapper, Class<T> modelClass) {

		Map<String, Function<?, Object>> fieldFunctions = new HashMap<>();

		_fieldFunctions.put(modelClass.getName(), fieldFunctions);

		List<EmbeddedTuple<?, ?>> embeddedTuples = new ArrayList<>();

		_embeddedTuples.put(modelClass.getName(), embeddedTuples);

		List<String> types = new ArrayList<>();

		_types.put(modelClass.getName(), types);

		modelRepresentorMapper.buildRepresentor(
			new RepresentorBuilderImpl<>(
				modelClass, _identifierFunctions, fieldFunctions,
				embeddedTuples, types));
	}

	private <T> Class<T> _getModelClass(
			ModelRepresentorMapper<T> modelRepresentorMapper)
		throws IllegalArgumentException {

		Optional<Class<T>> optional = GenericUtil.getGenericClassOptional(
			modelRepresentorMapper, ModelRepresentorMapper.class);

		if (!optional.isPresent()) {
			Class<?> clazz = modelRepresentorMapper.getClass();

			throw new IllegalArgumentException(
				"Class " + clazz.getName() + " must have a generic type");
		}

		return optional.get();
	}

	private <T> void _removeModelRepresentorMapper(
		ModelRepresentorMapper modelRepresentorMapper, Class<T> modelClass) {

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMappers.get(modelClass.getName());

		modelRepresentorMapperTuples.removeIf(
			modelRepresentorMapperTuple -> {
				if (modelRepresentorMapperTuple.getModelRepresentorMapper() ==
						modelRepresentorMapper) {

					return true;
				}

				return false;
			});
	}

	private <T> void _removeRepresentor(Class<T> modelClass) {
		_embeddedTuples.remove(modelClass.getName());
		_fieldFunctions.remove(modelClass.getName());
		_identifierFunctions.remove(modelClass.getName());
		_types.remove(modelClass.getName());
	}

	private final BundleContext _bundleContext;
	private final Map<String, List<EmbeddedTuple<?, ?>>> _embeddedTuples =
		new ConcurrentHashMap<>();
	private final Map<String, Map<String, Function<?, Object>>>
		_fieldFunctions = new ConcurrentHashMap<>();
	private final Map<String, Function<?, String>> _identifierFunctions =
		new ConcurrentHashMap<>();
	private final Map<String, TreeSet<ModelRepresentorMapperTuple<?>>>
		_modelRepresentorMappers = new ConcurrentHashMap<>();
	private final Map<String, List<String>> _types = new ConcurrentHashMap<>();

}
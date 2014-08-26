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

package com.liferay.portal.search.elasticsearch.facet;

import com.liferay.portal.kernel.search.facet.Facet;

import java.util.HashMap;
import java.util.Map;

import com.liferay.portal.kernel.util.MapUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, property = {
		"class.name=com.liferay.portal.search.elasticsearch.facet.CompositeFacetProcessor"
	}
)
public class CompositeFacetProcessor
	implements FacetProcessor<SearchRequestBuilder> {

	public void processFacet(
		SearchRequestBuilder searchRequestBuilder, Facet facet) {

		Class<?> clazz = facet.getClass();

		FacetProcessor facetProcessor = _facetProcessors.get(clazz.getName());

		if (facetProcessor == null) {
			facetProcessor = _defaultFacetProcessor;
		}

		facetProcessor.processFacet(searchRequestBuilder, facet);
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		target = "(&(!(class.name=*))(default.facet.processor=*))"
	)
	public void setDefaultFacetProcessor(FacetProcessor defaultFacetProcessor) {
		_defaultFacetProcessor = defaultFacetProcessor;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(&(!(class.name=" +
			"com.liferay.portal.search.elasticsearch.facet." +
			"CompositeFacetProcessor))" +
			"(!(default.facet.processor=*)))"
	)
	public void setFacetProcessor(
		FacetProcessor facetProcessor, Map<String, Object> properties) {

		String className = MapUtil.getString(properties, "class.name");

		_facetProcessors.put(className, facetProcessor);
	}

	public void unsetFacetProcessor(
		FacetProcessor facetProcessor, Map<String, Object> properties) {

		String className = MapUtil.getString(properties, "class.name");

		_facetProcessors.remove(className);
	}

	private static FacetProcessor _defaultFacetProcessor;
	private static Map<String, FacetProcessor> _facetProcessors =
		new HashMap<String, FacetProcessor>();

}
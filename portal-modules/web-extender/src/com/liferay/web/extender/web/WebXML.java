/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.web.extender.web;

import com.liferay.web.extender.definition.FilterDefinition;
import com.liferay.web.extender.definition.ListenerDefinition;
import com.liferay.web.extender.definition.ServletDefinition;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class WebXML {

	public WebXML() {
	}

	/**
	 * @return the contextParams
	 */
	public Hashtable<String, String> getContextParams() {
		return _contextParams;
	}

	/**
	 * @return the filters
	 */
	public Map<String, FilterDefinition> getFilters() {
		return _filters;
	}

	public Map<String, String> getMimeTypes() {
		return _mimeTypes;
	}

	/**
	 * @return the listeners
	 */
	public Map<String, ListenerDefinition> getListeners() {
		return _listeners;
	}

	/**
	 * @return the servlets
	 */
	public Map<String, ServletDefinition> getServlets() {
		return _servlets;
	}

	/**
	 * @param key the contextParam's key
	 * @param value the contextParam's value
	 */
	public void setContextParam(String key, String value) {
		_contextParams.put(key, value);
	}

	/**
	 * @param contextParams the contextParams to set
	 */
	public void setContextParams(Hashtable<String, String> contextParams) {
		_contextParams = contextParams;
	}

	/**
	 * @param mapping the filter's mapping
	 * @param filter the filter
	 */
	public void setFilter(String filterName, FilterDefinition filter) {
		_filters.put(filterName, filter);
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(Map<String, FilterDefinition> filters) {
		_filters = filters;
	}

	public void setMimeType(String extension, String mimeType) {
		_mimeTypes.put(extension, mimeType);
	}

	public void setMimeTypes(Map<String, String> mimeTypes) {
		_mimeTypes = mimeTypes;
	}

	/**
	 * @param listeners the listeners to set
	 */
	public void setListener(String className, ListenerDefinition listener) {
		_listeners.put(className, listener);
	}

	/**
	 * @param listeners the listeners to set
	 */
	public void setListeners(Map<String, ListenerDefinition> listeners) {
		_listeners = listeners;
	}

	/**
	 * @param mapping the servlet's mapping
	 * @param servlet the servlet
	 */
	public void setServlet(String mapping, ServletDefinition servlet) {
		_servlets.put(mapping, servlet);
	}

	/**
	 * @param servlets the servlets to set
	 */
	public void setServlets(Map<String, ServletDefinition> servlets) {
		_servlets = servlets;
	}

	private Hashtable<String, String> _contextParams =
		new Hashtable<String, String>();
	private Map<String, FilterDefinition> _filters =
		new HashMap<String, FilterDefinition>();
	private Map<String, String> _mimeTypes =
		new HashMap<String, String>();
	private Map<String, ListenerDefinition> _listeners =
		new HashMap<String, ListenerDefinition>();
	private Map<String, ServletDefinition> _servlets =
		new HashMap<String, ServletDefinition>();

}
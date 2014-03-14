/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.osgi.http.internal.servlet;

import aQute.lib.converter.Converter;

import com.liferay.osgi.util.ComponentProps;
import com.liferay.osgi.util.HttpProperties;
import com.liferay.osgi.util.ServiceProps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;

/**
 * @author Raymond Augé
 */
public class ServletProperties {

	public static ServletProperties cnv(
			Map<String, Object> properties, Servlet servlet)
		throws Exception {

		return new ServletProperties(
			Converter.cnv(Props.class, properties), properties, servlet);
	}

	@SuppressWarnings("unchecked")
	private ServletProperties(
		Props props, Map<String, Object> rawProperties, Servlet servlet) {

		_props = props;
		_rawProperties = rawProperties;

		String contextName = _props.osgi_http_whiteboard_context_select();

		if (contextName == null) {
			contextName = "";
		}

		_contextName = contextName;

		Map<String, String> initParameters = new HashMap<String, String>();

		Iterator<Entry<String, Object>> iterator =
			_rawProperties.entrySet().iterator();

		while(iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();

			initParameters.put(
				entry.getKey(), String.valueOf(entry.getValue()));
		}

		_initParameters = Collections.unmodifiableMap(initParameters);

		String servletName = _props.osgi_http_whiteboard_servlet_name();

		if (servletName == null) {
			servletName = servlet.getClass().getName();
		}

		_servletName = servletName;
	}

	public String getContextName() {
		return _contextName;
	}

	public Map<String, String> getInitParameters() {
		return _initParameters;
	}

	public Props getProps() {
		return _props;
	}

	public Map<String, Object> getRawProperties() {
		return _rawProperties;
	}

	public String getServletName() {
		return _servletName;
	}

	private final String _contextName;
	private final Map<String, String> _initParameters;
	private final Props _props;
	private final Map<String, Object> _rawProperties;
	private final String _servletName;

	public interface Props
		extends ComponentProps, HttpProperties, ServiceProps {

		public boolean osgi_http_whiteboard_servlet_asyncSupported();

		public List<String> osgi_http_whiteboard_servlet_errorPage();

		public String osgi_http_whiteboard_servlet_name();

		public List<String> osgi_http_whiteboard_servlet_pattern();

		public String osgi_http_whiteboard_resource_prefix();

	}

}
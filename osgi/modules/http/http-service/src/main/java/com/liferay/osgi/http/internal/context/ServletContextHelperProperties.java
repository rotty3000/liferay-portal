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

package com.liferay.osgi.http.internal.context;

import aQute.lib.converter.Converter;

import com.liferay.osgi.util.ComponentProps;
import com.liferay.osgi.util.ServiceProps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class ServletContextHelperProperties {

	public static ServletContextHelperProperties cnv(
			Map<String, Object> properties)
		throws Exception {

		return new ServletContextHelperProperties(
			Converter.cnv(Props.class, properties), properties);
	}

	@SuppressWarnings("unchecked")
	private ServletContextHelperProperties(
		Props props, Map<String, Object> rawProperties) {

		_props = props;
		_rawProperties = Collections.unmodifiableMap(rawProperties);

		List<String> contextNames = _props.osgi_http_whiteboard_context_name();

		String contextName = "";

		if ((contextNames != null) && !contextNames.isEmpty()) {
			contextName = contextNames.get(0);
		}

		_contextName = contextName;

		String contextPath = _props.osgi_http_whiteboard_context_path();

		if (contextPath == null) {
			contextPath = "";
		}

		_contextPath = contextPath;
	}

	public String getContextPath() {
		return _contextPath;
	}

	public String getContextName() {
		return _contextName;
	}

	public Props getProps() {
		return _props;
	}

	public Map<String, Object> getRawProperties() {
		return _rawProperties;
	}

	private String getSafeName(String name) {
		if (name == null) {
			return null;
		}

		StringBuilder sb = null;

		int index = 0;

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			switch (c) {
				case ' ':

				case '-':

				case '.':
					if (sb == null) {
						sb = new StringBuilder(name.length() - 1);

						sb.append(name, index, i);
					}

					break;

				default:
					if (sb != null) {
						sb.append(c);
					}
			}
		}

		if (sb == null) {
			return name;
		}

		return sb.toString();
	}

	private final String _contextName;
	private final String _contextPath;
	private final Props _props;
	private final Map<String, Object> _rawProperties;

	public interface Props extends ComponentProps, ServiceProps {

		public List<String> osgi_http_whiteboard_context_name();

		public String osgi_http_whiteboard_context_path();

		public boolean osgi_http_whiteboard_context_shared();

	}

}
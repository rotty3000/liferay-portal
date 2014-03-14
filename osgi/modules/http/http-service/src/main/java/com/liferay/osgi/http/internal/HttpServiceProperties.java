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

package com.liferay.osgi.http.internal;

import aQute.lib.converter.Converter;

import com.liferay.osgi.util.ComponentProps;
import com.liferay.osgi.util.ServiceProps;

import java.net.URI;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class HttpServiceProperties {

	public static HttpServiceProperties cnv(Map<String, Object> properties)
		throws Exception {

		return new HttpServiceProperties(
			Converter.cnv(Props.class, properties), properties);
	}

	@SuppressWarnings("unchecked")
	private HttpServiceProperties(
		Props props, Map<String, Object> rawProperties) {

		_props = props;
		_rawProperties = Collections.unmodifiableMap(rawProperties);

		URI create = URI.create(_props.osgi_http_endpoint().get(0));

		String path = create.getPath();

		if (path.equals("/")) {
			path = "";
		}

		_contextPath = path;
	}

	public String getContextPath() {
		return _contextPath;
	}

	public Props getProps() {
		return _props;
	}

	public Map<String, Object> getRawProperties() {
		return _rawProperties;
	}

	private String _contextPath;
	private final Props _props;
	private final Map<String, Object> _rawProperties;

	public interface Props extends ComponentProps, ServiceProps {

		public List<String> osgi_http_endpoint();

	}

}
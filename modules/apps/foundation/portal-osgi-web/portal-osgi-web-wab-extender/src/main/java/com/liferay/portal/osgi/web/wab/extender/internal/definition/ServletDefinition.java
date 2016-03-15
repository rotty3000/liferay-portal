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

package com.liferay.portal.osgi.web.wab.extender.internal.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class ServletDefinition {

	public void addURLPattern(String urlPattern) {
		_urlPatterns.add(urlPattern);
	}

	public String getClassName() {
		return _className;
	}

	public Map<String, String> getInitParameters() {
		return _initParameters;
	}

	public String getJspFile() {
		return _jspFile;
	}

	public String getName() {
		return _name;
	}

	public List<String> getURLPatterns() {
		return _urlPatterns;
	}

	public boolean isAsyncSupported() {
		return _asyncSupported;
	}

	public void setAsyncSupported(boolean asyncSupported) {
		_asyncSupported = asyncSupported;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setInitParameter(String name, String value) {
		_initParameters.put(name, value);
	}

	public void setInitParameters(Map<String, String> initParameters) {
		_initParameters = initParameters;
	}

	public void setJSPFile(String jspFile) {
		_jspFile = jspFile;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setURLPatterns(List<String> urlPatterns) {
		_urlPatterns = urlPatterns;
	}

	private boolean _asyncSupported;
	private String _className;
	private Map<String, String> _initParameters = new HashMap<>();
	private String _jspFile;
	private String _name;
	private List<String> _urlPatterns = new ArrayList<>();

}
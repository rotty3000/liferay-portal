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

package com.liferay.portal.osgi.web.servlet.context.helper;

import com.liferay.portal.kernel.util.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;

/**
 * @author Juan Gonzalez
 *
 */
public class FilterRegistrationImpl implements FilterRegistration.Dynamic {

	@Override
	public void addMappingForServletNames(
		EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
		String... servletNames) {

		_filterMapping.setDispatcher(dispatcherTypes);

		int i = 0;

		for (String servletName : servletNames) {
			if (isMatchAfter) {
				_filterMapping.addServletName(servletName);
			}
			else {
				_filterMapping.addServletName(i, servletName);
				i++;
			}
		}
	}

	@Override
	public void addMappingForUrlPatterns(
		EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter,
		String... urlPatterns) {

		_filterMapping.setDispatcher(dispatcherTypes);

		int i = 0;

		for (String servletName : urlPatterns) {
			if (isMatchAfter) {
				_filterMapping.addURLPattern(servletName);
			}
			else {
				_filterMapping.addURLPattern(i, servletName);
				i++;
			}
		}
	}

	@Override
	public String getClassName() {
		return _className;
	}

	public EnumSet<DispatcherType> getDispatchers() {
		return _filterMapping.getDispatcher();
	}

	@Override
	public String getInitParameter(String name) {
		return _initParameters.get(name);
	}

	@Override
	public Map<String, String> getInitParameters() {
		return _initParameters;
	}

	public Filter getInstance() {
		return _instance;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Collection<String> getServletNameMappings() {
		return new ArrayList<>(_filterMapping.getServletNames());
	}

	@Override
	public Collection<String> getUrlPatternMappings() {
		return new ArrayList<>(_filterMapping.getURLPatterns());
	}

	public boolean isAsyncSupported() {
		return _asyncSupported;
	}

	@Override
	public void setAsyncSupported(boolean isAsyncSupported) {
		_asyncSupported = isAsyncSupported;
	}

	public void setClassName(String className) {
		_className = className;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		boolean exists = _initParameters.containsKey(name);
		_initParameters.put(name, value);
		return exists;
	}

	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {
		_initParameters = initParameters;
		return new HashSet<>();
	}

	public void setInstance(Filter instance) {
		_instance = instance;
	}

	public void setName(String name) {
		_name = name;
	}

	private boolean _asyncSupported;
	private String _className = StringPool.BLANK;
	private final FilterMapping _filterMapping = new FilterMapping();
	private Map<String, String> _initParameters = new HashMap<>();
	private Filter _instance;
	private String _name = StringPool.BLANK;

	private static class FilterMapping {

		public void addServletName(int index, String servletName) {
			_servletNames.add(index, servletName);
		}

		public void addServletName(String servletName) {
			addServletName(_servletNames.size(), servletName);
		}

		public void addURLPattern(int index, String urlPattern) {
			_urlPatterns.add(index, urlPattern);
		}

		public void addURLPattern(String urlPattern) {
			addURLPattern(_urlPatterns.size(), urlPattern);
		}

		public EnumSet<DispatcherType> getDispatcher() {
			return _dispatcher;
		}

		public List<String> getServletNames() {
			return _servletNames;
		}

		public List<String> getURLPatterns() {
			return _urlPatterns;
		}

		public void setDispatcher(EnumSet<DispatcherType> dispatcher) {
			_dispatcher = dispatcher;
		}

		private EnumSet<DispatcherType> _dispatcher = EnumSet.noneOf(
			DispatcherType.class);
		private final List<String> _servletNames = new ArrayList<>();
		private final List<String> _urlPatterns = new ArrayList<>();

	}

}
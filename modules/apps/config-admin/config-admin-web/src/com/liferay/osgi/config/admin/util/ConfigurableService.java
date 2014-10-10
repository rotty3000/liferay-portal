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

package com.liferay.osgi.config.admin.util;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 *
 */
public class ConfigurableService {

	public ConfigurableService(
		boolean factory, String factoryPid, String name, String pid) {

		_factory = factory;
		_factoryPid = factoryPid;
		_name = name;
		_pid = pid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;
		ConfigurableService other = (ConfigurableService)obj;

		if (_pid == null) {
			if (other._pid != null)
				return false;
		}
		else if (!_pid.equals(other._pid))
			return false;
		return true;
	}

	public String getBundleLocation() {
		return _bundleLocation;
	}

	public String getFactoryPid() {
		return _factoryPid;
	}

	public String getName() {
		return _name;
	}

	public ObjectClassDefinition getObjectClassDefinition() {
		return _objectClassDefinition;
	}

	public String getPid() {
		return _pid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_pid == null) ? 0 : _pid.hashCode());
		return result;
	}

	public boolean isFactory() {
		return _factory;
	}

	public void setBundleLocation(String bundleLocation) {
		_bundleLocation = bundleLocation;
	}

	public void setFactory(boolean factory) {
		_factory = factory;
	}

	public void setFactoryPid(String factoryPid) {
		_factoryPid = factoryPid;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setObjectClassDefinition(
		ObjectClassDefinition objectClassDefinition) {

		_objectClassDefinition = objectClassDefinition;
	}

	public void setPid(String pid) {
		_pid = pid;
	}

	private String _bundleLocation;
	private boolean _factory;
	private String _factoryPid;
	private final List<ConfigurableService> _instances = new ArrayList<>();
	private String _name;
	private ObjectClassDefinition _objectClassDefinition;
	private String _pid;

}
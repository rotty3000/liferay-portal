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

package com.liferay.portal.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * 
 * @author Milen Dyankov
 *
 */
public abstract class PKConfiguration {


	protected PKConfiguration (String... keys) {
		
		if (keys == null || keys.length == 0) {
			throw new IllegalArgumentException("PKConfiguration must have at least one field for primary key");
		}
			
		_primaryKey = new PK(keys);
		_valid = false;
	}

	
	public PK getKey () {
		return _primaryKey.copy();
	}

	public String getKey (String key) {
		return _primaryKey.get(key);
	}

	public Set<String> getKeys () {
		return Collections.unmodifiableSet(_primaryKey.getKeys());
	}

	public boolean isValid () {
		return _valid;
	}
	
	public String getPid() {
		return _pid;
	}

	public String factoryPid() {
		return _factoryPid;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((_pid == null) ? 0 : _pid.hashCode());
		return result;
	}


	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PKConfiguration other = (PKConfiguration) obj;
		if (_pid == null) {
			if (other._pid != null)
				return false;
		}
		else if (!_pid.equals(other._pid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [_primaryKey=" + _primaryKey + ", _pid=" + _pid + ", _factoryPid=" +
			_factoryPid + "]";
	}


	
	protected void buildKey (Map<String, Object> properties) {
		if (properties == null) {
			return;
		}

		_pid = GetterUtil.getString(properties.get(PKConfigurationConstants.PID));
		_factoryPid = GetterUtil.getString(properties.get(PKConfigurationConstants.FACTORY_PID));
		
		_valid = true;
		for (String key : _primaryKey.getKeys()) {
			Object value = properties.get(key);
			if (value == null || String.valueOf(value).trim().isEmpty()) {
				_valid = false;
				_primaryKey.add(key, null);
			}
			_primaryKey.add(key, String.valueOf(value));
		}
	}
	
	
	public static class PK {

		public PK (Map<String, String> map) {
			_map = new TreeMap<String, String>(map);
		}

		protected PK (String ... keys) {
			_map = new TreeMap<String, String>();
			for (int i = 0; i < keys.length; i++) {
				_map.put(keys[i], null);
			}
		}
		
		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 0;
			for (String key : _map.keySet()) {
				result = prime * result + ((_map.get(key) == null) ? 0 : _map.get(key).hashCode());
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj)
				return true;
			if (getClass() != obj.getClass())
				return false;
			PK other = (PK) obj;
			
			for (String key : _map.keySet()) {
				if (_map.get(key) == null) {
					if (other._map.get(key) != null)
						return false;
				}
				else if (!_map.get(key).equals(other._map.get(key)))
					return false;
			}
			return true;
		}

		public PK copy() {
			return new PK (_map);
		}

		
		protected Set<String> getKeys () {
			return _map.keySet();
		}
		
		public String get (String key) {
			return _map.get(key);
		}

		@Override
		public String toString () {
			StringBundler result = new StringBundler("[");
			boolean first = true;
			for (String key : _map.keySet()) {
				if (!first) {
					result.append(",");
				}
				first = false;
				result.append(key).append("=").append(_map.get(key));
			}
			return result.append("]").toString();
		}

		protected void add (String key, String value) {
			if (_map.containsKey(key)) {
				_map.put(key, value);
			}
		}

		private TreeMap<String, String> _map;

	}

	private String _factoryPid;
	private String _pid;
	private PK _primaryKey;
	private boolean _valid;

}

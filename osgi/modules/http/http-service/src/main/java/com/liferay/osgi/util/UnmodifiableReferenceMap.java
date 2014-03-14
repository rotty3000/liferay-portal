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

package com.liferay.osgi.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceReference;

/**
 * @author Raymond Augé
 */
public class UnmodifiableReferenceMap implements Map<String, Object> {

	public UnmodifiableReferenceMap(ServiceReference<?> reference) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (String key : reference.getPropertyKeys()) {
			map.put(key, reference.getProperty(key));
		}

		_map = Collections.unmodifiableMap(map);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Object get(Object key) {
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		return _map.keySet();
	}

	@Override
	public Collection<Object> values() {
		return _map.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return _map.entrySet();
	}

	private Map<String, Object> _map;

}
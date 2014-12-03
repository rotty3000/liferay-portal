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
package com.liferay.osgi.diagnostics.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Raymond Augé
 */
public class IntegrationPoint implements Comparable<IntegrationPoint> {

	public IntegrationPoint(String objectClass) {
		_objectClass = objectClass;
	}

	@Override
	public int compareTo(IntegrationPoint other) {
		return getObjectClass().compareTo(other.getObjectClass());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof IntegrationPoint)) {
			return false;
		}

		IntegrationPoint other = (IntegrationPoint)obj;

		return getObjectClass().equals(other.getObjectClass());
	}

	public Set<String> getFilters() {
		return _filters;
	}

	public String getObjectClass() {
		return _objectClass;
	}

	@Override
	public int hashCode() {
		return getObjectClass().hashCode();
	}

	private String _objectClass;
	private Set<String> _filters = new HashSet<>();

}
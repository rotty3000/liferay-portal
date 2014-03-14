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

package com.liferay.osgi.http.internal.holder;

import org.osgi.dto.DTO;

/**
 * @author Raymond Augé
 */
public abstract class Holder<T, D extends DTO> {

	public Holder(T t, D d) {
		this.t = t;
		this.d = d;
	}

	public abstract void destroy();
	public abstract T match(String requestURI, String name);

	protected boolean isPathWildcardMatch(String pattern, String path) {
		int cpl = pattern.length() - 2;

		if (pattern.endsWith("/*") && path.regionMatches(0, pattern, 0, cpl)) {
			if (path.length() == cpl || '/' == path.charAt(cpl)) {
				return true;
			}
		}

		return false;
	}

	protected boolean match(String pattern, String path, boolean noDefault)
		throws IllegalArgumentException {

		char firstChar = pattern.charAt(0);

		if (firstChar == '/') {
			if (!noDefault && (pattern.length() == 1) || pattern.equals(path)) {
				return true;
			}

			if(isPathWildcardMatch(pattern, path)) {
				return true;
			}
		}
		else if (firstChar == '*') {
			return path.regionMatches(
				path.length() - pattern.length() + 1,
				pattern, 1, pattern.length() - 1);
		}

		return false;
	}

	public final D d;
	public final T t;

}
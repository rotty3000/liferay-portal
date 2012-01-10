/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.util;

/**
 * @author Brian Wing Shun Chan
 */
public class PropsUtil_IW {
	public static PropsUtil_IW getInstance() {
		return _instance;
	}

	public void addProperties(java.util.Properties properties) {
		PropsUtil.addProperties(properties);
	}

	public void addProperties(
		com.liferay.portal.kernel.util.UnicodeProperties unicodeProperties) {
		PropsUtil.addProperties(unicodeProperties);
	}

	public boolean contains(java.lang.String key) {
		return PropsUtil.contains(key);
	}

	public java.lang.String get(java.lang.String key) {
		return PropsUtil.get(key);
	}

	public java.lang.String get(java.lang.String key,
		com.liferay.portal.kernel.configuration.Filter filter) {
		return PropsUtil.get(key, filter);
	}

	public java.lang.String[] getArray(java.lang.String key) {
		return PropsUtil.getArray(key);
	}

	public java.lang.String[] getArray(java.lang.String key,
		com.liferay.portal.kernel.configuration.Filter filter) {
		return PropsUtil.getArray(key, filter);
	}

	public java.util.Properties getProperties() {
		return PropsUtil.getProperties();
	}

	public java.util.Properties getProperties(java.lang.String prefix,
		boolean removePrefix) {
		return PropsUtil.getProperties(prefix, removePrefix);
	}

	public void reload() {
		PropsUtil.reload();
	}

	public void removeProperties(java.util.Properties properties) {
		PropsUtil.removeProperties(properties);
	}

	public void set(java.lang.String key, java.lang.String value) {
		PropsUtil.set(key, value);
	}

	private PropsUtil_IW() {
	}

	private static PropsUtil_IW _instance = new PropsUtil_IW();
}
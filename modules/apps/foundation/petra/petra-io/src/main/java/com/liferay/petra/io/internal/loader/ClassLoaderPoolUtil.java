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

package com.liferay.petra.io.internal.loader;

import com.liferay.petra.io.spi.loader.ClassLoaderPool;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Raymond Augé
 */
public class ClassLoaderPoolUtil {

	public static ClassLoader getClassLoader(String contextName) {
		return _classLoaderPool.getClassLoader(contextName);
	}

	public static String getContextName(ClassLoader classLoader) {
		return _classLoaderPool.getContextName(classLoader);
	}

	public static void register(String contextName, ClassLoader classLoader) {
		_classLoaderPool.register(contextName, classLoader);
	}

	public static void unregister(ClassLoader classLoader) {
		_classLoaderPool.unregister(classLoader);
	}

	public static void unregister(String contextName) {
		_classLoaderPool.unregister(contextName);
	}

	private ClassLoaderPoolUtil() {

		// Prevent Instantiation

	}

	private static final ClassLoaderPool _classLoaderPool;

	static {

		// Deliberately avoid using thread context class loader

		ServiceLoader<ClassLoaderPool> serviceLoader = ServiceLoader.load(
			ClassLoaderPool.class, ClassLoaderPool.class.getClassLoader());

		Iterator<ClassLoaderPool> iterator = serviceLoader.iterator();

		if (iterator.hasNext()) {
			_classLoaderPool = serviceLoader.iterator().next();
		}
		else {
			_classLoaderPool = new DefaultClassLoaderPool();
		}
	}

}
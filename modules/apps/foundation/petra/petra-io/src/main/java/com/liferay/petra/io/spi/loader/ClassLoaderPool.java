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

package com.liferay.petra.io.spi.loader;

/**
 * @author Raymond Augé
 */
public interface ClassLoaderPool {

	/**
	 * Returns the class loader associated with the context name.
	 *
	 * <p>
	 * If no class loader is found for the context name, the thread's context
	 * class loader is returned as a fallback.
	 * </p>
	 *
	 * @param  contextName the servlet context's name
	 * @return the class loader associated with the context name
	 */
	public ClassLoader getClassLoader(String contextName);

	/**
	 * Returns the context name associated with the class loader.
	 *
	 * <p>
	 * If the class loader is <code>null</code> or if no context name is
	 * associated with the class loader, {@link <code>NULL</code>} is
	 * returned.
	 * </p>
	 *
	 * @param  classLoader the class loader
	 * @return the context name associated with the class loader
	 */
	public String getContextName(ClassLoader classLoader);

	public void register(String contextName, ClassLoader classLoader);

	public void unregister(ClassLoader classLoader);

	public void unregister(String contextName);

}
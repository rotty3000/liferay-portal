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

package com.liferay.portal.kernel.test;

import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PropsImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class BaseTestCase extends TestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();

		Class<?> clazz = getClass();

		PortalClassLoaderUtil.setClassLoader(clazz.getClassLoader());

		PropsUtil.setProps(new PropsImpl());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		PortalClassLoaderUtil.setClassLoader(null);
	}

}
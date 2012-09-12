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

package com.liferay.portal.kernel.store;

import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;

import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
public class StoreFactoryUtil {

	public static Store createStore(
		String storeClassName, Properties initProperties)
		throws Exception {

		//TODO: Ray - which permission can I use please to check creation of the
		// instance ? In fact it tries to instantiate any provided class

		PortalRuntimePermission.checkGetBeanProperty(
			StoreFactoryUtil.class, storeClassName);

		return getStoreFactory().createStore(storeClassName, initProperties);
	}

	protected static StoreFactory getStoreFactory() {
		PortalRuntimePermission.checkGetBeanProperty(StoreFactory.class);

		return _storeFactory;
	}

	public void setStoreFactory(StoreFactory props) {
		PortalRuntimePermission.checkSetBeanProperty(StoreFactory.class);

		_storeFactory = props;
	}

	private static StoreFactory _storeFactory;

}
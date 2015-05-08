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

package com.liferay.shopping.service;

import aQute.bnd.annotation.ProviderType;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the remote service utility for ShoppingItem. This utility wraps
 * {@link com.liferay.shopping.service.impl.ShoppingItemServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on a remote server. Methods of this service are expected to have security
 * checks based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingItemService
 * @see com.liferay.shopping.service.base.ShoppingItemServiceBaseImpl
 * @see com.liferay.shopping.service.impl.ShoppingItemServiceImpl
 * @generated
 */
@ProviderType
public class ShoppingItemServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.shopping.service.impl.ShoppingItemServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */
	public static ShoppingItemService getService() {
		return _serviceTracker.getService();
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	@Deprecated
	public void setService(ShoppingItemService service) {
	}

	private static ServiceTracker<ShoppingItemService, ShoppingItemService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(ShoppingItemServiceUtil.class);

		_serviceTracker = new ServiceTracker<ShoppingItemService, ShoppingItemService>(bundle.getBundleContext(),
				ShoppingItemService.class, null);

		_serviceTracker.open();
	}
}
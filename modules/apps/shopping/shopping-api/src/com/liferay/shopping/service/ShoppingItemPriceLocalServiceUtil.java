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
 * Provides the local service utility for ShoppingItemPrice. This utility wraps
 * {@link com.liferay.shopping.service.impl.ShoppingItemPriceLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingItemPriceLocalService
 * @see com.liferay.shopping.service.base.ShoppingItemPriceLocalServiceBaseImpl
 * @see com.liferay.shopping.service.impl.ShoppingItemPriceLocalServiceImpl
 * @generated
 */
@ProviderType
public class ShoppingItemPriceLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.shopping.service.impl.ShoppingItemPriceLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */
	public static ShoppingItemPriceLocalService getService() {
		return _serviceTracker.getService();
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	@Deprecated
	public void setService(ShoppingItemPriceLocalService service) {
	}

	private static ServiceTracker<ShoppingItemPriceLocalService, ShoppingItemPriceLocalService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(ShoppingItemPriceLocalServiceUtil.class);

		_serviceTracker = new ServiceTracker<ShoppingItemPriceLocalService, ShoppingItemPriceLocalService>(bundle.getBundleContext(),
				ShoppingItemPriceLocalService.class, null);

		_serviceTracker.open();
	}
}
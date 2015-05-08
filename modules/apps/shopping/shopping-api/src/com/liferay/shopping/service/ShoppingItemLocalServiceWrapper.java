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

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ShoppingItemLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingItemLocalService
 * @generated
 */
@ProviderType
public class ShoppingItemLocalServiceWrapper implements ShoppingItemLocalService,
	ServiceWrapper<ShoppingItemLocalService> {
	public ShoppingItemLocalServiceWrapper(
		ShoppingItemLocalService shoppingItemLocalService) {
		_shoppingItemLocalService = shoppingItemLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public ShoppingItemLocalService getWrappedShoppingItemLocalService() {
		return _shoppingItemLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedShoppingItemLocalService(
		ShoppingItemLocalService shoppingItemLocalService) {
		_shoppingItemLocalService = shoppingItemLocalService;
	}

	@Override
	public ShoppingItemLocalService getWrappedService() {
		return _shoppingItemLocalService;
	}

	@Override
	public void setWrappedService(
		ShoppingItemLocalService shoppingItemLocalService) {
		_shoppingItemLocalService = shoppingItemLocalService;
	}

	private ShoppingItemLocalService _shoppingItemLocalService;
}
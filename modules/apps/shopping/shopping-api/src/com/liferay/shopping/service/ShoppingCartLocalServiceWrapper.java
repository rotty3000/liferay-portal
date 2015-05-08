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
 * Provides a wrapper for {@link ShoppingCartLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingCartLocalService
 * @generated
 */
@ProviderType
public class ShoppingCartLocalServiceWrapper implements ShoppingCartLocalService,
	ServiceWrapper<ShoppingCartLocalService> {
	public ShoppingCartLocalServiceWrapper(
		ShoppingCartLocalService shoppingCartLocalService) {
		_shoppingCartLocalService = shoppingCartLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public ShoppingCartLocalService getWrappedShoppingCartLocalService() {
		return _shoppingCartLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedShoppingCartLocalService(
		ShoppingCartLocalService shoppingCartLocalService) {
		_shoppingCartLocalService = shoppingCartLocalService;
	}

	@Override
	public ShoppingCartLocalService getWrappedService() {
		return _shoppingCartLocalService;
	}

	@Override
	public void setWrappedService(
		ShoppingCartLocalService shoppingCartLocalService) {
		_shoppingCartLocalService = shoppingCartLocalService;
	}

	private ShoppingCartLocalService _shoppingCartLocalService;
}
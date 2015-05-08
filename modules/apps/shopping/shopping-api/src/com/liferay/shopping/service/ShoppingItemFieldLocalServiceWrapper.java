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
 * Provides a wrapper for {@link ShoppingItemFieldLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingItemFieldLocalService
 * @generated
 */
@ProviderType
public class ShoppingItemFieldLocalServiceWrapper
	implements ShoppingItemFieldLocalService,
		ServiceWrapper<ShoppingItemFieldLocalService> {
	public ShoppingItemFieldLocalServiceWrapper(
		ShoppingItemFieldLocalService shoppingItemFieldLocalService) {
		_shoppingItemFieldLocalService = shoppingItemFieldLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public ShoppingItemFieldLocalService getWrappedShoppingItemFieldLocalService() {
		return _shoppingItemFieldLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedShoppingItemFieldLocalService(
		ShoppingItemFieldLocalService shoppingItemFieldLocalService) {
		_shoppingItemFieldLocalService = shoppingItemFieldLocalService;
	}

	@Override
	public ShoppingItemFieldLocalService getWrappedService() {
		return _shoppingItemFieldLocalService;
	}

	@Override
	public void setWrappedService(
		ShoppingItemFieldLocalService shoppingItemFieldLocalService) {
		_shoppingItemFieldLocalService = shoppingItemFieldLocalService;
	}

	private ShoppingItemFieldLocalService _shoppingItemFieldLocalService;
}
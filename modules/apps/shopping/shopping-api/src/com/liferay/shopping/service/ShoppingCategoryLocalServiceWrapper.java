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
 * Provides a wrapper for {@link ShoppingCategoryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingCategoryLocalService
 * @generated
 */
@ProviderType
public class ShoppingCategoryLocalServiceWrapper
	implements ShoppingCategoryLocalService,
		ServiceWrapper<ShoppingCategoryLocalService> {
	public ShoppingCategoryLocalServiceWrapper(
		ShoppingCategoryLocalService shoppingCategoryLocalService) {
		_shoppingCategoryLocalService = shoppingCategoryLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public ShoppingCategoryLocalService getWrappedShoppingCategoryLocalService() {
		return _shoppingCategoryLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedShoppingCategoryLocalService(
		ShoppingCategoryLocalService shoppingCategoryLocalService) {
		_shoppingCategoryLocalService = shoppingCategoryLocalService;
	}

	@Override
	public ShoppingCategoryLocalService getWrappedService() {
		return _shoppingCategoryLocalService;
	}

	@Override
	public void setWrappedService(
		ShoppingCategoryLocalService shoppingCategoryLocalService) {
		_shoppingCategoryLocalService = shoppingCategoryLocalService;
	}

	private ShoppingCategoryLocalService _shoppingCategoryLocalService;
}
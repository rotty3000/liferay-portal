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

package com.liferay.portlet.shopping.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ShoppingOrderService}.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingOrderService
 * @generated
 */
@ProviderType
public class ShoppingOrderServiceWrapper implements ShoppingOrderService,
	ServiceWrapper<ShoppingOrderService> {
	public ShoppingOrderServiceWrapper(
		ShoppingOrderService shoppingOrderService) {
		_shoppingOrderService = shoppingOrderService;
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _shoppingOrderService.getBeanIdentifier();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_shoppingOrderService.setBeanIdentifier(beanIdentifier);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public ShoppingOrderService getWrappedShoppingOrderService() {
		return _shoppingOrderService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedShoppingOrderService(
		ShoppingOrderService shoppingOrderService) {
		_shoppingOrderService = shoppingOrderService;
	}

	@Override
	public ShoppingOrderService getWrappedService() {
		return _shoppingOrderService;
	}

	@Override
	public void setWrappedService(ShoppingOrderService shoppingOrderService) {
		_shoppingOrderService = shoppingOrderService;
	}

	private ShoppingOrderService _shoppingOrderService;
}
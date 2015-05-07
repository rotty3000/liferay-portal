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
 * Provides a wrapper for {@link ShoppingOrderLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ShoppingOrderLocalService
 * @generated
 */
@ProviderType
public class ShoppingOrderLocalServiceWrapper
	implements ShoppingOrderLocalService,
		ServiceWrapper<ShoppingOrderLocalService> {
	public ShoppingOrderLocalServiceWrapper(
		ShoppingOrderLocalService shoppingOrderLocalService) {
		_shoppingOrderLocalService = shoppingOrderLocalService;
	}

	/**
	* Adds the shopping order to the database. Also notifies the appropriate model listeners.
	*
	* @param shoppingOrder the shopping order
	* @return the shopping order that was added
	*/
	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder addShoppingOrder(
		com.liferay.portlet.shopping.model.ShoppingOrder shoppingOrder) {
		return _shoppingOrderLocalService.addShoppingOrder(shoppingOrder);
	}

	/**
	* Creates a new shopping order with the primary key. Does not add the shopping order to the database.
	*
	* @param orderId the primary key for the new shopping order
	* @return the new shopping order
	*/
	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder createShoppingOrder(
		long orderId) {
		return _shoppingOrderLocalService.createShoppingOrder(orderId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.model.PersistedModel deletePersistedModel(
		com.liferay.portal.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _shoppingOrderLocalService.deletePersistedModel(persistedModel);
	}

	/**
	* Deletes the shopping order with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param orderId the primary key of the shopping order
	* @return the shopping order that was removed
	* @throws PortalException if a shopping order with the primary key could not be found
	*/
	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder deleteShoppingOrder(
		long orderId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _shoppingOrderLocalService.deleteShoppingOrder(orderId);
	}

	/**
	* Deletes the shopping order from the database. Also notifies the appropriate model listeners.
	*
	* @param shoppingOrder the shopping order
	* @return the shopping order that was removed
	*/
	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder deleteShoppingOrder(
		com.liferay.portlet.shopping.model.ShoppingOrder shoppingOrder) {
		return _shoppingOrderLocalService.deleteShoppingOrder(shoppingOrder);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _shoppingOrderLocalService.dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _shoppingOrderLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.portlet.shopping.model.impl.ShoppingOrderModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return _shoppingOrderLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.portlet.shopping.model.impl.ShoppingOrderModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return _shoppingOrderLocalService.dynamicQuery(dynamicQuery, start,
			end, orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _shoppingOrderLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return _shoppingOrderLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder fetchShoppingOrder(
		long orderId) {
		return _shoppingOrderLocalService.fetchShoppingOrder(orderId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _shoppingOrderLocalService.getActionableDynamicQuery();
	}

	/**
	* Returns the Spring bean ID for this bean.
	*
	* @return the Spring bean ID for this bean
	*/
	@Override
	public java.lang.String getBeanIdentifier() {
		return _shoppingOrderLocalService.getBeanIdentifier();
	}

	@Override
	public com.liferay.portal.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _shoppingOrderLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the shopping order with the primary key.
	*
	* @param orderId the primary key of the shopping order
	* @return the shopping order
	* @throws PortalException if a shopping order with the primary key could not be found
	*/
	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder getShoppingOrder(
		long orderId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _shoppingOrderLocalService.getShoppingOrder(orderId);
	}

	/**
	* Returns a range of all the shopping orders.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.portlet.shopping.model.impl.ShoppingOrderModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of shopping orders
	* @param end the upper bound of the range of shopping orders (not inclusive)
	* @return the range of shopping orders
	*/
	@Override
	public java.util.List<com.liferay.portlet.shopping.model.ShoppingOrder> getShoppingOrders(
		int start, int end) {
		return _shoppingOrderLocalService.getShoppingOrders(start, end);
	}

	/**
	* Returns the number of shopping orders.
	*
	* @return the number of shopping orders
	*/
	@Override
	public int getShoppingOrdersCount() {
		return _shoppingOrderLocalService.getShoppingOrdersCount();
	}

	/**
	* Sets the Spring bean ID for this bean.
	*
	* @param beanIdentifier the Spring bean ID for this bean
	*/
	@Override
	public void setBeanIdentifier(java.lang.String beanIdentifier) {
		_shoppingOrderLocalService.setBeanIdentifier(beanIdentifier);
	}

	/**
	* Updates the shopping order in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param shoppingOrder the shopping order
	* @return the shopping order that was updated
	*/
	@Override
	public com.liferay.portlet.shopping.model.ShoppingOrder updateShoppingOrder(
		com.liferay.portlet.shopping.model.ShoppingOrder shoppingOrder) {
		return _shoppingOrderLocalService.updateShoppingOrder(shoppingOrder);
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedService}
	 */
	@Deprecated
	public ShoppingOrderLocalService getWrappedShoppingOrderLocalService() {
		return _shoppingOrderLocalService;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #setWrappedService}
	 */
	@Deprecated
	public void setWrappedShoppingOrderLocalService(
		ShoppingOrderLocalService shoppingOrderLocalService) {
		_shoppingOrderLocalService = shoppingOrderLocalService;
	}

	@Override
	public ShoppingOrderLocalService getWrappedService() {
		return _shoppingOrderLocalService;
	}

	@Override
	public void setWrappedService(
		ShoppingOrderLocalService shoppingOrderLocalService) {
		_shoppingOrderLocalService = shoppingOrderLocalService;
	}

	private ShoppingOrderLocalService _shoppingOrderLocalService;
}
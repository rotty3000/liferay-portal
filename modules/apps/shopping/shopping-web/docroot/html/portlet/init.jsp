<%--
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
--%>

<%@ include file="/html/portlet/init.jsp" %>

<%@ page import="com.liferay.shopping.BillingCityException" %><%@
page import="com.liferay.shopping.BillingCountryException" %><%@
page import="com.liferay.shopping.BillingEmailAddressException" %><%@
page import="com.liferay.shopping.BillingFirstNameException" %><%@
page import="com.liferay.shopping.BillingLastNameException" %><%@
page import="com.liferay.shopping.BillingPhoneException" %><%@
page import="com.liferay.shopping.BillingStateException" %><%@
page import="com.liferay.shopping.BillingStreetException" %><%@
page import="com.liferay.shopping.BillingZipException" %><%@
page import="com.liferay.shopping.CCExpirationException" %><%@
page import="com.liferay.shopping.CCNameException" %><%@
page import="com.liferay.shopping.CCNumberException" %><%@
page import="com.liferay.shopping.CCTypeException" %><%@
page import="com.liferay.shopping.CartMinQuantityException" %><%@
page import="com.liferay.shopping.CategoryNameException" %><%@
page import="com.liferay.shopping.CouponActiveException" %><%@
page import="com.liferay.shopping.CouponCodeException" %><%@
page import="com.liferay.shopping.CouponDateException" %><%@
page import="com.liferay.shopping.CouponDescriptionException" %><%@
page import="com.liferay.shopping.CouponDiscountException" %><%@
page import="com.liferay.shopping.CouponEndDateException" %><%@
page import="com.liferay.shopping.CouponLimitCategoriesException" %><%@
page import="com.liferay.shopping.CouponLimitSKUsException" %><%@
page import="com.liferay.shopping.CouponMinimumOrderException" %><%@
page import="com.liferay.shopping.CouponNameException" %><%@
page import="com.liferay.shopping.CouponStartDateException" %><%@
page import="com.liferay.shopping.DuplicateCouponCodeException" %><%@
page import="com.liferay.shopping.DuplicateItemFieldNameException" %><%@
page import="com.liferay.shopping.DuplicateItemSKUException" %><%@
page import="com.liferay.shopping.ItemLargeImageNameException" %><%@
page import="com.liferay.shopping.ItemLargeImageSizeException" %><%@
page import="com.liferay.shopping.ItemMediumImageNameException" %><%@
page import="com.liferay.shopping.ItemMediumImageSizeException" %><%@
page import="com.liferay.shopping.ItemNameException" %><%@
page import="com.liferay.shopping.ItemSKUException" %><%@
page import="com.liferay.shopping.ItemSmallImageNameException" %><%@
page import="com.liferay.shopping.ItemSmallImageSizeException" %><%@
page import="com.liferay.shopping.NoSuchCategoryException" %><%@
page import="com.liferay.shopping.NoSuchCouponException" %><%@
page import="com.liferay.shopping.NoSuchItemException" %><%@
page import="com.liferay.shopping.NoSuchOrderException" %><%@
page import="com.liferay.shopping.ShippingCityException" %><%@
page import="com.liferay.shopping.ShippingCountryException" %><%@
page import="com.liferay.shopping.ShippingEmailAddressException" %><%@
page import="com.liferay.shopping.ShippingFirstNameException" %><%@
page import="com.liferay.shopping.ShippingLastNameException" %><%@
page import="com.liferay.shopping.ShippingPhoneException" %><%@
page import="com.liferay.shopping.ShippingStateException" %><%@
page import="com.liferay.shopping.ShippingStreetException" %><%@
page import="com.liferay.shopping.ShippingZipException" %><%@
page import="com.liferay.shopping.ShoppingGroupServiceSettings" %><%@
page import="com.liferay.shopping.model.ShoppingCart" %><%@
page import="com.liferay.shopping.model.ShoppingCartItem" %><%@
page import="com.liferay.shopping.model.ShoppingCategory" %><%@
page import="com.liferay.shopping.model.ShoppingCategoryConstants" %><%@
page import="com.liferay.shopping.model.ShoppingCoupon" %><%@
page import="com.liferay.shopping.model.ShoppingCouponConstants" %><%@
page import="com.liferay.shopping.model.ShoppingItem" %><%@
page import="com.liferay.shopping.model.ShoppingItemField" %><%@
page import="com.liferay.shopping.model.ShoppingItemPrice" %><%@
page import="com.liferay.shopping.model.ShoppingItemPriceConstants" %><%@
page import="com.liferay.shopping.model.ShoppingOrder" %><%@
page import="com.liferay.shopping.model.ShoppingOrderConstants" %><%@
page import="com.liferay.shopping.model.ShoppingOrderItem" %><%@
page import="com.liferay.shopping.search.CouponDisplayTerms" %><%@
page import="com.liferay.shopping.search.CouponSearch" %><%@
page import="com.liferay.shopping.search.OrderDisplayTerms" %><%@
page import="com.liferay.shopping.search.OrderSearch" %><%@
page import="com.liferay.shopping.search.OrderSearchTerms" %><%@
page import="com.liferay.shopping.service.ShoppingCartLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingCategoryServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingCouponLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingCouponServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingItemFieldLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingItemLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingItemPriceLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingItemServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingOrderItemLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.ShoppingOrderLocalServiceUtil" %><%@
page import="com.liferay.shopping.service.permission.ShoppingCategoryPermission" %><%@
page import="com.liferay.shopping.service.permission.ShoppingItemPermission" %><%@
page import="com.liferay.shopping.service.permission.ShoppingOrderPermission" %><%@
page import="com.liferay.shopping.service.permission.ShoppingPermission" %><%@
page import="com.liferay.shopping.util.ShoppingConstants" %><%@
page import="com.liferay.shopping.util.ShoppingUtil" %>

<%
PortalPreferences portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(request);

ShoppingGroupServiceSettings shoppingGroupServiceSettings = ShoppingGroupServiceSettings.getInstance(themeDisplay.getSiteGroupId());

Currency currency = Currency.getInstance(shoppingGroupServiceSettings.getCurrencyId());

NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

currencyFormat.setCurrency(currency);

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);

NumberFormat doubleFormat = NumberFormat.getNumberInstance(locale);

doubleFormat.setMaximumFractionDigits(2);
doubleFormat.setMinimumFractionDigits(2);

NumberFormat percentFormat = NumberFormat.getPercentInstance(locale);

NumberFormat taxFormat = NumberFormat.getPercentInstance(locale);

taxFormat.setMinimumFractionDigits(3);
%>

<%@ include file="init-ext.jsp" %>
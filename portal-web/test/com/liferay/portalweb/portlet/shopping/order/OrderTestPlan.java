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

package com.liferay.portalweb.portlet.shopping.order;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.shopping.order.checkoutorder.CheckoutOrderTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingcitynull.CheckoutOrderBillingCityNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingcountrynull.CheckoutOrderBillingCountryNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingemailaddressinvalid.CheckoutOrderBillingEmailAddressInvalidTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingemailaddressnull.CheckoutOrderBillingEmailAddressNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingfirstnamenull.CheckoutOrderBillingFirstNameNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillinglastnamenull.CheckoutOrderBillingLastNameNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingphonenull.CheckoutOrderBillingPhoneNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingstateinvalid.CheckoutOrderBillingStateInvalidTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingstreetnull.CheckoutOrderBillingStreetNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutorderbillingzipnull.CheckoutOrderBillingZipNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardnamenull.CheckoutOrderCreditCardNameNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardnumberinvalidamex.CheckoutOrderCreditCardNumberInvalidAmexTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardnumberinvaliddiscover.CheckoutOrderCreditCardNumberInvalidDiscoverTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardnumberinvalidmastercard.CheckoutOrderCreditCardNumberInvalidMasterCardTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardnumberinvalidvisa.CheckoutOrderCreditCardNumberInvalidVisaTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardnumbernull.CheckoutOrderCreditCardNumberNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordercreditcardtypenull.CheckoutOrderCreditCardTypeNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingcitynull.CheckoutOrderShippingCityNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingcountrynull.CheckoutOrderShippingCountryNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingemailaddressinvalid.CheckoutOrderShippingEmailAddressInvalidTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingemailaddressnull.CheckoutOrderShippingEmailAddressNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingfirstnamenull.CheckoutOrderShippingFirstNameNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippinglastnamenull.CheckoutOrderShippingLastNameNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingphonenull.CheckoutOrderShippingPhoneNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingstateinvalid.CheckoutOrderShippingStateInvalidTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingstreetnull.CheckoutOrderShippingStreetNullTests;
import com.liferay.portalweb.portlet.shopping.order.checkoutordershippingzipnull.CheckoutOrderShippingZipNullTests;
import com.liferay.portalweb.portlet.shopping.order.invoiceorder.InvoiceOrderTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class OrderTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(CheckoutOrderTests.suite());
		testSuite.addTest(CheckoutOrderBillingCityNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingCountryNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingEmailAddressInvalidTests.suite());
		testSuite.addTest(CheckoutOrderBillingEmailAddressNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingFirstNameNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingLastNameNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingPhoneNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingStateInvalidTests.suite());
		testSuite.addTest(CheckoutOrderBillingStreetNullTests.suite());
		testSuite.addTest(CheckoutOrderBillingZipNullTests.suite());
		testSuite.addTest(CheckoutOrderCreditCardNameNullTests.suite());
		testSuite.addTest(
			CheckoutOrderCreditCardNumberInvalidAmexTests.suite());
		testSuite.addTest(
			CheckoutOrderCreditCardNumberInvalidDiscoverTests.suite());
		testSuite.addTest(
			CheckoutOrderCreditCardNumberInvalidMasterCardTests.suite());
		testSuite.addTest(
			CheckoutOrderCreditCardNumberInvalidVisaTests.suite());
		testSuite.addTest(CheckoutOrderCreditCardNumberNullTests.suite());
		testSuite.addTest(CheckoutOrderCreditCardTypeNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingCityNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingCountryNullTests.suite());
		testSuite.addTest(
			CheckoutOrderShippingEmailAddressInvalidTests.suite());
		testSuite.addTest(CheckoutOrderShippingEmailAddressNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingFirstNameNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingLastNameNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingPhoneNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingStateInvalidTests.suite());
		testSuite.addTest(CheckoutOrderShippingStreetNullTests.suite());
		testSuite.addTest(CheckoutOrderShippingZipNullTests.suite());
		testSuite.addTest(InvoiceOrderTests.suite());

		return testSuite;
	}

}
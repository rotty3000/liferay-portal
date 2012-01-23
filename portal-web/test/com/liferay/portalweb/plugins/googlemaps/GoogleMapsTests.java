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

package com.liferay.portalweb.plugins.googlemaps;

import com.liferay.portalweb.portal.BaseTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class GoogleMapsTests extends BaseTestSuite {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(AddPageTest.class);
		testSuite.addTestSuite(AddPortletTest.class);
		testSuite.addTestSuite(AddGoogleMapTest.class);
		testSuite.addTestSuite(AssertGoogleMapTest.class);
		testSuite.addTestSuite(EnableInputTest.class);
		testSuite.addTestSuite(AssertEnableInputTest.class);
		testSuite.addTestSuite(GetMapTest.class);
		testSuite.addTestSuite(GetDirectionsTest.class);
		testSuite.addTestSuite(DisableInputTest.class);
		testSuite.addTestSuite(AssertDisableInputTest.class);
		testSuite.addTestSuite(AddGoogleMapWithMapTest.class);
		testSuite.addTestSuite(AssertGoogleMapWithMapTest.class);
		testSuite.addTestSuite(AddGoogleMapWithDirectionsTest.class);
		testSuite.addTestSuite(AssertGoogleMapWithDirectionsTest.class);
		testSuite.addTestSuite(TearDownTest.class);

		return testSuite;
	}
}
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

package com.liferay.portalweb.portlet.addapplication.sample;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.addapplication.sample.searchhellovelocity.SearchHelloVelocityTests;
import com.liferay.portalweb.portlet.addapplication.sample.searchhelloworld.SearchHelloWorldTests;
import com.liferay.portalweb.portlet.addapplication.sample.searchiframe.SearchIFrameTests;
import com.liferay.portalweb.portlet.addapplication.sample.searchwebproxy.SearchWebProxyTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class SampleTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(SearchHelloVelocityTests.suite());
		testSuite.addTest(SearchHelloWorldTests.suite());
		testSuite.addTest(SearchIFrameTests.suite());
		testSuite.addTest(SearchWebProxyTests.suite());

		return testSuite;
	}

}
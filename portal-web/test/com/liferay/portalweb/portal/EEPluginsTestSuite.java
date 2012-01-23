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

package com.liferay.portalweb.portal;

import com.liferay.portalweb.plugins.chat.ChatTests;
import com.liferay.portalweb.plugins.flash.FlashTests;
import com.liferay.portalweb.plugins.googleadsense.GoogleAdSenseTests;
import com.liferay.portalweb.plugins.googlemaps.GoogleMapsTests;
import com.liferay.portalweb.plugins.ipgeocoder.IPGeocoderTests;
import com.liferay.portalweb.plugins.mail.MailTestPlan;
import com.liferay.portalweb.plugins.socialcoding.SocialCodingTests;
import com.liferay.portalweb.plugins.socialnetworking.SocialNetworkingTests;
import com.liferay.portalweb.plugins.twitter.TwitterTests;
import com.liferay.portalweb.plugins.weather.WeatherTests;
import com.liferay.portalweb.plugins.webform.WebFormTests;
import com.liferay.portalweb.plugins.wsrp.WSRPTestPlan;
import com.liferay.portalweb.portal.login.LoginTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class EEPluginsTestSuite extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(LoginTests.suite());
		testSuite.addTest(ChatTests.suite());
		testSuite.addTest(FlashTests.suite());
		testSuite.addTest(GoogleAdSenseTests.suite());
		testSuite.addTest(GoogleMapsTests.suite());
		testSuite.addTest(IPGeocoderTests.suite());
		testSuite.addTest(MailTestPlan.suite());
		testSuite.addTest(SocialCodingTests.suite());
		testSuite.addTest(SocialNetworkingTests.suite());
		testSuite.addTest(TwitterTests.suite());
		testSuite.addTest(WeatherTests.suite());
		testSuite.addTest(WebFormTests.suite());
		testSuite.addTest(WSRPTestPlan.suite());

		testSuite.addTestSuite(StopSeleniumTest.class);

		return testSuite;
	}

}
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

package com.liferay.portalweb.portal.controlpanel.settings.portalsettings;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.editgeneral.EditGeneralTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.edittimezone.EditTimeZoneTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.enterdefaultuserassociationscommunity.EnterDefaultUserAssociationsCommunityTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.enterdefaultuserassociationsrole.EnterDefaultUserAssociationsRoleTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.enterdefaultuserassociationsusergroup.EnterDefaultUserAssociationsUserGroupTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.enterreservedemailaddress.EnterReservedEmailAddressTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.enterreservedscreenname.EnterReservedScreenNameTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationcas.ViewAuthenticationCASTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationgeneral.ViewAuthenticationGeneralTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationldap.ViewAuthenticationLDAPTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationntlm.ViewAuthenticationNTLMTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationopenid.ViewAuthenticationOpenIDTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationopensso.ViewAuthenticationOpenSSOTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewauthenticationsiteminder.ViewAuthenticationSiteMinderTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewdefaultuserassociations.ViewDefaultUserAssociationsTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewemailnotificationaccountcreated.ViewEmailNotificationAccountCreatedTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewemailnotificationgeneral.ViewEmailNotificationGeneralTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewemailnotificationpasswordchanged.ViewEmailNotificationPasswordChangedTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewmailhostnames.ViewMailHostNamesTests;
import com.liferay.portalweb.portal.controlpanel.settings.portalsettings.viewreservedscreennames.ViewReservedScreenNamesTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class PortalSettingsTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(EditGeneralTests.suite());
		testSuite.addTest(EditTimeZoneTests.suite());
		testSuite.addTest(EnterDefaultUserAssociationsCommunityTests.suite());
		testSuite.addTest(EnterDefaultUserAssociationsRoleTests.suite());
		testSuite.addTest(EnterDefaultUserAssociationsUserGroupTests.suite());
		testSuite.addTest(EnterReservedEmailAddressTests.suite());
		testSuite.addTest(EnterReservedScreenNameTests.suite());
		testSuite.addTest(ViewAuthenticationCASTests.suite());
		testSuite.addTest(ViewAuthenticationGeneralTests.suite());
		testSuite.addTest(ViewAuthenticationLDAPTests.suite());
		testSuite.addTest(ViewAuthenticationNTLMTests.suite());
		testSuite.addTest(ViewAuthenticationOpenIDTests.suite());
		testSuite.addTest(ViewAuthenticationOpenSSOTests.suite());
		testSuite.addTest(ViewAuthenticationSiteMinderTests.suite());
		testSuite.addTest(ViewDefaultUserAssociationsTests.suite());
		testSuite.addTest(ViewEmailNotificationAccountCreatedTests.suite());
		testSuite.addTest(ViewEmailNotificationGeneralTests.suite());
		testSuite.addTest(ViewEmailNotificationPasswordChangedTests.suite());
		testSuite.addTest(ViewMailHostNamesTests.suite());
		testSuite.addTest(ViewReservedScreenNamesTests.suite());

		return testSuite;
	}

}
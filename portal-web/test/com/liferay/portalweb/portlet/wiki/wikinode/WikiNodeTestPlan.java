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

package com.liferay.portalweb.portlet.wiki.wikinode;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.wiki.wikinode.addwikinode.AddWikiNodeTests;
import com.liferay.portalweb.portlet.wiki.wikinode.addwikinodemultiple.AddWikiNodeMultipleTests;
import com.liferay.portalweb.portlet.wiki.wikinode.addwikinodenameduplicate.AddWikiNodeNameDuplicateTests;
import com.liferay.portalweb.portlet.wiki.wikinode.addwikinodenamenull.AddWikiNodeNameNullTests;
import com.liferay.portalweb.portlet.wiki.wikinode.addwikinodenamenumber.AddWikiNodeNameNumberTests;
import com.liferay.portalweb.portlet.wiki.wikinode.addwikinodenamesymbol.AddWikiNodeNameSymbolTests;
import com.liferay.portalweb.portlet.wiki.wikinode.deletewikinode.DeleteWikiNodeTests;
import com.liferay.portalweb.portlet.wiki.wikinode.editwikinode.EditWikiNodeTests;
import com.liferay.portalweb.portlet.wiki.wikinode.viewwikinodefrontpage.ViewWikiNodeFrontPageTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class WikiNodeTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddWikiNodeTests.suite());
		testSuite.addTest(AddWikiNodeMultipleTests.suite());
		testSuite.addTest(AddWikiNodeNameDuplicateTests.suite());
		testSuite.addTest(AddWikiNodeNameNullTests.suite());
		testSuite.addTest(AddWikiNodeNameNumberTests.suite());
		testSuite.addTest(AddWikiNodeNameSymbolTests.suite());
		testSuite.addTest(DeleteWikiNodeTests.suite());
		testSuite.addTest(EditWikiNodeTests.suite());
		testSuite.addTest(ViewWikiNodeFrontPageTests.suite());

		return testSuite;
	}

}
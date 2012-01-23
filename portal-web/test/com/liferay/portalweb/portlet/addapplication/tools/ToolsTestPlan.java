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

package com.liferay.portalweb.portlet.addapplication.tools;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.addapplication.tools.searchdictionary.SearchDictionaryTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchlanguage.SearchLanguageTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchnetworkutilities.SearchNetworkUtilitiesTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchpasswordgenerator.SearchPasswordGeneratorTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchquicknote.SearchQuickNoteTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchsearch.SearchSearchTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchsignin.SearchSignInTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchsoftwarecatalog.SearchSoftwareCatalogTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchtranslator.SearchTranslatorTests;
import com.liferay.portalweb.portlet.addapplication.tools.searchunitconverter.SearchUnitConverterTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class ToolsTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(SearchDictionaryTests.suite());
		testSuite.addTest(SearchLanguageTests.suite());
		testSuite.addTest(SearchNetworkUtilitiesTests.suite());
		testSuite.addTest(SearchPasswordGeneratorTests.suite());
		testSuite.addTest(SearchQuickNoteTests.suite());
		testSuite.addTest(SearchSearchTests.suite());
		testSuite.addTest(SearchSignInTests.suite());
		testSuite.addTest(SearchSoftwareCatalogTests.suite());
		testSuite.addTest(SearchTranslatorTests.suite());
		testSuite.addTest(SearchUnitConverterTests.suite());

		return testSuite;
	}

}
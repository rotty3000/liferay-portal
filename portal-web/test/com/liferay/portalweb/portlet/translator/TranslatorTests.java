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

package com.liferay.portalweb.portlet.translator;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portal.util.TearDownPageTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class TranslatorTests extends BaseTestSuite {
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(AddPageTranslatorTest.class);
		testSuite.addTestSuite(AddPortletTranslatorTest.class);
		testSuite.addTestSuite(TranslateEnglishChineseCTest.class);
		testSuite.addTestSuite(TranslateEnglishChineseTTest.class);
		testSuite.addTestSuite(TranslateEnglishDutchTest.class);
		testSuite.addTestSuite(TranslateEnglishFrenchTest.class);
		testSuite.addTestSuite(TranslateEnglishGermanTest.class);
		testSuite.addTestSuite(TranslateEnglishItalianTest.class);
		testSuite.addTestSuite(TranslateEnglishJapaneseTest.class);
		testSuite.addTestSuite(TranslateEnglishPortugueseTest.class);
		testSuite.addTestSuite(TranslateEnglishSpanishTest.class);
		testSuite.addTestSuite(TranslateChineseCEnglishTest.class);
		testSuite.addTestSuite(TranslateChineseTEnglishTest.class);
		testSuite.addTestSuite(TranslateDutchEnglishTest.class);
		testSuite.addTestSuite(TranslateFrenchEnglishTest.class);
		testSuite.addTestSuite(TranslateFrenchGermanTest.class);
		testSuite.addTestSuite(TranslateGermanEnglishTest.class);
		testSuite.addTestSuite(TranslateGermanFrenchTest.class);
		testSuite.addTestSuite(TranslateItalianEnglishTest.class);
		testSuite.addTestSuite(TranslateJapaneseEnglishTest.class);
		testSuite.addTestSuite(TranslatePortugueseEnglishTest.class);
		testSuite.addTestSuite(TranslateSpanishEnglishTest.class);
		testSuite.addTestSuite(TearDownPageTest.class);

		return testSuite;
	}
}
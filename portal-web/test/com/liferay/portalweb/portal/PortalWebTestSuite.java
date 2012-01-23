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

import com.liferay.portalweb.portal.login.LoginTests;
import com.liferay.portalweb.portlet.activities.ActivitiesTestPlan;
import com.liferay.portalweb.portlet.amazonrankings.AmazonRankingsTestPlan;
import com.liferay.portalweb.portlet.announcements.AnnouncementsTestPlan;
import com.liferay.portalweb.portlet.assetpublisher.AssetPublisherTestPlan;
import com.liferay.portalweb.portlet.blogs.BlogsTestPlan;
import com.liferay.portalweb.portlet.blogsaggregator.BlogsAggregatorTestPlan;
import com.liferay.portalweb.portlet.bookmarks.BookmarksTestPlan;
import com.liferay.portalweb.portlet.breadcrumb.BreadcrumbTestPlan;
import com.liferay.portalweb.portlet.calendar.CalendarTestPlan;
import com.liferay.portalweb.portlet.currencyconverter.CurrencyConverterTests;
import com.liferay.portalweb.portlet.dictionary.DictionaryTestPlan;
import com.liferay.portalweb.portlet.directory.DirectoryTestPlan;
import com.liferay.portalweb.portlet.documentlibrary.DocumentLibraryTestPlan;
import com.liferay.portalweb.portlet.documentlibrarydisplay.DocumentLibraryDisplayTestPlan;
import com.liferay.portalweb.portlet.hellovelocity.HelloVelocityTestPlan;
import com.liferay.portalweb.portlet.helloworld.HelloWorldTestPlan;
import com.liferay.portalweb.portlet.iframe.IFrameTestPlan;
import com.liferay.portalweb.portlet.imagegallery.ImageGalleryTestPlan;
import com.liferay.portalweb.portlet.invitation.InvitationTestPlan;
import com.liferay.portalweb.portlet.language.LanguageTests;
import com.liferay.portalweb.portlet.loancalculator.LoanCalculatorTestPlan;
import com.liferay.portalweb.portlet.managepages.ManagePagesTestPlan;
import com.liferay.portalweb.portlet.messageboards.MessageBoardsTestPlan;
import com.liferay.portalweb.portlet.myaccount.MyAccountTestPlan;
import com.liferay.portalweb.portlet.navigation.NavigationTests;
import com.liferay.portalweb.portlet.nestedportlets.NestedPortletsTestPlan;
import com.liferay.portalweb.portlet.networkutilities.NetworkUtilitiesTestPlan;
import com.liferay.portalweb.portlet.pagecomments.PageCommentsTestPlan;
import com.liferay.portalweb.portlet.pageratings.PageRatingsTestPlan;
import com.liferay.portalweb.portlet.passwordgenerator.PasswordGeneratorTestPlan;
import com.liferay.portalweb.portlet.pollsdisplay.PollsDisplayTestPlan;
import com.liferay.portalweb.portlet.quicknote.QuickNoteTestPlan;
import com.liferay.portalweb.portlet.recentbloggers.RecentBloggersTestPlan;
import com.liferay.portalweb.portlet.recentdocuments.RecentDocumentsTestPlan;
import com.liferay.portalweb.portlet.rss.RSSTestPlan;
import com.liferay.portalweb.portlet.search.SearchTestPlan;
import com.liferay.portalweb.portlet.shopping.ShoppingTestPlan;
import com.liferay.portalweb.portlet.sitemap.SiteMapTestPlan;
import com.liferay.portalweb.portlet.softwarecatalog.SoftwareCatalogTestPlan;
import com.liferay.portalweb.portlet.translator.TranslatorTests;
import com.liferay.portalweb.portlet.unitconverter.UnitConverterTestPlan;
import com.liferay.portalweb.portlet.webcontentdisplay.WebContentDisplayTestPlan;
import com.liferay.portalweb.portlet.webcontentlist.WebContentListTestPlan;
import com.liferay.portalweb.portlet.webcontentsearch.WebContentSearchTestPlan;
import com.liferay.portalweb.portlet.webproxy.WebProxyTestPlan;
import com.liferay.portalweb.portlet.wiki.WikiTestPlan;
import com.liferay.portalweb.portlet.wikidisplay.WikiDisplayTestPlan;
import com.liferay.portalweb.portlet.words.WordsTestPlan;
import com.liferay.portalweb.portlet.xslcontent.XSLContentTestPlan;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class PortalWebTestSuite extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(LoginTests.suite());
		testSuite.addTest(ActivitiesTestPlan.suite());
		testSuite.addTest(AmazonRankingsTestPlan.suite());
		testSuite.addTest(AnnouncementsTestPlan.suite());
		testSuite.addTest(AssetPublisherTestPlan.suite());
		testSuite.addTest(BlogsTestPlan.suite());
		testSuite.addTest(BlogsAggregatorTestPlan.suite());
		testSuite.addTest(BookmarksTestPlan.suite());
		testSuite.addTest(BreadcrumbTestPlan.suite());
		testSuite.addTest(CalendarTestPlan.suite());
		testSuite.addTest(CurrencyConverterTests.suite());
		testSuite.addTest(DictionaryTestPlan.suite());
		testSuite.addTest(DirectoryTestPlan.suite());
		testSuite.addTest(DocumentLibraryTestPlan.suite());
		testSuite.addTest(DocumentLibraryDisplayTestPlan.suite());
		testSuite.addTest(HelloVelocityTestPlan.suite());
		testSuite.addTest(HelloWorldTestPlan.suite());
		testSuite.addTest(IFrameTestPlan.suite());
		testSuite.addTest(ImageGalleryTestPlan.suite());
		testSuite.addTest(InvitationTestPlan.suite());
		testSuite.addTest(LanguageTests.suite());
		testSuite.addTest(LoanCalculatorTestPlan.suite());
		testSuite.addTest(ManagePagesTestPlan.suite());
		testSuite.addTest(MessageBoardsTestPlan.suite());
		testSuite.addTest(MyAccountTestPlan.suite());
		testSuite.addTest(NavigationTests.suite());
		testSuite.addTest(NestedPortletsTestPlan.suite());
		testSuite.addTest(NetworkUtilitiesTestPlan.suite());
		testSuite.addTest(PageCommentsTestPlan.suite());
		testSuite.addTest(PageRatingsTestPlan.suite());
		testSuite.addTest(PasswordGeneratorTestPlan.suite());
		testSuite.addTest(PollsDisplayTestPlan.suite());
		testSuite.addTest(QuickNoteTestPlan.suite());
		testSuite.addTest(RecentBloggersTestPlan.suite());
		testSuite.addTest(RecentDocumentsTestPlan.suite());
		testSuite.addTest(RSSTestPlan.suite());
		testSuite.addTest(SearchTestPlan.suite());
		testSuite.addTest(ShoppingTestPlan.suite());
		testSuite.addTest(SiteMapTestPlan.suite());
		testSuite.addTest(SoftwareCatalogTestPlan.suite());
		testSuite.addTest(TranslatorTests.suite());
		testSuite.addTest(UnitConverterTestPlan.suite());
		testSuite.addTest(WebContentDisplayTestPlan.suite());
		testSuite.addTest(WebContentListTestPlan.suite());
		testSuite.addTest(WebContentSearchTestPlan.suite());
		testSuite.addTest(WebProxyTestPlan.suite());
		testSuite.addTest(WikiTestPlan.suite());
		testSuite.addTest(WikiDisplayTestPlan.suite());
		testSuite.addTest(WordsTestPlan.suite());
		testSuite.addTest(XSLContentTestPlan.suite());

		testSuite.addTestSuite(StopSeleniumTest.class);

		return testSuite;
	}

}
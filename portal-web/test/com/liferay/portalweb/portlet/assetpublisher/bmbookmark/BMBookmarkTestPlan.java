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

package com.liferay.portalweb.portlet.assetpublisher.bmbookmark;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.addnewbmfolderbookmarkapactions.AddNewBMFolderBookmarkAPActionsTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.deletebmfolderbookmarkap.DeleteBMFolderBookmarkAPTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.selectexistingbmfolderbookmarkapactions.SelectExistingBMFolderBookmarkAPActionsTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.viewconfigureportletabstractsbmfolderbookmarkap.ViewConfigurePortletAbstractsBMFolderBookmarkAPTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.viewconfigureportletavailablebookmarksentryap.ViewConfigurePortletAvailableBookmarksEntryAPTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.viewconfigureportletcurrentbookmarksentryap.ViewConfigurePortletCurrentBookmarksEntryAPTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.viewconfigureportletfullcontentbmfolderbookmarkap.ViewConfigurePortletFullContentBMFolderBookmarkAPTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.viewconfigureportlettablebmfolderbookmarkap.ViewConfigurePortletTableBMFolderBookmarkAPTests;
import com.liferay.portalweb.portlet.assetpublisher.bmbookmark.viewconfigureportlettitlelistbmfolderbookmarkap.ViewConfigurePortletTitleListBMFolderBookmarkAPTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class BMBookmarkTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddNewBMFolderBookmarkAPActionsTests.suite());
		testSuite.addTest(DeleteBMFolderBookmarkAPTests.suite());
		testSuite.addTest(SelectExistingBMFolderBookmarkAPActionsTests.suite());
		testSuite.addTest(
			ViewConfigurePortletAbstractsBMFolderBookmarkAPTests.suite());
		testSuite.addTest(
			ViewConfigurePortletAvailableBookmarksEntryAPTests.suite());
		testSuite.addTest(
			ViewConfigurePortletCurrentBookmarksEntryAPTests.suite());
		testSuite.addTest(
			ViewConfigurePortletFullContentBMFolderBookmarkAPTests.suite());
		testSuite.addTest(
			ViewConfigurePortletTableBMFolderBookmarkAPTests.suite());
		testSuite.addTest(
			ViewConfigurePortletTitleListBMFolderBookmarkAPTests.suite());

		return testSuite;
	}

}
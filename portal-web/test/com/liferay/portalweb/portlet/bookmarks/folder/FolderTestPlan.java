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

package com.liferay.portalweb.portlet.bookmarks.folder;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.bookmarks.folder.addfolder.AddFolderTests;
import com.liferay.portalweb.portlet.bookmarks.folder.addfoldermultiple.AddFolderMultipleTests;
import com.liferay.portalweb.portlet.bookmarks.folder.addfoldernamenull.AddFolderNameNullTests;
import com.liferay.portalweb.portlet.bookmarks.folder.addsubfolder.AddSubfolderTests;
import com.liferay.portalweb.portlet.bookmarks.folder.combinesubfoldertofolder.CombineSubfolderToFolderTests;
import com.liferay.portalweb.portlet.bookmarks.folder.deletefolder.DeleteFolderTests;
import com.liferay.portalweb.portlet.bookmarks.folder.deletesubfolder.DeleteSubfolderTests;
import com.liferay.portalweb.portlet.bookmarks.folder.editfolder.EditFolderTests;
import com.liferay.portalweb.portlet.bookmarks.folder.editsubfolder.EditSubfolderTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class FolderTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddFolderTests.suite());
		testSuite.addTest(AddFolderMultipleTests.suite());
		testSuite.addTest(AddFolderNameNullTests.suite());
		testSuite.addTest(AddSubfolderTests.suite());
		testSuite.addTest(CombineSubfolderToFolderTests.suite());
		testSuite.addTest(DeleteFolderTests.suite());
		testSuite.addTest(DeleteSubfolderTests.suite());
		testSuite.addTest(EditFolderTests.suite());
		testSuite.addTest(EditSubfolderTests.suite());

		return testSuite;
	}

}
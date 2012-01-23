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

package com.liferay.portalweb.portlet.imagegallery.folder;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.imagegallery.folder.addfolder.AddFolderTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addfoldermultiple.AddFolderMultipleTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addfoldernameduplicate.AddFolderNameDuplicateTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addfoldernameinvalid.AddFolderNameInvalidTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addfoldernamenull.AddFolderNameNullTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addsubfolder.AddSubfolderTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addsubfoldermultiple.AddSubfolderMultipleTests;
import com.liferay.portalweb.portlet.imagegallery.folder.addsubfoldernameimagename.AddSubfolderNameImageNameTests;
import com.liferay.portalweb.portlet.imagegallery.folder.deletefolder.DeleteFolderTests;
import com.liferay.portalweb.portlet.imagegallery.folder.deletesubfolder.DeleteSubfolderTests;
import com.liferay.portalweb.portlet.imagegallery.folder.editfolder.EditFolderTests;
import com.liferay.portalweb.portlet.imagegallery.folder.editsubfolder.EditSubfolderTests;
import com.liferay.portalweb.portlet.imagegallery.folder.movesubfoldertofolder.MoveSubfolderToFolderTests;

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
		testSuite.addTest(AddFolderNameDuplicateTests.suite());
		testSuite.addTest(AddFolderNameInvalidTests.suite());
		testSuite.addTest(AddFolderNameNullTests.suite());
		testSuite.addTest(AddSubfolderTests.suite());
		testSuite.addTest(AddSubfolderMultipleTests.suite());
		testSuite.addTest(AddSubfolderNameImageNameTests.suite());
		testSuite.addTest(DeleteFolderTests.suite());
		testSuite.addTest(DeleteSubfolderTests.suite());
		testSuite.addTest(EditFolderTests.suite());
		testSuite.addTest(EditSubfolderTests.suite());
		testSuite.addTest(MoveSubfolderToFolderTests.suite());

		return testSuite;
	}

}
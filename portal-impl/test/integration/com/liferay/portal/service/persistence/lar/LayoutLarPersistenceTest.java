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

package com.liferay.portal.service.persistence.lar;

import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.lar.PortletDataContextImpl;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.test.EnvironmentExecutionTestListener;
import com.liferay.portal.test.ExecutionTestListeners;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;

/**
 * @author Mate Thurzo
 */
@ExecutionTestListeners(listeners = {EnvironmentExecutionTestListener.class})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class LayoutLarPersistenceTest extends PowerMockito {

	@Test
	public void testSerialize() throws Exception {
		Layout layout = LayoutLocalServiceUtil.getLayout(10222);

		LayoutLarPersistenceImpl layoutLarPersistence =
			new LayoutLarPersistenceImpl();

		layoutLarPersistence.serialize(
			layout,
			new PortletDataContextImpl(
				10201, 10219, null, null, null, null, null));

		ZipWriter zipWriter = layoutLarPersistence.getZipWriter();

		File file = zipWriter.getFile();

		System.out.println("file: " + file);
		System.out.println("file path: " + file.getAbsolutePath());
	}
}

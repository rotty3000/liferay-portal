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

package com.liferay.portlet.documentlibrary.trash;

import com.liferay.portal.kernel.trash.BaseTrashHandler;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;

/**
 * @author Alexander Chow
 */
public class DLFileEntryTrashHandler extends BaseTrashHandler {

	public static final String CLASS_NAME = DLFileEntry.class.getName();

	public void deleteTrashEntries(long[] classPKs) {

		// LPS-26339

	}

	public String getClassName() {
		return CLASS_NAME;
	}

	public void restoreTrashEntries(long[] classPKs) {

		// LPS-26339

	}

}
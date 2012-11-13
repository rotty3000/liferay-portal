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

package com.liferay.web.extender.internal.introspection;

import java.io.IOException;
import java.io.InputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Raymond Augé
 */
public class ZipSource extends ClassLoaderSource {

	public ZipSource(ZipFile zipFile, ClassLoader classLoader) {
		super(classLoader);

		_zipFile = zipFile;
	}

	@Override
	public InputStream getResourceAsStream(String name) throws IOException {
		ZipEntry entry = _zipFile.getEntry(name);

		if (entry != null) {
			return _zipFile.getInputStream(entry);
		}

		return super.getResourceAsStream(name);
	}

	private ZipFile _zipFile;

}
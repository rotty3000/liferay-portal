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

package com.liferay.portal.service.persistence;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.lar.XStreamWrapper;
import com.liferay.portal.model.BaseModel;

import java.io.InputStream;

/**
 * @author Mate Thurzo
 */
public interface BaseLarPersistence<T extends BaseModel<T>> {

	public ZipWriter getZipWriter();

	public ZipWriter getZipWriter(boolean newInstance);

	public void addZipEntry(String path, T object) throws SystemException;

	public void addZipEntry(String path, byte[] bytes) throws SystemException;

	public void addZipEntry(String path, InputStream is)
		throws SystemException;

	public void addExpando(T object) throws SystemException;

	public String toXML(Object object);

	public void setXstreamWrapper(XStreamWrapper xstreamWrapper);

	public XStreamWrapper getXStreamWrapper();

}

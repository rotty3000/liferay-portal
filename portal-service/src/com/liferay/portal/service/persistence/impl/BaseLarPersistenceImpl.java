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

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.PortletDataContextListener;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;
import com.liferay.portal.lar.XStreamWrapper;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.ClassedModel;
import com.liferay.portal.service.persistence.BaseLarPersistence;
import com.liferay.portal.service.persistence.lar.BookmarksEntryLarPersistence;
import com.liferay.portal.service.persistence.lar.BookmarksFolderLarPersistence;
import com.liferay.portal.service.persistence.lar.ImageLarPersistence;
import com.liferay.portal.service.persistence.lar.RoleLarPersistence;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.journal.service.persistence.lar.JournalArticleLarPersistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Mate Thurzo
 */
public class BaseLarPersistenceImpl<T extends BaseModel<T>>
	implements BaseLarPersistence<T> {

	public ZipWriter getZipWriter() {
		return getZipWriter(false);
	}

	public ZipWriter getZipWriter(boolean newInstance) {
		if (newInstance) {
			return ZipWriterFactoryUtil.getZipWriter();
		}

		return _zipWriter;
	}

	public void addZipEntry(String path, T object) throws SystemException {
		addZipEntry(path, toXML(object));
	}

	public void addZipEntry(String path, byte[] bytes) throws SystemException {
		if (_portletDataContextListener != null) {
			_portletDataContextListener.onAddZipEntry(path);
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, bytes);
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}
	}

	public void addZipEntry(String path, InputStream is)
			throws SystemException {

		if (_portletDataContextListener != null) {
			_portletDataContextListener.onAddZipEntry(path);
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, is);
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}
	}

	public void addZipEntry(String path, Object object) throws SystemException {
		addZipEntry(path, toXML(object));
	}

	public void addZipEntry(String path, String s) throws SystemException {
		if (_portletDataContextListener != null) {
			_portletDataContextListener.onAddZipEntry(path);
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, s);
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}
	}

	public void addZipEntry(String path, StringBuilder sb)
			throws SystemException {

		if (_portletDataContextListener != null) {
			_portletDataContextListener.onAddZipEntry(path);
		}

		try {
			ZipWriter zipWriter = getZipWriter();

			zipWriter.addEntry(path, sb);
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}
	}

	public void addExpando(T object) throws SystemException {
		if (!(object instanceof ClassedModel)) {
			return;
		}

		ClassedModel classedModel = (ClassedModel)object;

		String expandoPath = getEntityPath(object);
		expandoPath = StringUtil.replace(expandoPath, ".xml", "-expando.xml");

		ExpandoBridge expandoBridge = classedModel.getExpandoBridge();

		addZipEntry(expandoPath, expandoBridge.getAttributes());
	}

	public String getEntityPath(T object) {
		if (object instanceof BaseModel) {
			BaseModel<T> baseModel = (BaseModel<T>)object;

			Map<String, Object> modelAttributes =
				baseModel.getModelAttributes();

			StringBundler sb = new StringBundler();

			sb.append(modelAttributes.get("groupId"));
			sb.append(StringPool.FORWARD_SLASH);
			sb.append(baseModel.getModelClassName());
			sb.append(StringPool.FORWARD_SLASH);
			sb.append(baseModel.getPrimaryKeyObj() + ".xml");

			return sb.toString();
		}

		return StringPool.BLANK;
	}

	public String toXML(Object object) {
		String rv = null;

		if (_xStreamWrapper != null) {
			rv = _xStreamWrapper.toXML(object);
		}
		else {
			Object o = PortalBeanLocatorUtil.locate("xStreamWrapper");

			if (o != null) {
				return ((XStreamWrapper)o).toXML(object);
			}
		}

		return rv;
	}

	public void setXstreamWrapper(XStreamWrapper xstreamWrapper) {
		_xStreamWrapper = xstreamWrapper;
	}

	public XStreamWrapper getXStreamWrapper() {
		return _xStreamWrapper;
	}

	private static ZipWriter _zipWriter = ZipWriterFactoryUtil.getZipWriter();
	private XStreamWrapper _xStreamWrapper;

	private PortletDataContextListener _portletDataContextListener;

	@BeanReference(type = JournalArticleLarPersistence.class)
	protected JournalArticleLarPersistence journalArticleLarPersistence;
	@BeanReference(type = ImageLarPersistence.class)
	protected ImageLarPersistence imageLarPersistence;
	@BeanReference(type = RoleLarPersistence.class)
	protected RoleLarPersistence roleLarPersistence;
	@BeanReference(type = BookmarksEntryLarPersistence.class)
	protected BookmarksEntryLarPersistence bookmarksEntryLarPersistence;
	@BeanReference(type = BookmarksFolderLarPersistence.class)
	protected BookmarksFolderLarPersistence bookmarksFolderLarPersistence;

}

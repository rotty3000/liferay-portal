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

package com.liferay.portlet.documentlibrary.store;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
@Deprecated
public class DeprecatedStoreWrapper implements com.liferay.portal.kernel.store.Store {
	private Store deprecatedStore;

	public DeprecatedStoreWrapper(Store deprecatedStore) {
		this.deprecatedStore = deprecatedStore;
	}

	public void addDirectory(long companyId, long repositoryId, String dirName) throws PortalException, SystemException {
		deprecatedStore.addDirectory(companyId, repositoryId, dirName);
	}

	public void addFile(long companyId, long repositoryId, String fileName, byte[] bytes) throws PortalException, SystemException {
		deprecatedStore.addFile(companyId, repositoryId, fileName, bytes);
	}

	public void addFile(long companyId, long repositoryId, String fileName, File file) throws PortalException, SystemException {
		deprecatedStore.addFile(companyId, repositoryId, fileName, file);
	}

	public void addFile(long companyId, long repositoryId, String fileName, InputStream is) throws PortalException, SystemException {
		deprecatedStore.addFile(companyId, repositoryId, fileName, is);
	}

	public void checkRoot(long companyId) throws SystemException {
		deprecatedStore.checkRoot(companyId);
	}

	public void copyFileVersion(long companyId, long repositoryId, String fileName, String fromVersionLabel, String toVersionLabel) throws PortalException, SystemException {
		deprecatedStore.copyFileVersion(companyId, repositoryId, fileName, fromVersionLabel, toVersionLabel);
	}

	public void deleteDirectory(long companyId, long repositoryId, String dirName) throws PortalException, SystemException {
		deprecatedStore.deleteDirectory(companyId, repositoryId, dirName);
	}

	public void deleteFile(long companyId, long repositoryId, String fileName) throws PortalException, SystemException {
		deprecatedStore.deleteFile(companyId, repositoryId, fileName);
	}

	public void deleteFile(long companyId, long repositoryId, String fileName, String versionLabel) throws PortalException, SystemException {
		deprecatedStore.deleteFile(companyId, repositoryId, fileName, versionLabel);
	}

	public void destroy() throws PortalException, SystemException {

	}

	public File getFile(long companyId, long repositoryId, String fileName) throws PortalException, SystemException {
		return deprecatedStore.getFile(companyId, repositoryId, fileName);
	}

	public File getFile(long companyId, long repositoryId, String fileName, String versionLabel) throws PortalException, SystemException {
		return deprecatedStore.getFile(companyId, repositoryId, fileName, versionLabel);
	}

	public byte[] getFileAsBytes(long companyId, long repositoryId, String fileName) throws PortalException, SystemException {
		return deprecatedStore.getFileAsBytes(companyId, repositoryId, fileName);
	}

	public byte[] getFileAsBytes(long companyId, long repositoryId, String fileName, String versionLabel) throws PortalException, SystemException {
		return deprecatedStore.getFileAsBytes(companyId, repositoryId, fileName, versionLabel);
	}

	public InputStream getFileAsStream(long companyId, long repositoryId, String fileName) throws PortalException, SystemException {
		return deprecatedStore.getFileAsStream(companyId, repositoryId, fileName);
	}

	public InputStream getFileAsStream(long companyId, long repositoryId, String fileName, String versionLabel) throws PortalException, SystemException {
		return deprecatedStore.getFileAsStream(companyId, repositoryId, fileName, versionLabel);
	}

	public String[] getFileNames(long companyId, long repositoryId) throws SystemException {
		return deprecatedStore.getFileNames(companyId, repositoryId);
	}

	public String[] getFileNames(long companyId, long repositoryId, String dirName) throws PortalException, SystemException {
		return deprecatedStore.getFileNames(companyId, repositoryId, dirName);
	}

	public long getFileSize(long companyId, long repositoryId, String fileName) throws PortalException, SystemException {
		return deprecatedStore.getFileSize(companyId, repositoryId, fileName);
	}

	public String getInitPropertiesKey() {
		return StringPool.DOUBLE_SLASH;
	}

	public boolean hasDirectory(long companyId, long repositoryId, String dirName) throws PortalException, SystemException {
		return deprecatedStore.hasDirectory(companyId, repositoryId, dirName);
	}

	public boolean hasFile(long companyId, long repositoryId, String fileName) throws PortalException, SystemException {
		return deprecatedStore.hasFile(companyId, repositoryId, fileName);
	}

	public boolean hasFile(long companyId, long repositoryId, String fileName, String versionLabel) throws PortalException, SystemException {
		return deprecatedStore.hasFile(companyId, repositoryId, fileName, versionLabel);
	}

	public void init(Properties configuration) throws PortalException, SystemException {

	}

	public void move(String srcDir, String destDir) throws SystemException {
		deprecatedStore.move(srcDir, destDir);
	}

	public void updateFile(long companyId, long repositoryId, long newRepositoryId, String fileName) throws PortalException, SystemException {
		deprecatedStore.updateFile(companyId, repositoryId, newRepositoryId, fileName);
	}

	public void updateFile(long companyId, long repositoryId, String fileName, String newFileName) throws PortalException, SystemException {
		deprecatedStore.updateFile(companyId, repositoryId, fileName, newFileName);
	}

	public void updateFile(long companyId, long repositoryId, String fileName, String versionLabel, byte[] bytes) throws PortalException, SystemException {
		deprecatedStore.updateFile(companyId, repositoryId, fileName, versionLabel, bytes);
	}

	public void updateFile(long companyId, long repositoryId, String fileName, String versionLabel, File file) throws PortalException, SystemException {
		deprecatedStore.updateFile(companyId, repositoryId, fileName, versionLabel, file);
	}

	public void updateFile(long companyId, long repositoryId, String fileName, String versionLabel, InputStream is) throws PortalException, SystemException {
		deprecatedStore.updateFile(companyId, repositoryId, fileName, versionLabel, is);
	}

	public void updateFileVersion(long companyId, long repositoryId, String fileName, String fromVersionLabel, String toVersionLabel) throws PortalException, SystemException {
		deprecatedStore.updateFileVersion(companyId, repositoryId, fileName, fromVersionLabel, toVersionLabel);
	}
}

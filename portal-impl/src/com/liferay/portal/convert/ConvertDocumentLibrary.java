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

package com.liferay.portal.convert;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.security.pacl.PACLClassLoaderUtil;
import com.liferay.portal.util.MaintenanceUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.DuplicateDirectoryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileVersion;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.store.Store;
import com.liferay.portlet.documentlibrary.store.StoreFactory;
import com.liferay.portlet.documentlibrary.util.comparator.FileVersionVersionComparator;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.wiki.model.WikiPage;
import com.liferay.portlet.wiki.service.WikiPageLocalServiceUtil;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Minhchau Dang
 * @author Alexander Chow
 */
public abstract class ConvertDocumentLibrary extends ConvertProcess {

	@Override
	public String getParameterDescription() {
		return "please-fill-out-store-parameters-or-leave-blank-to-use-" +
			"defaults";
	}

	@Override
	public String[] getParameterNames() {
		ArrayList<String> result = new ArrayList<String>();

		String storeClassName = getStoreClassName();

		try {
			ClassLoader classLoader =
				PACLClassLoaderUtil.getPortalClassLoader();

			Store store = (Store) InstanceFactory.newInstance(
				classLoader, storeClassName);

			String propsKey = store.getInitPropertiesKey();

			Properties properties = PropsUtil.getProperties(propsKey, false);

			result.addAll(properties.stringPropertyNames());
		} catch (Exception e) {
			_log.warn("Cannot instantiate " + storeClassName, e);
		}

		return result.toArray(new String[0]);
	}

	public abstract String getStoreClassName();

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected void doConvert() throws Exception {
		_sourceStore = StoreFactory.getInstance();

		String[] values = getParameterValues();

		String targetStoreClassName = getStoreClassName();

		Properties initProperties = new Properties();
		String[] parameterNames = getParameterNames();

		for (int i = 0; i < parameterNames.length; i++) {
			String value = values[i];
			String key = parameterNames[i];
			if (Validator.isNull(value)) {
				value = PropsUtil.get(key);
			}

			initProperties.setProperty(key, value);
		}

		_targetStore = StoreFactory.createStore(
			targetStoreClassName, initProperties);

		migratePortlets();

		StoreFactory.setInstance(_targetStore);

		if (_sourceStore != null) {
			_sourceStore.destroy();
		}

		MaintenanceUtil.appendStatus(
			"Please set " + PropsKeys.DL_STORE_IMPL +
				" in your portal-ext.properties to use " +
					targetStoreClassName);

		PropsValues.DL_STORE_IMPL = targetStoreClassName;
	}

	protected List<DLFileVersion> getDLFileVersions(DLFileEntry dlFileEntry)
		throws SystemException {

		List<DLFileVersion> dlFileVersions = dlFileEntry.getFileVersions(
			WorkflowConstants.STATUS_ANY);

		return ListUtil.sort(
			dlFileVersions, new FileVersionVersionComparator(true));
	}

	protected void migrateDL() throws Exception {
		int count = DLFileEntryLocalServiceUtil.getFileEntriesCount();

		MaintenanceUtil.appendStatus(
			"Migrating " + count + " documents and media files");

		int pages = count / Indexer.DEFAULT_INTERVAL;

		for (int i = 0; i <= pages; i++) {
			int start = (i * Indexer.DEFAULT_INTERVAL);
			int end = start + Indexer.DEFAULT_INTERVAL;

			List<DLFileEntry> dlFileEntries =
				DLFileEntryLocalServiceUtil.getFileEntries(start, end);

			for (DLFileEntry dlFileEntry : dlFileEntries) {
				long companyId = dlFileEntry.getCompanyId();
				long repositoryId = dlFileEntry.getDataRepositoryId();

				migrateDLFileEntry(companyId, repositoryId, dlFileEntry);
			}
		}
	}

	protected void migrateDLFileEntry(
			long companyId, long repositoryId, DLFileEntry fileEntry)
		throws Exception {

		String fileName = fileEntry.getName();

		List<DLFileVersion> dlFileVersions = getDLFileVersions(fileEntry);

		if (dlFileVersions.isEmpty()) {
			String versionNumber = Store.VERSION_DEFAULT;

			migrateFile(companyId, repositoryId, fileName, versionNumber);

			return;
		}

		for (DLFileVersion dlFileVersion : dlFileVersions) {
			String versionNumber = dlFileVersion.getVersion();

			migrateFile(companyId, repositoryId, fileName, versionNumber);
		}
	}

	protected void migrateFile(
		long companyId, long repositoryId, String fileName,
		String versionNumber) {

		try {
			InputStream is = _sourceStore.getFileAsStream(
				companyId, repositoryId, fileName, versionNumber);

			if (versionNumber.equals(Store.VERSION_DEFAULT)) {
				_targetStore.addFile(companyId, repositoryId, fileName, is);
			}
			else {
				_targetStore.updateFile(
					companyId, repositoryId, fileName, versionNumber, is);
			}
		}
		catch (Exception e) {
			_log.error("Migration failed for " + fileName, e);
		}
	}

	protected void migrateFiles(
			long companyId, String dirName, String[] fileNames)
		throws Exception {

		long repositoryId = CompanyConstants.SYSTEM;
		String versionNumber = Store.VERSION_DEFAULT;

		try {
			_targetStore.addDirectory(companyId, repositoryId, dirName);
		}
		catch (DuplicateDirectoryException dde) {
		}

		for (String fileName : fileNames) {
			if (fileName.startsWith(StringPool.SLASH)) {
				fileName = fileName.substring(1);
			}

			migrateFile(companyId, repositoryId, fileName, versionNumber);
		}
	}

	protected void migrateMB() throws Exception {
		int count = MBMessageLocalServiceUtil.getMBMessagesCount();

		MaintenanceUtil.appendStatus(
			"Migrating message boards attachments in " + count + " messages");

		int pages = count / Indexer.DEFAULT_INTERVAL;

		for (int i = 0; i <= pages; i++) {
			int start = (i * Indexer.DEFAULT_INTERVAL);
			int end = start + Indexer.DEFAULT_INTERVAL;

			List<MBMessage> messages = MBMessageLocalServiceUtil.getMBMessages(
				start, end);

			for (MBMessage message : messages) {
				migrateFiles(
					message.getCompanyId(), message.getAttachmentsDir(),
					message.getAttachmentsFiles());
			}
		}
	}

	protected void migratePortlets() throws Exception {
		migrateDL();
		migrateMB();
		migrateWiki();
	}

	protected void migrateWiki() throws Exception {
		int count = WikiPageLocalServiceUtil.getWikiPagesCount();

		MaintenanceUtil.appendStatus(
			"Migrating wiki page attachments in " + count + " pages");

		int pages = count / Indexer.DEFAULT_INTERVAL;

		for (int i = 0; i <= pages; i++) {
			int start = (i * Indexer.DEFAULT_INTERVAL);
			int end = start + Indexer.DEFAULT_INTERVAL;

			List<WikiPage> wikiPages = WikiPageLocalServiceUtil.getWikiPages(
				start, end);

			for (WikiPage wikiPage : wikiPages) {
				if (!wikiPage.isHead()) {
					continue;
				}

				migrateFiles(
					wikiPage.getCompanyId(), wikiPage.getAttachmentsDir(),
					wikiPage.getAttachmentsFiles());
			}
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		ConvertDocumentLibrary.class);

	private Store _sourceStore;
	private Store _targetStore;

}
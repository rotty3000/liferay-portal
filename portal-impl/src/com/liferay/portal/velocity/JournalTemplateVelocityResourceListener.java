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

package com.liferay.portal.velocity;

import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.journal.model.JournalTemplate;
import com.liferay.portlet.journal.service.JournalTemplateLocalServiceUtil;

import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * @author Alexander Chow
 */
public class JournalTemplateVelocityResourceListener
	extends VelocityResourceListener {

	@Override
	public InputStream getResourceStream(String source)
		throws ResourceNotFoundException {

		try {
			return doGetResourceStream(source);
		}
		catch (Exception e) {
			throw new ResourceNotFoundException(source);
		}
	}

	protected InputStream doGetResourceStream(String source) throws Exception {
		int pos = source.indexOf(_SOURCE_PREFIX);

		if (pos == -1) {
			return null;
		}

		int x = source.indexOf(CharPool.SLASH, pos);
		int y = source.indexOf(CharPool.SLASH, x + 1);
		int z = source.indexOf(CharPool.SLASH, y + 1);

		long companyId = GetterUtil.getLong(source.substring(x + 1, y));
		long groupId = GetterUtil.getLong(source.substring(y + 1, z));
		String templateId = source.substring(z + 1);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Loading {companyId=" + companyId + ", groupId=" + groupId +
					", templateId=" + templateId + "}");
		}

		JournalTemplate journalTemplate =
			JournalTemplateLocalServiceUtil.getTemplate(groupId, templateId);

		String xsl = journalTemplate.getXsl();

		return new UnsyncByteArrayInputStream(xsl.getBytes());
	}

	private static final String _SOURCE_PREFIX = JOURNAL_SEPARATOR.concat(
		StringPool.SLASH);

	private static Log _log = LogFactoryUtil.getLog(
		JournalTemplateVelocityResourceListener.class);

}
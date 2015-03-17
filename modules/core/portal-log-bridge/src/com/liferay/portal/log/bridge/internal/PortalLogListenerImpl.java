/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portal.log.bridge.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import org.eclipse.equinox.log.ExtendedLogEntry;
import org.eclipse.equinox.log.SynchronousLogListener;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;

import org.osgi.framework.Bundle;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;

/**
 * @author Raymond Augé
 * @author Kamesh Sampath
 */
public class PortalLogListenerImpl implements SynchronousLogListener {

	@Override
	public void logged(LogEntry logEntry) {
		if (!(logEntry instanceof ExtendedLogEntry)) {
			return;
		}

		ExtendedLogEntry extendedLogEntry = (ExtendedLogEntry)logEntry;

		Object context = extendedLogEntry.getContext();

		if (context instanceof FrameworkLogEntry) {
			_log((FrameworkLogEntry)context);
			return;
		}

		Bundle bundle = extendedLogEntry.getBundle();

		int bundleCode = (int)bundle.getBundleId();

		FrameworkLogEntry frameworkLogEntry = new FrameworkLogEntry(
				_logEntryTag(bundle), extendedLogEntry.getLevel(), bundleCode,
				extendedLogEntry.getMessage(), 0,
				extendedLogEntry.getException(), null);

		_log(frameworkLogEntry);
	}

	private synchronized void _log(FrameworkLogEntry logEntry) {
		//TODO need to add ThreadName and other FrameworkLogEntry attribs?

		String logName = logEntry.getEntry();

		Log log = LogFactoryUtil.getLog(logName);

		String message = logEntry.getMessage();

		int level = logEntry.getSeverity();

		Throwable throwable = logEntry.getThrowable();

		if ((level == LogService.LOG_DEBUG) && log.isDebugEnabled()) {
			if (throwable!= null) {
				log.debug(message, throwable);
			}
			else {
				log.debug(message);
			}
		}
		else if ((level == LogService.LOG_ERROR) && log.isErrorEnabled()) {
			if (throwable!= null) {
				log.error(message, throwable);
			}
			else {
				log.error(message);
			}
		}
		else if ((level == LogService.LOG_INFO) && log.isInfoEnabled()) {
			if (throwable!= null) {
				log.info(message, throwable);
			}
			else {
				log.info(message);
			}
		}
		else if ((level == LogService.LOG_WARNING) && log.isWarnEnabled()) {
			if (throwable!= null) {
				log.warn(message, throwable);
			}
			else {
				log.warn(message);
			}
		}

		// handle the log child entries

		FrameworkLogEntry[] children = logEntry.getChildren();

		if (children!= null) {
			for (FrameworkLogEntry frameworkLogEntry : children) {
				_log(frameworkLogEntry);
			}
		}
	}

	private String _logEntryTag(Bundle bundle) {
		StringBuilder logEntryTag = new StringBuilder("osgi.logging.");

		if (bundle!= null && (bundle.getSymbolicName() != null)) {
			String bsn = bundle.getSymbolicName();

			logEntryTag.append(
					StringUtil.replace(
						bsn, StringPool.PERIOD, StringPool.UNDERLINE));
		}
		else {
			logEntryTag.append("unknown");
		}

		return logEntryTag.toString();
	}

}
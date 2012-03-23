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

package com.liferay.portal.osgi;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;

/**
 * @author Raymond Augé
 */
public class FrameworkListener extends BaseListener
	implements org.osgi.framework.FrameworkListener {

	public void frameworkEvent(FrameworkEvent frameworkEvent) {
		try {
			int type = frameworkEvent.getType();

			Bundle bundle = frameworkEvent.getBundle();

			Log log = LogFactoryUtil.getLog(
				logFormatBSN(bundle.getSymbolicName()));

			if (type == FrameworkEvent.ERROR) {
				frameworkEventError(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.INFO) {
				frameworkEventInfo(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.PACKAGES_REFRESHED) {
				frameworkEventPackagesRefreshed(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.STARTED) {
				frameworkEventStarted(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.STARTLEVEL_CHANGED) {
				frameworkEventStartLevelChanged(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.STOPPED) {
				frameworkEventStopped(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.STOPPED_BOOTCLASSPATH_MODIFIED) {
				frameworkEventStoppedBootClasspathModified(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.STOPPED_UPDATE) {
				frameworkEventStoppedUpdate(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.WAIT_TIMEDOUT) {
				frameworkEventWaitTimedout(log, frameworkEvent);
			}
			else if (type == FrameworkEvent.WARNING) {
				frameworkEventWarning(log, frameworkEvent);
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	protected void frameworkEventError(Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isErrorEnabled()) {
			return;
		}

		log.error("[ERROR]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventInfo(Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[INFO]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventPackagesRefreshed(
			Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[PACKAGES_REFRESHED]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventStarted(Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[STARTED]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventStartLevelChanged(
			Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[STARTLEVEL_CHANGED]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventStopped(Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[STOPPED]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventStoppedBootClasspathModified(
			Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info(
			"[STOPPED_BOOTCLASSPATH_MODIFIED]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventStoppedUpdate(
			Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[STOPPED_UPDATE]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventWaitTimedout(
			Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isInfoEnabled()) {
			return;
		}

		log.info("[WAIT_TIMEDOUT]", frameworkEvent.getThrowable());
	}

	protected void frameworkEventWarning(Log log, FrameworkEvent frameworkEvent)
		throws Exception {

		if (!log.isWarnEnabled()) {
			return;
		}

		log.warn("[WARNING]", frameworkEvent.getThrowable());
	}

	private static Log _log = LogFactoryUtil.getLog(FrameworkListener.class);

}
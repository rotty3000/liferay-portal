/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.log.secure;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogUtil;
import com.liferay.portal.kernel.log.LogWrapper;

/**
 * @author Tomas Polesovsky
 */
public class SecureLogWrapper extends LogWrapper {

	public SecureLogWrapper(Log log) {
		super(log);
	}

	@Override
	public void debug(Object msg) {
		super.debug(LogUtil.sanitize(msg));
	}

	@Override
	public void debug(Object msg, Throwable t) {
		super.debug(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void debug(Throwable t) {
		super.debug(LogUtil.sanitize(t));
	}

	@Override
	public void error(Object msg) {
		super.error(LogUtil.sanitize(msg));
	}

	@Override
	public void error(Object msg, Throwable t) {
		super.error(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void error(Throwable t) {
		super.error(LogUtil.sanitize(t));
	}

	@Override
	public void fatal(Object msg) {
		super.fatal(LogUtil.sanitize(msg));
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		super.fatal(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void fatal(Throwable t) {
		super.fatal(LogUtil.sanitize(t));
	}

	@Override
	public void info(Object msg) {
		super.info(LogUtil.sanitize(msg));
	}

	@Override
	public void info(Object msg, Throwable t) {
		super.info(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void info(Throwable t) {
		super.info(LogUtil.sanitize(t));
	}

	public void insecureDebug(Object msg) {
		super.debug(msg);
	}

	public void insecureDebug(Object msg, Throwable t) {
		super.debug(msg, t);
	}

	public void insecureDebug(Throwable t) {
		super.debug(t);
	}

	public void insecureError(Object msg) {
		super.error(msg);
	}

	public void insecureError(Object msg, Throwable t) {
		super.error(msg, t);
	}

	public void insecureError(Throwable t) {
		super.error(t);
	}

	public void insecureFatal(Object msg) {
		super.fatal(msg);
	}

	public void insecureFatal(Object msg, Throwable t) {
		super.fatal(msg, t);
	}

	public void insecureFatal(Throwable t) {
		super.fatal(t);
	}

	public void insecureInfo(Object msg) {
		super.info(msg);
	}

	public void insecureInfo(Object msg, Throwable t) {
		super.info(msg, t);
	}

	public void insecureInfo(Throwable t) {
		super.info(t);
	}

	public void insecureTrace(Object msg) {
		super.trace(msg);
	}

	public void insecureTrace(Object msg, Throwable t) {
		super.trace(msg, t);
	}

	public void insecureTrace(Throwable t) {
		super.trace(t);
	}

	public void insecureWarn(Object msg) {
		super.warn(msg);
	}

	public void insecureWarn(Object msg, Throwable t) {
		super.warn(msg, t);
	}

	public void insecureWarn(Throwable t) {
		super.warn(t);
	}

	@Override
	public void trace(Object msg) {
		super.trace(LogUtil.sanitize(msg));
	}

	@Override
	public void trace(Object msg, Throwable t) {
		super.trace(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void trace(Throwable t) {
		super.trace(LogUtil.sanitize(t));
	}

	@Override
	public void warn(Object msg) {
		super.warn(LogUtil.sanitize(msg));
	}

	@Override
	public void warn(Object msg, Throwable t) {
		super.warn(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void warn(Throwable t) {
		super.warn(LogUtil.sanitize(t));
	}

}
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
		getWrappedLog().debug(LogUtil.sanitize(msg));
	}

	@Override
	public void debug(Object msg, Throwable t) {
		getWrappedLog().debug(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void debug(Throwable t) {
		getWrappedLog().debug(LogUtil.sanitize(t));
	}

	@Override
	public void error(Object msg) {
		getWrappedLog().error(LogUtil.sanitize(msg));
	}

	@Override
	public void error(Object msg, Throwable t) {
		getWrappedLog().error(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void error(Throwable t) {
		getWrappedLog().error(LogUtil.sanitize(t));
	}

	@Override
	public void fatal(Object msg) {
		getWrappedLog().fatal(LogUtil.sanitize(msg));
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		getWrappedLog().fatal(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void fatal(Throwable t) {
		getWrappedLog().fatal(LogUtil.sanitize(t));
	}

	@Override
	public void info(Object msg) {
		getWrappedLog().info(LogUtil.sanitize(msg));
	}

	@Override
	public void info(Object msg, Throwable t) {
		getWrappedLog().info(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void info(Throwable t) {
		getWrappedLog().info(LogUtil.sanitize(t));
	}

	@Override
	public void trace(Object msg) {
		getWrappedLog().trace(LogUtil.sanitize(msg));
	}

	@Override
	public void trace(Object msg, Throwable t) {
		getWrappedLog().trace(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void trace(Throwable t) {
		getWrappedLog().trace(LogUtil.sanitize(t));
	}

	@Override
	public void warn(Object msg) {
		getWrappedLog().warn(LogUtil.sanitize(msg));
	}

	@Override
	public void warn(Object msg, Throwable t) {
		getWrappedLog().warn(LogUtil.sanitize(msg), LogUtil.sanitize(t));
	}

	@Override
	public void warn(Throwable t) {
		getWrappedLog().warn(LogUtil.sanitize(t));
	}

}
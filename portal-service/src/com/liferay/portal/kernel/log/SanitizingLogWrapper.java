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

package com.liferay.portal.kernel.log;

import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class SanitizingLogWrapper extends LogWrapper {

	public SanitizingLogWrapper(Log log) {
		super(log);
	}

	@Override
	public void debug(Object msg) {
		getWrappedLog().debug(sanitize(msg));
	}

	@Override
	public void debug(Object msg, Throwable t) {
		getWrappedLog().debug(sanitize(msg), sanitize(t));
	}

	@Override
	public void debug(Throwable t) {
		getWrappedLog().debug(sanitize(t));
	}

	@Override
	public void error(Object msg) {
		getWrappedLog().error(sanitize(msg));
	}

	@Override
	public void error(Object msg, Throwable t) {
		getWrappedLog().error(sanitize(msg), sanitize(t));
	}

	@Override
	public void error(Throwable t) {
		getWrappedLog().error(sanitize(t));
	}

	@Override
	public void fatal(Object msg) {
		getWrappedLog().fatal(sanitize(msg));
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		getWrappedLog().fatal(sanitize(msg), sanitize(t));
	}

	@Override
	public void fatal(Throwable t) {
		getWrappedLog().fatal(sanitize(t));
	}

	@Override
	public void info(Object msg) {
		getWrappedLog().info(sanitize(msg));
	}

	@Override
	public void info(Object msg, Throwable t) {
		getWrappedLog().info(sanitize(msg), sanitize(t));
	}

	@Override
	public void info(Throwable t) {
		getWrappedLog().info(sanitize(t));
	}

	@Override
	public void trace(Object msg) {
		getWrappedLog().trace(sanitize(msg));
	}

	@Override
	public void trace(Object msg, Throwable t) {
		getWrappedLog().trace(sanitize(msg), sanitize(t));
	}

	@Override
	public void trace(Throwable t) {
		getWrappedLog().trace(sanitize(t));
	}

	@Override
	public void warn(Object msg) {
		getWrappedLog().warn(sanitize(msg));
	}

	@Override
	public void warn(Object msg, Throwable t) {
		getWrappedLog().warn(sanitize(msg), sanitize(t));
	}

	@Override
	public void warn(Throwable t) {
		getWrappedLog().warn(sanitize(t));
	}

	protected String sanitize(Object obj) {
		if (obj == null) {
			return null;
		}

		return sanitize(obj.toString(), false);
	}

	protected String sanitize(String message, boolean returnNull) {
		if (message == null) {
			return null;
		}

		boolean sanitized = false;
		char[] characters = message.toCharArray();

		for (int i = 0; i < characters.length; i++) {
			int codePoint = characters[i];

			if ((codePoint >= 0) && (codePoint < _logMessageWhitelist.length) &&
				(_logMessageWhitelist[codePoint] == 0)) {

				characters[i] = _LOG_SANITIZING_REPLACEMENT;
				sanitized = true;
			}
		}

		if (sanitized) {
			String result = new String(characters).concat(_SANITIZED);

			if (_LOG_SANITIZING_ESCAPE_HTML_ENABLED) {
				return HtmlUtil.escape(result);
			}
			else {
				return result;
			}
		}

		if (returnNull) {
			return null;
		}

		return message;
	}

	protected Throwable sanitize(Throwable throwable) {
		List<Throwable> throwableStack = new ArrayList<Throwable>();

		Throwable causeOnStack = throwable;

		while (causeOnStack != null) {
			throwableStack.add(causeOnStack);

			causeOnStack = causeOnStack.getCause();
		}

		Throwable cause = null;

		boolean sanitized = false;

		for (int i = throwableStack.size() - 1; i > - 1; i--) {
			Throwable t = throwableStack.get(i);
			String message = t.toString();

			String sanitizedMessage = sanitize(message, true);

			if (!sanitized && (sanitizedMessage == null)) {
				cause = t;
				continue;
			}

			if (sanitizedMessage == null) {
				sanitizedMessage = message;
			}

			sanitized = true;

			cause = new SanitizedException(
				sanitizedMessage, t.getStackTrace(), cause);
		}

		return cause;
	}

	protected static void init() {
		if (_initialized) {
			return;
		}

		_initialized = true;

		_LOG_SANITIZING_ESCAPE_HTML_ENABLED = GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.LOG_SANITIZING_ESCAPE_HTML_ENABLED));

		_LOG_SANITIZING_REPLACEMENT = (char)GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.LOG_SANITIZING_REPLACEMENT));

		int[] whitelist = GetterUtil.getIntegerValues(
			PropsUtil.getArray(PropsKeys.LOG_SANITIZING_WHITELIST));

		for (int codePoint : whitelist) {
			if ((codePoint >= 0) && (codePoint < _logMessageWhitelist.length)) {
				_logMessageWhitelist[codePoint] = 1;
			}
			else {
				System.err.println(
					"Unable to register log whitelisted character: " +
						codePoint);
			}
		}
	}

	private static final String _SANITIZED = " [Sanitized]";

	private static boolean _initialized = false;

	private static int[] _logMessageWhitelist = new int[128];

	private static boolean _LOG_SANITIZING_ESCAPE_HTML_ENABLED = false;

	private static char _LOG_SANITIZING_REPLACEMENT = CharPool.UNDERLINE;

	static {
		init();
	}

}
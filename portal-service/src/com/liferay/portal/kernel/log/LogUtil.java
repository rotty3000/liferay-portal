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

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.secure.SecureLogWrapper;
import com.liferay.portal.kernel.log.secure.TransparentException;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 */
public class LogUtil {

	public static final boolean REMOVE_UNKNOWN_SOURCE = true;

	public static final int STACK_TRACE_LENGTH = 20;

	public static void debug(Log log, Properties props) {
		if (log.isDebugEnabled()) {
			Properties sanitizedProps = new Properties();

			for (String key : props.stringPropertyNames()) {
				sanitizedProps.put(
					sanitize(key, false),
					sanitize(props.getProperty(key), false));
			}

			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter(
				sanitizedProps.size() + 1);

			sanitizedProps.list(
				UnsyncPrintWriterPool.borrow(unsyncStringWriter));

			if (log instanceof SecureLogWrapper) {
				SecureLogWrapper secureLogWrapper = (SecureLogWrapper)log;
				secureLogWrapper.insecureDebug(unsyncStringWriter.toString());
			}
			else {
				log.debug(unsyncStringWriter.toString());
			}
		}
	}

	public static void init() {
		_SECURE_LOGGING_ESCAPE_HTML_ENABLED = GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.SECURE_LOGGING_ESCAPE_HTML_ENABLED));

		_SECURE_LOGGING_SANITIZE_ENABLED = GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.SECURE_LOGGING_SANITIZE_ENABLED));

		_SECURE_LOGGING_SANITIZE_REPLACEMENT = (char)GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.SECURE_LOGGING_SANITIZE_REPLACEMENT));

		int[] whitelist = GetterUtil.getIntegerValues(
			PropsUtil.getArray(PropsKeys.SECURE_LOGGING_SANITIZE_WHITELIST));

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

	public static void log(Log log, JspException jspe) {
		Throwable cause = jspe.getCause();

		if (cause == null) {
			cause = jspe;
		}

		if ((cause != jspe) && (cause instanceof JspException)) {
			log(log, (JspException)cause);
		}
		else if (cause instanceof ServletException) {
			log(log, (ServletException)cause);
		}
		else {
			_log(log, cause);
		}
	}

	public static void log(Log log, ServletException se) {
		Throwable cause = se.getRootCause();

		if (cause == null) {
			cause = se;
		}

		if (cause instanceof JspException) {
			log(log, (JspException)cause);
		}
		else if ((cause != se) && (cause instanceof ServletException)) {
			log(log, (ServletException)cause);
		}
		else {
			_log(log, cause);
		}
	}

	public static void log(Log log, Throwable t) {
		if (t instanceof JspException) {
			log(log, (JspException)t);
		}
		else if (t instanceof ServletException) {
			log(log, (ServletException)t);
		}
		else {
			Throwable cause = t.getCause();

			if (cause != null) {
				log(log, cause);
			}
			else {
				_log(log, t);
			}
		}
	}

	public static String sanitize(Object obj) {
		String message = obj != null ? obj.toString() : null;
		return sanitize(message, false);
	}

	public static Throwable sanitize(Throwable exception) {
		if (!_SECURE_LOGGING_SANITIZE_ENABLED) {
			return exception;
		}

		List<Throwable> excStack = new ArrayList<Throwable>();

		Throwable e = exception;
		while (e != null) {
			excStack.add(e);
			e = e.getCause();
		}

		Throwable cause = null;

		boolean sanitized = false;

		for (int i = excStack.size() - 1; i > - 1; i--) {
			Throwable t = excStack.get(i);
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

			cause = new TransparentException(
				sanitizedMessage, t.getStackTrace(), cause);
		}

		return cause;
	}

	protected static String sanitize(String message, boolean returnNull) {
		if (!_SECURE_LOGGING_SANITIZE_ENABLED) {
			return message;
		}

		if (message == null) {
			return null;
		}

		boolean sanitized = false;
		char[] characters = message.toCharArray();

		for (int i = 0; i < characters.length; i++) {
			int codePoint = characters[i];

			if ((codePoint >= 0) && (codePoint < _logMessageWhitelist.length) &&
				(_logMessageWhitelist[codePoint] == 0)) {

				characters[i] = _SECURE_LOGGING_SANITIZE_REPLACEMENT;
				sanitized = true;
			}
		}

		if (sanitized) {
			String result = new String(characters).concat(_SANITIZED);

			if (_SECURE_LOGGING_ESCAPE_HTML_ENABLED) {
				return HtmlUtil.escape(result);
			}
			else {
				return result;
			}
		}

		return returnNull ? null : message;
	}

	private static void _log(Log log, Throwable cause) {
		StackTraceElement[] steArray = cause.getStackTrace();

		// Make the stack trace more readable by limiting the number of
		// elements.

		if (steArray.length <= STACK_TRACE_LENGTH) {
			log.error(StackTraceUtil.getStackTrace(cause));

			return;
		}

		int count = 0;

		List<StackTraceElement> steList = new ArrayList<StackTraceElement>();

		for (int i = 0; i < steArray.length; i++) {
			StackTraceElement ste = steArray[i];

			// Make the stack trace more readable by removing elements that
			// refer to classes with no packages, or starts with a $, or are
			// Spring classes, or are standard reflection classes.

			String className = ste.getClassName();

			boolean addElement = true;

			if (REMOVE_UNKNOWN_SOURCE && (ste.getLineNumber() < 0)) {
				addElement = false;
			}

			if (className.startsWith("$") ||
				className.startsWith("java.lang.reflect.") ||
				className.startsWith("org.springframework.") ||
				className.startsWith("sun.reflect.")) {

				addElement = false;
			}

			if (addElement) {
				steList.add(ste);

				count++;
			}

			if (count >= STACK_TRACE_LENGTH) {
				break;
			}
		}

		steArray = steList.toArray(new StackTraceElement[steList.size()]);

		cause.setStackTrace(steArray);

		log.error(StackTraceUtil.getStackTrace(cause));
	}

	private static final String _SANITIZED = " [Sanitized]";

	private static int[] _logMessageWhitelist = new int[128];

	private static boolean _SECURE_LOGGING_ESCAPE_HTML_ENABLED = false;

	private static boolean _SECURE_LOGGING_SANITIZE_ENABLED = false;

	private static char _SECURE_LOGGING_SANITIZE_REPLACEMENT =
		CharPool.UNDERLINE;

}
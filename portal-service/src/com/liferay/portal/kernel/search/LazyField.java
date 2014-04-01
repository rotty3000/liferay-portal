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

package com.liferay.portal.kernel.search;

import com.liferay.portal.kernel.executor.PortalExecutorManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author László Csontos
 */
public class LazyField extends Field {

	public LazyField(String name, Callable<String[]> valuesCallback) {
		super(name, (String[])null);

		_valuesCallback = valuesCallback;
	}

	public String[] getValues() {
		String[] values = _values.get();

		if (values != null) {
			return values;
		}

		Future<String[]> valuesFuture = PortalExecutorManagerUtil.execute(
			LazyField.class.getName(), _valuesCallback);

		Long startTime = null;

		if (_log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();

			_log.debug("Submitted task " + _valuesCallback);
		}

		try {
			values = valuesFuture.get();

			if (startTime != null) {
				StringBundler sb = new StringBundler(5);

				sb.append("Task ");
				sb.append(_valuesCallback);
				sb.append(" has been finished under ");

				long endTime = System.currentTimeMillis();

				sb.append(endTime - startTime);
				sb.append(" ms");

				_log.debug(sb.toString());
			}

			_values = new WeakReference<String[]>(values);
		}
		catch (Exception e) {
			_log.error(e);
		}

		return values;
	}

	private static Log _log = LogFactoryUtil.getLog(LazyField.class);

	private Reference<String[]> _values = new WeakReference<String[]>(null);
	private Callable<String[]> _valuesCallback;

}
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

package com.liferay.petra.io.nio.intraband;

import com.liferay.petra.io.convert.Conversions;
import com.liferay.petra.io.validate.Validator;
import com.liferay.petra.io.nio.intraband.blocking.ExecutorIntraband;
import com.liferay.petra.io.nio.intraband.nonblocking.SelectorIntraband;
import com.liferay.petra.io.nio.intraband.welder.Welder;
import com.liferay.petra.io.nio.intraband.welder.WelderFactoryUtil;
import com.liferay.petra.io.nio.intraband.welder.socket.SocketWelder;

import java.io.IOException;

import java.lang.reflect.Constructor;

/**
 * @author Shuyang Zhou
 */
public class IntrabandFactoryUtil {

	public static Intraband createIntraband() throws IOException {
		if (Validator.isNotNull(_INTRABAND_IMPL)) {
			try {
				Class<? extends Intraband> intrabandClass = Class.forName(
					_INTRABAND_IMPL).asSubclass(Intraband.class);

				Constructor<? extends Intraband> constructor =
					intrabandClass.getConstructor(long.class);

				return constructor.newInstance(_INTRABAND_TIMEOUT_DEFAULT);
			}
			catch (Exception e) {
				throw new RuntimeException(
					"Unable to instantiate " + _INTRABAND_IMPL, e);
			}
		}
		else {
			Class<? extends Welder> welderClass =
				WelderFactoryUtil.getWelderClass();

			if (welderClass.equals(SocketWelder.class)) {
				return new SelectorIntraband(_INTRABAND_TIMEOUT_DEFAULT);
			}
			else {
				return new ExecutorIntraband(_INTRABAND_TIMEOUT_DEFAULT);
			}
		}
	}

	private static final String _INTRABAND_IMPL = Conversions.getString(
		System.getProperty("intraband.impl"));

	private static final long _INTRABAND_TIMEOUT_DEFAULT = Conversions.getLong(
		System.getProperty("intraband.timeout.default"));

}
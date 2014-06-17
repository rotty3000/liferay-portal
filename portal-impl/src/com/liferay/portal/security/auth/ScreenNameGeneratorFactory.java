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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.util.PropsValues;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Peter Fellwock
 */
public class ScreenNameGeneratorFactory {

	public static ScreenNameGenerator getInstance() {
		if (_screenNameGenerator == null) {
			_originalScreenNameGenerator =
					ScreenNameGeneratorRegistryUtil.getScreenNameGenerator(
					PropsValues.USERS_SCREEN_NAME_GENERATOR);
			_screenNameGenerator = _originalScreenNameGenerator;
			}

		return _screenNameGenerator;
	}

	public static void setInstance(ScreenNameGenerator screenNameGenerator) {
		if (_log.isDebugEnabled()) {
			_log.debug("Set " + ClassUtil.getClassName(screenNameGenerator));
		}

		if (screenNameGenerator == null) {
			_screenNameGenerator = _originalScreenNameGenerator;
		}
		else {
			_screenNameGenerator = screenNameGenerator;
		}
	}

	public void afterPropertiesSet() throws Exception {
		String className = PropsValues.USERS_SCREEN_NAME_GENERATOR;

		if (_log.isDebugEnabled()) {
			_log.debug("Instantiate " + className);
		}

		_originalScreenNameGenerator =
				ScreenNameGeneratorRegistryUtil.getScreenNameGenerator(
					className);
		_screenNameGenerator = _originalScreenNameGenerator;
	}

	private static Log _log = LogFactoryUtil.getLog(
		ScreenNameGeneratorFactory.class);

	private static ScreenNameGenerator _originalScreenNameGenerator;
	private static volatile ScreenNameGenerator _screenNameGenerator;

}
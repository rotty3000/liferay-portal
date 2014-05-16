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

package com.liferay.portal.kernel.settings;

import com.liferay.portal.kernel.util.StringPool;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BaseSettings implements Settings {

	@Override
	public void reset() {
		for (String key : getKeys()) {
			reset(key);
		}
	}

	@Override
	public Settings setValues(Settings settings) {
		for (String key : settings.getKeys()) {
			String[] values = settings.getValues(key, StringPool.EMPTY_ARRAY);

			if (values.length == 1) {
				setValue(key, values[0]);
			}
			else {
				setValues(key, values);
			}
		}

		return this;
	}

}
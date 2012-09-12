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

package com.liferay.portlet.documentlibrary.store;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.store.StoreUtil;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Tomas Polesovsky
 *
 * @deprecated After 6.1 please use
 *             {@link com.liferay.portal.kernel.store.StoreFactory}
 */
@Deprecated
public class StoreFactory {

	public static void checkProperties() {
		_log.warn("checkProperties() method has been deprecated");
	}

	public static Store getInstance() {
		return StoreUtil.getDefaultStore();
	}

	public static void setInstance(Store store) {
		String defaultStoreId = StoreUtil.getDefaultStoreId();

		StoreUtil.setStore(defaultStoreId, new DeprecatedStoreWrapper(store));
	}

	private static Log _log = LogFactoryUtil.getLog(StoreFactory.class);

}
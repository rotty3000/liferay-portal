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

package com.liferay.portal.store;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.store.Store;
import com.liferay.portal.kernel.store.StoreFactory;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.security.pacl.PACLClassLoaderUtil;
import com.liferay.portal.spring.aop.MethodInterceptorInvocationHandler;
import com.liferay.portlet.documentlibrary.store.DBStore;
import com.liferay.portlet.documentlibrary.store.TempFileMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
public class StoreFactoryImpl implements StoreFactory {

	public Store createStore(
		String storeClassName, Properties initProperties)
		throws Exception {

		if(_log.isDebugEnabled()){
			_log.debug(
				"Creating store " + storeClassName + " initialized with " +
					initProperties);
		}

		ClassLoader classLoader = PACLClassLoaderUtil.getPortalClassLoader();

		Store store = (Store) InstanceFactory.newInstance(
			classLoader, storeClassName);

		if (initProperties == null) {
			initProperties = PropsUtil.getProperties(
				store.getInitPropertiesKey(), false);
		}

		store.init(initProperties);

		if (store instanceof DBStore) {
			DB db = DBFactoryUtil.getDB();

			String dbType = db.getType();

			if (dbType.equals(DB.TYPE_POSTGRESQL)) {
				MethodInterceptor transactionAdviceMethodInterceptor =
					(MethodInterceptor) PortalBeanLocatorUtil.locate(
						"transactionAdvice");

				MethodInterceptor tempFileMethodInterceptor =
					new TempFileMethodInterceptor();

				List<MethodInterceptor> methodInterceptors =
					Arrays.asList(
						transactionAdviceMethodInterceptor,
						tempFileMethodInterceptor);

				store = (Store) ProxyUtil.newProxyInstance(
					classLoader, new Class<?>[]{Store.class},
					new MethodInterceptorInvocationHandler(
						store, methodInterceptors));
			}
		}

		return store;
	}

	private static Log _log = LogFactoryUtil.getLog(StoreFactory.class);

}

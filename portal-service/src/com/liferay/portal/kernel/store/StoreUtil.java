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

package com.liferay.portal.kernel.store;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tomas Polesovsky
 */
public class StoreUtil {

	public static final String SYSTEM_STORE_ID = "SYSTEM_STORE";

	public static void destroy() {
		getInstance()._destroy();
	}

	public static String getDefaultStoreId(){
		return getInstance()._getDefaultStoreId();
	}

	public static StoreUtil getInstance() {
		PortalRuntimePermission.checkGetBeanProperty(StoreUtil.class);

		return _instance;
	}

	public static Store getDefaultStore() {
		return getStore(getDefaultStoreId());
	}

	public static Store getStore(String storeId) {
		if(Validator.isNull(storeId)){
			throw new IllegalArgumentException(
				"Parameter storeId cannot be null! Please use " +
					"getDefaultStoreId() to get the default one");
		}

		return getInstance()._getStore(storeId);
	}

	public static void init(Properties configuration) throws Exception {
		getInstance()._init(configuration);
	}

	public static Store removeStore(String storeId) {
		if(Validator.isNull(storeId)){
			throw new IllegalArgumentException(
				"Parameter storeId cannot be empty or null!");
		}

		if(storeId.equals(SYSTEM_STORE_ID)){
			throw new IllegalArgumentException(
				"System store cannot be removed");
		}


		return getInstance()._removeStore(storeId);
	}

	public static void setDefaultStoreId(String defaultStoreId) {
		if(Validator.isNull(defaultStoreId)){
			throw new IllegalArgumentException(
				"Parameter storeId cannot be null!");
		}

		getInstance()._setDefaultStoreId(defaultStoreId);
	}

	public static void setStore(String storeId, Store store) {
		if(Validator.isNull(storeId)){
			throw new IllegalArgumentException(
				"Parameter storeId cannot be null or empty!");
		}

		if(Validator.isNull(store)){
			throw new IllegalArgumentException(
				"Parameter store cannot be null! Please use removeStore() " +
					"method instead!");
		}

		if(storeId.equals(SYSTEM_STORE_ID)){
			throw new IllegalArgumentException(
				"System store cannot be overwritten! Please save store under " +
					"different storeId and make it the default one!");
		}

		getInstance()._setStore(storeId, store);
	}

	private void _checkProperties(Properties configuration) {
		String dlHookImpl = configuration.getProperty("dl.hook.impl");

		if (Validator.isNotNull(dlHookImpl)) {
			boolean found = false;

			for (String[] dlHookStoreParts : _DL_HOOK_STORES) {
				if (dlHookImpl.equals(dlHookStoreParts[0])) {
					configuration.setProperty(
						PropsKeys.DL_STORE_IMPL, dlHookStoreParts[1]);

					found = true;

					break;
				}
			}

			if (!found) {
				configuration.setProperty(PropsKeys.DL_STORE_IMPL, dlHookImpl);
			}

			if (_log.isWarnEnabled()) {
				StringBundler sb = new StringBundler(8);

				sb.append("Liferay is configured with the legacy ");
				sb.append("property \"dl.hook.impl=" + dlHookImpl + "\" ");
				sb.append("in portal-ext.properties. Please reconfigure ");
				sb.append("to use the new property \"");
				sb.append(PropsKeys.DL_STORE_IMPL + "\". Liferay will ");
				sb.append("attempt to temporarily set \"");
				sb.append(PropsKeys.DL_STORE_IMPL + "=");
				sb.append(configuration.getProperty(PropsKeys.DL_STORE_IMPL));
				sb.append("\".");

				_log.warn(sb.toString());
			}
		}
	}

	private void _destroy() {
		PortalRuntimePermission.checkDestroy(StoreUtil.class);

		for(String storeId : _stores.keySet()){
			Store store =_stores.get(storeId);

			if(_log.isDebugEnabled()){
				_log.debug("Destroying " + storeId + " " +
					store.getClass().getName());
			}

			try {
				store.destroy();
				_stores.remove(storeId);
			} catch (Exception e) {
				_log.warn(
					"Cannot destroy store " + storeId + " " +
						store.getClass().getName(), e);
			}
		}
	}

	private String _getDefaultStoreId(){
		PortalRuntimePermission.checkGetBeanProperty(
			StoreUtil.class, "defaultStoreId");

		String result = _defaultStoreId;

		if(result == null){
			result = SYSTEM_STORE_ID;
		}

		if(_log.isDebugEnabled()){
			_log.debug("Returning defaultStoreId: " + result);
		}

		return result;
	}

	private Store _getStore(String storeId) {
		PortalRuntimePermission.checkStore(storeId);

		Store store = _stores.get(storeId);

		if (store == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"There is no store configured with ID " +
						storeId);
			}

			return null;
		}

		if ((store != null) && _log.isDebugEnabled()) {
			Class<?> clazz = store.getClass();

			_log.debug(
				"Returning " + clazz.getName() + " for storeId " + storeId);
		}

		if(storeId.equals(SYSTEM_STORE_ID)){
			return new UnmodifiableStoreInstance(store);
		}

		return store;
	}

	private void _init(Properties configuration) throws Exception {
		PortalRuntimePermission.checkInit(StoreUtil.class);

		if(configuration == null){
			configuration = PropsUtil.getProperties();
		}

		_checkProperties(configuration);

		String systemStoreClass = configuration.getProperty(
			PropsKeys.DL_STORE_IMPL);

		Store systemStore = StoreFactoryUtil.createStore(
			systemStoreClass, configuration);

		_stores.put(SYSTEM_STORE_ID, systemStore);
	}

	private Store _removeStore(String storeId) {
		PortalRuntimePermission.checkStore(storeId);

		if(storeId.equals(getDefaultStoreId())){
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Removing default store instance " + storeId + ". " +
						"System store is now the new default store.");
			}

			setDefaultStoreId(SYSTEM_STORE_ID);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Removing store instance " + storeId);
		}

		return _stores.remove(storeId);
	}

	private void _setDefaultStoreId(String defaultStoreId) {
		PortalRuntimePermission.checkSetBeanProperty(
			StoreUtil.class, "defaultStoreId");

		if (_log.isDebugEnabled()) {
			_log.debug("Setting defaultStoreId to " + defaultStoreId);
		}

		_defaultStoreId = defaultStoreId;
	}

	private void _setStore(String storeId, Store store) {
		PortalRuntimePermission.checkStore(storeId);

		Store oldStore = _stores.get(storeId);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Setting store " + store.getClass().getName() + " as " +
					storeId);
		}

		_stores.put(storeId, store);

		if(oldStore != null){
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Destroying old store " + oldStore.getClass().getName());
			}

			try {
				oldStore.destroy();
			} catch (Exception e) {
				_log.warn(
					"Cannot destroy store " + oldStore.getClass().getName());
			}
		}
	}

	private static final StoreUtil _instance = new StoreUtil();

	private static final String[][] _DL_HOOK_STORES = new String[][] {
		new String[] {
			"com.liferay.documentlibrary.util.AdvancedFileSystemHook",
			"com.liferay.portlet.documentlibrary.store.AdvancedFileSystemStore"
		},
		new String[] {
			"com.liferay.documentlibrary.util.CMISHook",
			"com.liferay.portlet.documentlibrary.store.CMISStore"
		},
		new String[] {
			"com.liferay.documentlibrary.util.FileSystemHook",
			"com.liferay.portlet.documentlibrary.store.FileSystemStore"
		},
		new String[] {
			"com.liferay.documentlibrary.util.JCRHook",
			"com.liferay.portlet.documentlibrary.store.JCRStore"
		},
		new String[] {
			"com.liferay.documentlibrary.util.S3Hook",
			"com.liferay.portlet.documentlibrary.store.S3Store"
		}
	};

	private static Log _log = LogFactoryUtil.getLog(StoreUtil.class);

	private volatile String _defaultStoreId;
	private Map<String, Store> _stores =
		new ConcurrentHashMap<String, Store>();

}

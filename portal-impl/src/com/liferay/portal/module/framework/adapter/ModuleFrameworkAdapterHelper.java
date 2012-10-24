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

package com.liferay.portal.module.framework.adapter;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.module.framework.classloader.ModuleFrameworkClassloader;
import com.liferay.portal.util.PropsValues;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Miguel Pastor
 */
public class ModuleFrameworkAdapterHelper {

	public ModuleFrameworkAdapterHelper(String classname) {
		try {
			_adaptedInstance = InstanceFactory.newInstance(
				_classloader, classname);
		}
		catch (Exception e) {
			_log.error(
				"Unexpected error has ocurred while loading module framework" + 
					". It will be unusable");

			// not able to recover on this kind of fail

			throw new RuntimeException(e);
		}
	}

	public Object exec(
		String methodName, Class<?>[] parameterTypes, Object...parameters) {

		try {
			Method method = _searchMethod(methodName, parameterTypes);

			return method.invoke(_adaptedInstance, parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Unexpected in module framework");
	}

	public Object execute(String methodName, Object...parameters) {

		Class<?>[] parameterTypes = ReflectionUtil.getParameterTypes(
			parameters);

		return exec(methodName, parameterTypes, parameters);
	}

	public ClassLoader getClassLoader() {
		return _classloader;
	}

	private static ModuleFrameworkClassloader _getModuleFrameworkClassLoader() {
		try {

			File coreDir = new File(PropsValues.MODULE_FRAMEWORK_CORE_DIR);

			File[] files = coreDir.listFiles();

			URL[] classpath = new URL[files.length];

			for (int i = 0; i < classpath.length; i++) {
				classpath[i] = new URL("file://" + files[i].getAbsolutePath());
			}

			return new ModuleFrameworkClassloader(
				classpath, Thread.currentThread().getContextClassLoader());
		}
		catch (Exception e) {
			_log.error(
				"Unexpected error while configuring the custom classloader " + 
					"for the module framework. It will be unusable!");

			throw new RuntimeException(e);
		}
	}

	private Method _searchMethod(String methodName, Class<?>[] parameterTypes)
		throws Exception {

		MethodKey methodKey = new MethodKey(
			_adaptedInstance.getClass().getName(), methodName, parameterTypes);

		if (_methodsCache.containsKey(methodKey)) {
			return _methodsCache.get(methodKey);
		}

		Method method = ReflectionUtil.getDeclaredMethod(
			_adaptedInstance.getClass(), methodName, parameterTypes);

		_methodsCache.put(methodKey, method);

		return method;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ModuleFrameworkAdapterHelper.class);

	private static ModuleFrameworkClassloader _classloader =
			_getModuleFrameworkClassLoader();

	private static Map<MethodKey, Method> _methodsCache =
			new HashMap<MethodKey, Method>();

	private Object _adaptedInstance;

}
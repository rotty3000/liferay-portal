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

package com.liferay.portal.deploy.hot.module;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;

/**
 * @author Miguel Pastor
 */
public class ModuleHotDeployThreadLocal {

	public static Boolean isModuleDeploymentInProgress(){
		return _isModuleDeploymentInProgress.get();
	}

	public static void startModuleDeployment() {
		_isModuleDeploymentInProgress.set(true);
	}

	public static void stopModuleDeployment() {
		_isModuleDeploymentInProgress.set(false);
	}

	private static ThreadLocal<Boolean> _isModuleDeploymentInProgress =
		new AutoResetThreadLocal<Boolean>(
			ModuleHotDeployAdvice.class + ".isModuleDeploymentInProgress",
			false);

}
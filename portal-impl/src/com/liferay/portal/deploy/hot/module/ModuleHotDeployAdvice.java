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

import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Miguel Pastor
 */
public class ModuleHotDeployAdvice {

	public void manageDependenciesByModuleFramework(
			ProceedingJoinPoint proceedingJoinPoint,
			HotDeployEvent hotDeployEvent)
		throws Throwable {

		if (!ModuleHotDeployThreadLocal.isModuleDeploymentInProgress() ) {
			proceedingJoinPoint.proceed();

			return;
		}

		// the deployment has been started in the module framework

		HotDeployEvent managedHotDeployEvent = new HotDeployEvent(
			hotDeployEvent.getServletContext(),
			hotDeployEvent.getContextClassLoader(), false);

		proceedingJoinPoint.proceed(new Object[] {managedHotDeployEvent});
	}

}
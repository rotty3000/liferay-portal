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

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalService;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Tomas Polesovsky
 */
public abstract class LiveGroupStagingAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (!StagingAdvicesThreadLocal.isEnabled()) {
			return methodInvocation.proceed();
		}

		Method method = methodInvocation.getMethod();

		String methodName = method.getName();

		Object[] arguments = methodInvocation.getArguments();

		replaceStagingGroupIds(methodName, arguments);

		return methodInvocation.proceed();
	}

	public abstract void replaceStagingGroupIds(
		String methodName, Object[] arguments);

	protected void replaceGroupId(Object[] arguments, int index) {
		Object object = arguments[index];

		if (object == null) {
			return;
		}

		if (object instanceof Long[]) {
			Long[] groupIds = (Long[])object;

			for (int i = 0; i < groupIds.length; i++) {
				replaceGroupId(groupIds, i);
			}

			return;
		}

		long groupId = (Long)arguments[index];

		Group group = groupLocalService.fetchGroup(groupId);

		if ((group != null) && group.isStagingGroup() &&
			!group.isStagedRemotely()) {

			groupId = group.getLiveGroupId();
		}

		arguments[index] = groupId;
	}

	@BeanReference(type = GroupLocalService.class)
	protected GroupLocalService groupLocalService;

}
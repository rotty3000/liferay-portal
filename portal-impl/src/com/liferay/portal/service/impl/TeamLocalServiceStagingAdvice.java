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
public class TeamLocalServiceStagingAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (!StagingAdvicesThreadLocal.isEnabled()) {
			return methodInvocation.proceed();
		}

		Method method = methodInvocation.getMethod();

		String methodName = method.getName();

		Object[] arguments = methodInvocation.getArguments();

		if (methodName.equals("addTeam") && (arguments.length > 1)) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("deleteTeams")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("fetchTeamByUuidAndGroupId")) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("getGroupTeams")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getTeam")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("getTeamByUuidAndGroupId")) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("getUserTeams") && (arguments.length == 2)) {
			replaceGroupId(arguments, 1);
		}
		else if (methodName.equals("search")) {
			replaceGroupId(arguments, 0);
		}
		else if (methodName.equals("searchCount")) {
			replaceGroupId(arguments, 0);
		}

		return methodInvocation.proceed();
	}

	protected void replaceGroupId(Object[] arguments, int index) {
		long groupId = (Long)arguments[index];

		Group group = groupLocalService.fetchGroup(groupId);

		if ((group != null) && group.isStagingGroup()) {
			groupId = group.getLiveGroupId();
		}

		arguments[index] = groupId;
	}

	@BeanReference(type = GroupLocalService.class)
	protected GroupLocalService groupLocalService;

}
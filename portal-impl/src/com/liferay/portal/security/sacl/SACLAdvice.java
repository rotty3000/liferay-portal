/*
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.sacl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.RemoteAccessTypeThreadLocal;
import com.liferay.portal.security.RemoteMethodAccessType;
import com.liferay.portal.security.MethodSecurity;
import com.liferay.portal.spring.aop.AnnotationChainableMethodAdvice;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 *
 *
 * @author Tomas Polesovsky
 * @author Igor Spasic
 * @author Michael C. Han
 */
public class SACLAdvice extends
	AnnotationChainableMethodAdvice<MethodSecurity> {

	@Override
	public Object before(MethodInvocation methodInvocation) throws Throwable {

		boolean remoteAccess = RemoteAccessTypeThreadLocal.isRemoteAccess();

		MethodSecurity methodSecurity = findAnnotation(methodInvocation);

		if (remoteAccess) {
			Method targetMethod = methodInvocation.getMethod();

			_serviceAccessControlManager.accept(targetMethod, methodSecurity);
		}

		return null;
	}

	public void setServiceAccessControlManager(
		ServiceAccessControlManagerImpl serviceAccessControlManager) {

		_serviceAccessControlManager = serviceAccessControlManager;
	}

	@Override
	public MethodSecurity getNullAnnotation() {
		return _nullMethodSecurity;
	}

	private static Log _log = LogFactoryUtil.getLog(SACLAdvice.class);

	private static MethodSecurity _nullMethodSecurity =
		new MethodSecurity() {
			public RemoteMethodAccessType remoteMethodAccessType() {
				return RemoteMethodAccessType.ANONYMOUS;
			}

			public Class<? extends Annotation> annotationType() {
				return MethodSecurity.class;
			}
		};

	private ServiceAccessControlManagerImpl _serviceAccessControlManager;

}

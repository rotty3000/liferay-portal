/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.jsonwebservice;

import com.liferay.portal.kernel.annotation.AnnotationLocator;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionsManagerUtil;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMappingResolver;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.context.PortalContextLoaderListener;
import com.liferay.portal.util.PropsValues;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Igor Spasic
 */
public class JSONWebServiceRegitrator {

	public void processBean(BeanLocator beanLocator, String beanName) {
		if (!PropsValues.JSON_WEB_SERVICE_ENABLED ||
			!beanName.endsWith("Service")) {

			return;
		}

		Object bean;

		try {
			bean = beanLocator.locate(beanName);
		}
		catch (BeanLocatorException e) {
			return;
		}

		JSONWebService jsonWebService = AnnotationLocator.locate(
			bean.getClass(), JSONWebService.class);

		if (jsonWebService != null) {
			try {
				onJSONWebServiceBean(bean, jsonWebService);
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}
	}

	protected Class<?> loadUtilClass(Class<?> implementationClass)
		throws ClassNotFoundException {

		Class<?> utilClass = _utilClasses.get(implementationClass);

		if (utilClass != null) {
			return utilClass;
		}

		String implementationClassName = implementationClass.getName();

		if (implementationClassName.endsWith("Impl")) {
			implementationClassName = implementationClassName.substring(
				0, implementationClassName.length() - 4);
		}

		String utilClassName = implementationClassName + "Util";

		utilClassName = StringUtil.replace(
			utilClassName, ".impl.", StringPool.PERIOD);

		ClassLoader classLoader = implementationClass.getClassLoader();

		utilClass = classLoader.loadClass(utilClassName);

		_utilClasses.put(implementationClass, utilClass);

		return utilClass;
	}

	protected void onJSONWebServiceBean(
			Object serviceBean, JSONWebService jsonWebService)
		throws Exception {

		JSONWebServiceMode jsonWebServiceMode = JSONWebServiceMode.MANUAL;

		if (jsonWebService != null) {
			jsonWebServiceMode = jsonWebService.mode();
		}

		Class<?> serviceBeanClass = getTargetClass(serviceBean);

		Method[] methods = serviceBeanClass.getMethods();

		for (Method method : methods) {
			Class<?> declaringClass = method.getDeclaringClass();

			if (declaringClass != serviceBeanClass) {
				continue;
			}

			if ((_excludedMethodNames != null) &&
				_excludedMethodNames.contains(method.getName())) {

				continue;
			}

			JSONWebService methodJSONWebService = method.getAnnotation(
				JSONWebService.class);

			if (jsonWebServiceMode.equals(JSONWebServiceMode.AUTO)) {
				if (methodJSONWebService == null) {
					registerJSONWebServiceAction(serviceBean, serviceBeanClass, method);
				}
				else {
					JSONWebServiceMode methodJSONWebServiceMode =
						methodJSONWebService.mode();

					if (!methodJSONWebServiceMode.equals(
							JSONWebServiceMode.IGNORE)) {

						registerJSONWebServiceAction(serviceBean, serviceBeanClass, method);
					}
				}
			}
			else if (methodJSONWebService != null) {
				JSONWebServiceMode methodJSONWebServiceMode =
					methodJSONWebService.mode();

				if (!methodJSONWebServiceMode.equals(
						JSONWebServiceMode.IGNORE)) {

					registerJSONWebServiceAction(serviceBean, serviceBeanClass, method);
				}
			}
		}
	}

	// this has to be fixed for plugins
	// drop AopUtils usage and find field 'h'
	protected Class getTargetClass(Object proxy) throws Exception {
		if ((AopUtils.isJdkDynamicProxy(proxy))) {
			TargetSource targetSource = ((Advised)proxy).getTargetSource();

			return getTargetClass(targetSource.getTarget());
		}
		return proxy.getClass();
	}

	boolean wireViaUtil = false;

	protected void registerJSONWebServiceAction(
			Object serviceBean, Class serviceBeanClass, Method method)
		throws Exception {

		String httpMethod = _jsonWebServiceMappingResolver.resolveHttpMethod(
			method);

		if (_invalidHttpMethods.contains(httpMethod)) {
			return;
		}

		String servletContextName =
			PortletClassLoaderUtil.getServletContextName();

		if (servletContextName != null) {
			ServletContext servletContext = ServletContextPool.get(
				servletContextName);

			servletContextName = servletContext.getContextPath();
		}
		else {
			servletContextName =
				PortalContextLoaderListener.getPortalServletContextPath();

			if (servletContextName.equals(StringPool.SLASH)) {
				servletContextName = StringPool.BLANK;
			}
		}

		if (wireViaUtil == true) {
			Class<?> utilClass = loadUtilClass(serviceBeanClass);

			try {
				method = utilClass.getMethod(
					method.getName(), method.getParameterTypes());
			}
			catch (NoSuchMethodException nsme) {
				return;
			}

			String path = _jsonWebServiceMappingResolver.resolvePath(
				serviceBeanClass, method);

			JSONWebServiceActionsManagerUtil.registerJSONWebServiceAction(
				servletContextName, method.getDeclaringClass(), method, path,
				httpMethod);
		}
		else {
			String path = _jsonWebServiceMappingResolver.resolvePath(
				serviceBeanClass, method);

			JSONWebServiceActionsManagerUtil.registerJSONWebServiceAction(
				servletContextName, serviceBean, serviceBeanClass, method, path,
				httpMethod);

		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		JSONWebServiceRegitrator.class);

	private static Set<String> _excludedMethodNames = SetUtil.fromArray(
		new String[] {"getBeanIdentifier", "setBeanIdentifier"});

	private Set<String> _invalidHttpMethods = SetUtil.fromArray(
		PropsUtil.getArray(PropsKeys.JSONWS_WEB_SERVICE_INVALID_HTTP_METHODS));
	private JSONWebServiceMappingResolver _jsonWebServiceMappingResolver =
		new JSONWebServiceMappingResolver();
	private Map<Class<?>, Class<?>> _utilClasses =
		new HashMap<Class<?>, Class<?>>();

}
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

package com.liferay.portal.module.framework;

import com.liferay.arkadiko.sr.ServiceRegistry;
import com.liferay.portal.service.registry.Filter;
import com.liferay.portal.service.registry.ServiceRegistryUtil;
import com.liferay.portal.service.registry.ServiceTrackerInvocationHandler;

import java.lang.reflect.Proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class AKServiceRegistry implements ServiceRegistry {

	public Object registerBeanAsService(
			Object bean, String beanName, Class<?>[] interfaces,
			boolean trackService)
		throws Exception {

		List<String> names = new ArrayList<String>();

		for (Class<?> interfaceClass : interfaces) {
			names.add(interfaceClass.getName());
		}

		if (bean != null) {
			Map<String,Object> map = new HashMap<String, Object>();

			map.put("bean.id", beanName);
			map.put("original.bean", Boolean.TRUE);

			ServiceRegistryUtil.registerService(
				names.toArray(new String[names.size()]), bean, map);
		}

		if (!trackService) {
			return bean;
		}

		Filter filter = createFilter(beanName, interfaces);

		ServiceTrackerInvocationHandler serviceTrackerInvocationHandler =
			new ServiceTrackerInvocationHandler(filter, bean);

		return Proxy.newProxyInstance(
			getClass().getClassLoader(), interfaces,
			serviceTrackerInvocationHandler);
	}

	protected Filter createFilter(String beanName, Class<?>[] interfaces)
		throws Exception {

		StringBuffer sb = new StringBuffer((interfaces.length * 5) + 10);

		sb.append("(&");

		if (interfaces.length > 1) {
			sb.append("(|");
		}

		for (Class<?> clazz : interfaces) {
			sb.append("(objectClass=");
			sb.append(clazz.getName());
			sb.append(")");
		}

		if (interfaces.length > 1) {
			sb.append(")");
		}

		sb.append("(bean.id=");
		sb.append(beanName);
		sb.append(")(!(original.bean=*)))");

		return ServiceRegistryUtil.getFilter(sb.toString());
	}

}
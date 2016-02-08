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

package com.liferay.portal.servlet.context.helper.internal;

import com.liferay.portal.servlet.context.helper.ServletContextHelperFactory;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.context.ServletContextHelper;

/**
 * @author Juan Gonzalez
 */
@Component(immediate = true)
public class ServletContextHelperFactoryImpl
	implements ServletContextHelperFactory {

	@Override
	public ServletContextHelper createServletContextHelper(
		Bundle bundle, ServletContextHelperFactory.TYPE type) {

		switch (type) {
			case WAB:
				return new WabServletContextHelper(bundle);
			case PORTLET:
				return new BundlePortletServletContextHelper(bundle);
		}

		return null;
	}

}
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
package com.liferay.portlet.bundle.invokerfiltercontainerimpl;

import com.liferay.portal.model.PortletApp;
import com.liferay.portal.model.PortletFilter;

import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Philip Jones
 */
@Component(
		immediate = true,
		property = {
			"layout.type=testPortletFilter",
			"service.ranking:Integer=" + Integer.MAX_VALUE
		}
)
public class TestPortletFilter implements PortletFilter {

	@Override
	public String getFilterClass() {
		return null;
	}

	@Override
	public String getFilterName() {
		return null;
	}

	@Override
	public Map<String, String> getInitParams() {
		return null;
	}

	@Override
	public Set<String> getLifecycles() {
		return null;
	}

	@Override
	public PortletApp getPortletApp() {
		return null;
	}

	@Override
	public void setFilterClass(String filterClass) {
		return;
	}

	@Override
	public void setFilterName(String filterName) {
		return;
	}

	@Override
	public void setInitParams(Map<String, String> initParams) {
		return;
	}

	@Override
	public void setLifecycles(Set<String> lifecycles) {
		return;
	}

	@Override
	public void setPortletApp(PortletApp portletApp) {
		return;
	}

}
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

package com.liferay.site.map.web.portlet.action;

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.site.map.web.configuration.SiteMapConfiguration;

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Peter Fellwock
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=com_liferay_site_map_web_portlet_SiteMapPortlet"
	},
	service = ConfigurationAction.class
)
public class SiteMapConfigurationAction extends DefaultConfigurationAction {

	@Override
	public String render(
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws Exception {

		renderRequest.setAttribute(
			SiteMapConfiguration.class.getName(), _siteMapConfiguration);

		return super.render(portletConfig, renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_siteMapConfiguration = Configurable.createConfigurable(
			SiteMapConfiguration.class, properties);
	}

	private volatile SiteMapConfiguration _siteMapConfiguration;

}
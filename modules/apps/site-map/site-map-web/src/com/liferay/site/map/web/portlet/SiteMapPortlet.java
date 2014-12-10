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

package com.liferay.site.map.web.portlet;

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.site.map.web.configuration.SiteMapConfiguration;
import com.liferay.site.map.web.upgrade.SiteMapUpgrade;

import java.io.IOException;

import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 * @author Peter Fellwock
 */
@Component(
	configurationPid = "com.liferay.site.map.web",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-site-map",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.icon=/icons/site_map.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.remoteable=true",
		"com.liferay.portlet.struts-path=site_map",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Site Map",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user"
	},
	service = Portlet.class
)
public class SiteMapPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			SiteMapConfiguration.class.getName(), _siteMapConfiguration);

		super.doView(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_siteMapConfiguration = Configurable.createConfigurable(
			SiteMapConfiguration.class, properties);
	}

	@Reference(unbind = "-")
	protected void setSiteMapWebUpgrade(SiteMapUpgrade siteMapUpgrade) {
	}

	private volatile SiteMapConfiguration _siteMapConfiguration;

}
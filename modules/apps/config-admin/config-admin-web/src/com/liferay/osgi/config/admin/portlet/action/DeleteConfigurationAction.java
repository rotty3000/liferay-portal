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

package com.liferay.osgi.config.admin.portlet.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.ActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.MetaTypeService;

/**
 * @author Kamesh Sampath
 */

@Component(
	immediate = true, service = ActionCommand.class,
	property = {
		"action.command.name=deleteConfiguration",
		"javax.portlet.name=com_liferay_osgi_config_admin_portlet_" +
		"ConfigAdminPortlet"
	}
)
public class DeleteConfigurationAction implements ActionCommand {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public boolean processCommand(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws PortletException {

		String servicePID = ParamUtil.getString(
			portletRequest, "servicePID");

		if(_log.isDebugEnabled()){
			_log.debug("Deleting configuration for service:"+servicePID);
		}

		deleteConfiguration(servicePID);

		return true;
	}

	protected void deleteConfiguration(String servicePID)
					throws PortletException {

		try {

			Configuration configuration =
							_configurationAdmin.getConfiguration(
								servicePID,null);
			configuration.delete();

			//TODO Handle Factory Deletes
		}
		catch (IOException e) {
			_log.error(e);
			throw new PortletException(e);
		}
	}

	@Reference
	protected void setConfigAdminService(
		ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	@Reference
	protected void setMetaTypeService(MetaTypeService metaTypeService) {
		_metaTypeService = metaTypeService;
	}

	private String _extractBundleLocation(String targetPID){

		return StringUtil.extractLast(targetPID, StringPool.PIPE);
	}

	private static Log _log = LogFactoryUtil.getLog(DeleteConfigurationAction.class);

	protected BundleContext _bundleContext;
	protected ConfigurationAdmin _configurationAdmin;
	protected MetaTypeService _metaTypeService;


}
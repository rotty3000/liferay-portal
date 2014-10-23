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

package com.liferay.osgi.config.admin.portlet;

import com.liferay.osgi.config.admin.util.ConfigurableService;
import com.liferay.osgi.config.admin.util.ConfigurationFormBuilder;
import com.liferay.osgi.config.admin.util.ConfigurationsHelper;
import com.liferay.osgi.config.admin.util.ObjectClassDefinitionsHelper;
import com.liferay.osgi.config.admin.util.ObjectClassDefinitionsIterator;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.PortletApp;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.util.bridges.freemarker.FreeMarkerPortlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.net.URL;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.control-panel-entry-category=configuration",
		"com.liferay.portlet.control-panel-entry-weight=11",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.ftl",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class ConfigAdminPortlet extends FreeMarkerPortlet {

	@Activate
	public void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	public void deactivate() {
		_bundleContext = null;
	}

	@Override
	public void destroy() {
		PortletContext portletContext = getPortletContext();

		ServletContextPool.remove(portletContext.getPortletContextName());

		super.destroy();
	}

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		try {
			ConfigurationsHelper configurationsHelper =
							new ConfigurationsHelper(
								_bundleContext, languageId);

			String pidFilter = renderRequest.getParameter("pidFilter");

			List<ConfigurableService> services =
								configurationsHelper.getConfigurableSevices(
									themeDisplay.getLanguageId(), pidFilter);

			renderRequest.setAttribute(
				"ocdIterator", new ObjectClassDefinitionsIterator(services));

			super.doView(renderRequest, renderResponse);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	@Override
	public void init(PortletConfig portletConfig) throws PortletException {
		super.init(portletConfig);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletConfig;

		com.liferay.portal.model.Portlet portlet =
			liferayPortletConfig.getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		ServletContextPool.put(
			portletApp.getServletContextName(), portletApp.getServletContext());
	}

	protected URL getResourceURL(String path) {
		if (path.indexOf(StringPool.SLASH) != 0) {
			path = StringPool.SLASH.concat(path);
		}

		Bundle bundle = _bundleContext.getBundle();

		return bundle.getEntry("META-INF/resources".concat(path));
	}

	@Override
	protected void include(
			String path, RenderRequest renderRequest,
			RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String languageId = themeDisplay.getLanguageId();

		String viewType = renderRequest.getParameter("viewType");

		String pid = ParamUtil.getString(renderRequest, "servicePID");
		String factoryPid = ParamUtil.getString(renderRequest, "factoryPID");

		if (Validator.isNull(pid) && Validator.isNotNull(factoryPid)) {
			renderRequest.setAttribute("servicePID", StringPool.BLANK);
			renderRequest.setAttribute("factoryPID", factoryPid);
		}
		else if (Validator.isNotNull(pid) && Validator.isNotNull(factoryPid)) {
			renderRequest.setAttribute("servicePID", pid);
			renderRequest.setAttribute("factoryPID", factoryPid);
		}
		else {
			renderRequest.setAttribute("servicePID", pid);
			renderRequest.setAttribute("factoryPID", StringPool.BLANK);
		}

		renderRequest.setAttribute("randomNamespace", StringUtil.randomId());

		if ("/edit_attributes.ftl".equals(path)) {
			ObjectClassDefinitionsHelper ocdHelper =
							new ObjectClassDefinitionsHelper(
								_bundleContext, languageId);

			ObjectClassDefinition objectClassDefinition = null;

			//Instances will not have OCD, they need to inherit parents
			if ((Validator.isNull(pid) ||  Validator.isNotNull(pid))
					&& Validator.isNotNull(factoryPid)) {

				objectClassDefinition = ocdHelper.getObjectClassDefinition(
								factoryPid);
			}
			else {
				objectClassDefinition = ocdHelper.getObjectClassDefinition(pid);
			}

			if (objectClassDefinition!= null) {
				renderRequest.setAttribute(
					"editingHeaderTitle", objectClassDefinition.getName());

				renderRequest.setAttribute(
					"configurationFormBuilder", new ConfigurationFormBuilder(
						objectClassDefinition, _configurationAdmin));
			}
		}
		else if ("factoryInstances".equals(viewType)) {
			List<ConfigurableService> services = _factoryInstances(
							languageId, factoryPid);

			renderRequest.setAttribute(
				"ocdIterator", new ObjectClassDefinitionsIterator(services));
		}

		include(
			path, renderRequest, renderResponse, PortletRequest.RENDER_PHASE);
	}

	@Reference
	protected void setConfigAdminService(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	private List<ConfigurableService> _factoryInstances(
			String languageId, String factoryPid)
		throws IOException, PortletException {

		List<ConfigurableService> configServices =
						new ArrayList<ConfigurableService>();

		ConfigurationsHelper configurationsHelper = new ConfigurationsHelper(
						_bundleContext, languageId);

		ObjectClassDefinitionsHelper ocdHelper =
						new ObjectClassDefinitionsHelper(
							_bundleContext, languageId);

		ObjectClassDefinition objectClassDefinition =
						ocdHelper.getObjectClassDefinition(factoryPid);

		StringBuilder filter = new StringBuilder();

		filter.append(StringPool.OPEN_PARENTHESIS);
		filter.append(ConfigurationAdmin.SERVICE_FACTORYPID);
		filter.append(StringPool.EQUAL);
		filter.append(factoryPid);
		filter.append(StringPool.CLOSE_PARENTHESIS);

		String name = objectClassDefinition!= null?
			objectClassDefinition.getName():StringPool.BLANK;

		try {
			Configuration[] configs =
							configurationsHelper.getConfigurations(
				filter.toString());

			if (configs!= null) {
				for (Configuration configuration : configs) {
					String instancePid = configuration.getPid();

					String bundleLocation = configuration.getBundleLocation();

					ConfigurableService configurableService =
									new ConfigurableService(
										false, factoryPid, name, instancePid);
					configurableService.setBundleLocation(bundleLocation);

					configServices.add(configurableService);
				}
			}
		}
		catch (InvalidSyntaxException e) {
			throw new PortletException(e);
		}

		return configServices;
	}

	private static Log _log = LogFactoryUtil.getLog(ConfigAdminPortlet.class);

	private BundleContext _bundleContext;
	private ConfigurationAdmin _configurationAdmin;

}
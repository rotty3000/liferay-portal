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

package com.liferay.portal.template.velocity;

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.template.BaseTemplateManager;
import com.liferay.portal.template.RestrictedTemplate;
import com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration;
import com.liferay.taglib.servlet.PipingServletResponse;
import com.liferay.taglib.util.VelocityTaglib;
import com.liferay.taglib.util.VelocityTaglibImpl;

import java.io.IOException;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.introspection.SecureUberspector;

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
		configurationPid = "com.liferay.portal.template.velocity",
		configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
		service = TemplateManager.class
)
public class VelocityManager extends BaseTemplateManager {

	@Override
	public void addTaglibRequest(
		Map<String, Object> contextObjects, String applicationName,
		HttpServletRequest request, HttpServletResponse response) {

		contextObjects.put(
			applicationName, getVelocityTaglib(request, response));
	}

	@Override
	public void addTaglibRequest(
		Template template, String applicationName, HttpServletRequest request,
		HttpServletResponse response) {

		template.put(applicationName, getVelocityTaglib(request, response));
	}

	@Override
	public void destroy() {
		if (_velocityEngine == null) {
			return;
		}

		_velocityEngine = null;

		templateContextHelper.removeAllHelperUtilities();

		templateContextHelper = null;
	}

	@Override
	public void destroy(ClassLoader classLoader) {
		templateContextHelper.removeHelperUtilities(classLoader);
	}

	@Override
	public String getName() {
		return TemplateConstants.LANG_TYPE_VM;
	}

	@Override
	public String[] getRestrictedVariables() {
		return _velocityEngineConfiguration.restrictedVariables();
	}

	@Override
	public void init() throws TemplateException {
		if (_velocityEngine != null) {
			return;
		}

		_velocityEngine = new VelocityEngine();

		ExtendedProperties extendedProperties = new FastExtendedProperties();

		extendedProperties.setProperty(
			VelocityEngine.DIRECTIVE_IF_TOSTRING_NULLCHECK,
			String.valueOf(
				_velocityEngineConfiguration.directiveIfToStringNullCheck()));

		extendedProperties.setProperty(
			VelocityEngine.EVENTHANDLER_METHODEXCEPTION,
			LiferayMethodExceptionEventHandler.class.getName());

		extendedProperties.setProperty(
			RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES,
			StringUtil.merge(_velocityEngineConfiguration.restrictedClasses()));

		extendedProperties.setProperty(
			RuntimeConstants.INTROSPECTOR_RESTRICT_PACKAGES,
			StringUtil.merge(
				_velocityEngineConfiguration.restrictedPackages()));

		extendedProperties.setProperty(
			VelocityEngine.RESOURCE_LOADER, "liferay");

		boolean cacheEnabled = false;

		if (_velocityEngineConfiguration.resourceModificationCheckInterval() !=
				0) {

			cacheEnabled = true;
		}

		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER + ".cache",
			String.valueOf(cacheEnabled));

		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER +
			".resourceModificationCheckInterval",
			_velocityEngineConfiguration.resourceModificationCheckInterval() +
			"");

		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_LOADER + ".class",
			LiferayResourceLoader.class.getName());

		extendedProperties.setProperty(
			VelocityEngine.RESOURCE_MANAGER_CLASS,
			LiferayResourceManager.class.getName());

		extendedProperties.setProperty(
			"liferay." + VelocityEngine.RESOURCE_MANAGER_CLASS +
			".resourceModificationCheckInterval",
			_velocityEngineConfiguration.resourceModificationCheckInterval() +
			"");

		extendedProperties.setProperty(
			VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
			_velocityEngineConfiguration.logger());

		extendedProperties.setProperty(
			VelocityEngine.RUNTIME_LOG_LOGSYSTEM + ".log4j.category",
			_velocityEngineConfiguration.loggerCategory());

		extendedProperties.setProperty(
			RuntimeConstants.UBERSPECT_CLASSNAME,
			SecureUberspector.class.getName());

		extendedProperties.setProperty(
			VelocityEngine.VM_LIBRARY,
			_velocityEngineConfiguration.velocimacroLibrary());

		extendedProperties.setProperty(
			VelocityEngine.VM_LIBRARY_AUTORELOAD,
			String.valueOf(!cacheEnabled));

		extendedProperties.setProperty(
			VelocityEngine.VM_PERM_ALLOW_INLINE_REPLACE_GLOBAL,
			String.valueOf(!cacheEnabled));

		_velocityEngine.setExtendedProperties(extendedProperties);

		try {
			_velocityEngine.init();
		}
		catch (Exception e) {
			throw new TemplateException(e);
		}
	}

	@Reference(unbind = "-")
	public void setVelocityTemplateContextHelper(
		VelocityTemplateContextHelper velocityTemplateContextHelper) {

		templateContextHelper = velocityTemplateContextHelper;
	}

	@Reference(unbind = "-")
	public void setVelocityTemplateResourceLoader(
		VelocityTemplateResourceLoader velocityTemplateResourceLoader) {

		templateResourceLoader = velocityTemplateResourceLoader;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_velocityEngineConfiguration = Configurable.createConfigurable(
			VelocityEngineConfiguration.class, properties);
	}

	@Override
	protected Template doGetTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource, boolean restricted,
		Map<String, Object> helperUtilities, boolean privileged) {

		Template template = new VelocityTemplate(
			templateResource, errorTemplateResource, helperUtilities,
			_velocityEngine, templateContextHelper,
			_velocityEngineConfiguration.resourceModificationCheckInterval(),
			privileged);

		if (restricted) {
			template = new RestrictedTemplate(
				template, templateContextHelper.getRestrictedVariables());
		}

		return template;
	}

	protected VelocityTaglib getVelocityTaglib(
		HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();

		ServletContext servletContext = session.getServletContext();

		try {
			VelocityTaglib velocityTaglib = new VelocityTaglibImpl(
				servletContext, request,
				new PipingServletResponse(response, response.getWriter()),
				null);

			return velocityTaglib;
		}
		catch (IOException ioe) {
			return ReflectionUtil.throwException(ioe);
		}
	}

	private static volatile VelocityEngineConfiguration
		_velocityEngineConfiguration;

	private VelocityEngine _velocityEngine;

}
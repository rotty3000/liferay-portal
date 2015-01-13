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

package com.liferay.portal.template.freemarker;

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.portal.freemarker.configuration.FreemarkerEngineConfiguration;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.template.BaseTemplateManager;
import com.liferay.portal.template.RestrictedTemplate;
import com.liferay.portal.template.TemplateContextHelper;

import freemarker.cache.TemplateCache;

import freemarker.debug.impl.DebuggerService;

import freemarker.template.Configuration;

import java.lang.reflect.Field;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Tina Tina
 */
@Component(
		configurationPid = "com.liferay.portal.template.freemarker",
		configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
		service = TemplateManager.class
)
public class FreeMarkerManager extends BaseTemplateManager {

	@Override
	public void destroy() {
		if (_configuration == null) {
			return;
		}

		_configuration.clearEncodingMap();
		_configuration.clearSharedVariables();
		_configuration.clearTemplateCache();

		_configuration = null;

		templateContextHelper.removeAllHelperUtilities();

		templateContextHelper = null;

		if (isEnableDebuggerService()) {
			//DebuggerService.shutdown();
		}
	}

	@Override
	public void destroy(ClassLoader classLoader) {
		templateContextHelper.removeHelperUtilities(classLoader);
	}

	@Override
	public String getName() {
		return TemplateConstants.LANG_TYPE_FTL;
	}

	@Override
	public String[] getRestrictedVariables() {
		return _freemarkerEngineConfiguration.getRestrictedVariables();
	}

	@Override
	public void init() throws TemplateException {
		if (_configuration != null) {
			return;
		}

		_configuration = new Configuration();

		try {
			Field field = ReflectionUtil.getDeclaredField(
				Configuration.class, "cache");

			TemplateCache templateCache = new LiferayTemplateCache(
				_configuration, _freemarkerEngineConfiguration);

			field.set(_configuration, templateCache);
		}
		catch (Exception e) {
			throw new TemplateException(
				"Unable to Initialize Freemarker manager");
		}

		_configuration.setDefaultEncoding(StringPool.UTF8);
		_configuration.setLocalizedLookup(
			_freemarkerEngineConfiguration.getLocalizedLookup());
		_configuration.setNewBuiltinClassResolver(
			new LiferayTemplateClassResolver());
		_configuration.setObjectWrapper(new LiferayObjectWrapper());

		try {
			_configuration.setSetting(
				"auto_import",
				_freemarkerEngineConfiguration.getMacroLibrary());
			_configuration.setSetting(
				"template_exception_handler",
				_freemarkerEngineConfiguration.getTemplateExceptionHandler());
		}
		catch (Exception e) {
			throw new TemplateException("Unable to init freemarker manager", e);
		}

		if (isEnableDebuggerService()) {
			DebuggerService.getBreakpoints("*");
		}
	}

	@Reference(unbind = "-")
	public void setFreeMarkerTemplateContextHelper(
		TemplateContextHelper freeMarkerTemplateContextHelper) {

		templateContextHelper = freeMarkerTemplateContextHelper;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_freemarkerEngineConfiguration = Configurable.createConfigurable(
			FreemarkerEngineConfiguration.class, properties);
	}

	@Override
	protected Template doGetTemplate(
		TemplateResource templateResource,
		TemplateResource errorTemplateResource, boolean restricted,
		Map<String, Object> helperUtilities, boolean privileged) {

		Template template = new FreeMarkerTemplate(
			templateResource, errorTemplateResource, helperUtilities,
			_configuration, templateContextHelper, privileged,
			_freemarkerEngineConfiguration.getResourceModificationCheck());

		if (restricted) {
			template = new RestrictedTemplate(
				template, templateContextHelper.getRestrictedVariables());
		}

		return template;
	}

	protected boolean isEnableDebuggerService() {
		if ((System.getProperty("freemarker.debug.password") != null) &&
			(System.getProperty("freemarker.debug.port") != null)) {

			return true;
		}

		return false;
	}

	private Configuration _configuration;
	private volatile FreemarkerEngineConfiguration
		_freemarkerEngineConfiguration;

}
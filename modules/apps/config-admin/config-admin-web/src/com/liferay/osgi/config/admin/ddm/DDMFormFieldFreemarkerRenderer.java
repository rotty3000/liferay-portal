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

package com.liferay.osgi.config.admin.ddm;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.JSPSupportServlet;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.URLTemplateResource;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormField;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormFieldOptions;
import com.liferay.portlet.dynamicdatamapping.model.LocalizedValue;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldRenderer;
import com.liferay.portlet.dynamicdatamapping.render.DDMFormFieldRenderingContext;
import com.liferay.util.freemarker.FreeMarkerTaglibFactoryUtil;

import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;

import java.io.Writer;

import java.net.URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.GenericServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.osgi.framework.Bundle;

/**
 * @author Kamesh Sampath
 */
public class DDMFormFieldFreemarkerRenderer implements DDMFormFieldRenderer {

	public DDMFormFieldFreemarkerRenderer(Bundle bundle) {
		_bundle = bundle;

		String defaultTemplateId = "alloy/text.ftl";

		_defaultTemplateResource = getTemplateResource(defaultTemplateId);
	}

	@Override
	public String[] getSupportedDDMFormFieldTypes() {
		return _SUPPORTED_DDM_FORM_FIELD_TYPES;
	}

	@Override
	public String render(
			DDMFormField ddmFormField,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws PortalException {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Processing field:" + ddmFormField.getName());
			}

			// FOR NOW WE WILL NOT HANDLE fields, field, mode etc.,

			HttpServletRequest request =
				ddmFormFieldRenderingContext.getHttpServletRequest();
			HttpServletResponse response =
				ddmFormFieldRenderingContext.getHttpServletResponse();
			String portletNamespace =
				ddmFormFieldRenderingContext.getPortletNamespace();
			String namespace = ddmFormFieldRenderingContext.getNamespace();
			boolean readOnly = ddmFormFieldRenderingContext.isReadOnly();
			boolean showEmptyFieldLabel =
				ddmFormFieldRenderingContext.isShowEmptyFieldLabel();
			Locale locale = ddmFormFieldRenderingContext.getLocale();

			return _buildFieldHTML(
				request, response, ddmFormField, portletNamespace, namespace,
				readOnly, showEmptyFieldLabel, locale);
		}
		catch (Exception e) {
			throw new PortalException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Map<String, Object>> getFieldsContext(
		HttpServletRequest request, HttpServletResponse response,
		String portletNamespace, String namespace) {

		String fieldsContextKey =
			portletNamespace + namespace + "fieldsContext";

		Map<String, Map<String, Object>> fieldsContext =
			(Map<String, Map<String, Object>>)request.getAttribute(
				fieldsContextKey);

		if (fieldsContext == null) {
			fieldsContext = new HashMap<String, Map<String, Object>>();

			request.setAttribute(fieldsContextKey, fieldsContext);
		}

		return fieldsContext;
	}

	private Map<String, Object> _buildFieldContext(
		HttpServletRequest request, HttpServletResponse response,
		DDMFormField ddmFormField, String portletNamespace, String namespace,
		boolean readOnly, boolean showEmptyFieldLabel, Locale locale) {

		Map<String, Map<String, Object>> fieldsContext = getFieldsContext(
			request, response, portletNamespace, namespace);

		String name = ddmFormField.getName();

		boolean localizable = ddmFormField.isLocalizable();

		Map<String, Object> fieldContext = fieldsContext.get(name);

		if (fieldContext != null) {
			return fieldContext;
		}

		DDMForm ddmForm = ddmFormField.getDDMForm();

		Set<Locale> availableLocales = ddmForm.getAvailableLocales();

		Locale defaultLocale = ddmForm.getDefaultLocale();

		Locale structureLocale = locale;

		if (!availableLocales.contains(locale)) {
			structureLocale = defaultLocale;
		}

		fieldContext = new HashMap<String, Object>();

		_layoutProperties(ddmFormField, fieldContext, structureLocale);

		_structureProperties(ddmFormField, fieldContext);

		if (!localizable && !locale.equals(defaultLocale)) {
			fieldContext.put("disabled", Boolean.TRUE.toString());
		}

		boolean checkRequired = GetterUtil.getBoolean(
			request.getAttribute("checkRequired"), true);

		if (!checkRequired) {
			fieldContext.put("required", Boolean.FALSE.toString());
		}

		fieldsContext.put(name, fieldContext);

		return fieldContext;
	}

	@SuppressWarnings("unchecked")
	private String _buildFieldHTML(
		HttpServletRequest request, HttpServletResponse response,
		DDMFormField ddmFormField, String portletNamespace, String namespace,
		boolean readOnly, boolean showEmptyFieldLabel,
		Locale locale) throws Exception {

		StringBuilder fieldHTML = new StringBuilder();

		// Build Freemarker context map

		Map<String, Object> freeMarkerContext = _buildFreemarkerContext(
			request, response, ddmFormField, portletNamespace, namespace,
			readOnly, showEmptyFieldLabel, locale);

		// Build the HTML String from fieldStructure

		Map<String, Object> fieldStructure =
			(Map<String, Object>)freeMarkerContext.get("fieldStructure");

		int fieldRepetition = 1;

		// TODO Handle how to build repetition count

		while (fieldRepetition > 0) {
			String fieldNamespace = StringUtil.randomId();

			fieldStructure.put("fieldNamespace", fieldNamespace);

			// NO NESTED CHILDREN TO HANDLE

			StringBundler childrenHTML = new StringBundler(2);

			if (Validator.equals(ddmFormField.getType(), "select") ||
				Validator.equals(ddmFormField.getType(), "radio")) {

				Map<String, Object> optionFreeMarkerContext =
					new HashMap<String, Object>(freeMarkerContext);

				optionFreeMarkerContext.put(
					"parentFieldStructure", fieldStructure);

				childrenHTML.append(
					_ddmFormFieldOptionHTML(
						request, response, ddmFormField, readOnly, locale,
						optionFreeMarkerContext));
			}

			fieldStructure.put("children", childrenHTML.toString());

			boolean disabled = GetterUtil.getBoolean(
				fieldStructure.get("disabled"), false);

			if (disabled) {
				readOnly = true;
			}

			fieldHTML.append(
				_processFTL(
					request, response, ddmFormField.getNamespace(),
					ddmFormField.getType(), readOnly, freeMarkerContext));

			fieldRepetition--;
		}

		return fieldHTML.toString();
	}

	private Map<String, Object> _buildFreemarkerContext(
		HttpServletRequest request, HttpServletResponse response,
		DDMFormField ddmFormField, String portletNamespace, String namespace,
		boolean readOnly, boolean showEmptyFieldLabel, Locale locale) {

		Map<String, Object> freeMarkerContext = new HashMap<String, Object>();

		// Build Field Context

		Map<String, Object> fieldContext =
			_buildFieldContext(
				request, response, ddmFormField, portletNamespace, namespace,
				readOnly, showEmptyFieldLabel, locale);

		freeMarkerContext.put("fieldStructure", fieldContext);
		freeMarkerContext.put("namespace", namespace);

		freeMarkerContext.put("parentFieldStructure", Collections.emptyMap());

		freeMarkerContext.put("portletNamespace", portletNamespace);
		freeMarkerContext.put(
			"requestedLanguageDir", LanguageUtil.get(locale, "lang.dir"));
		freeMarkerContext.put("requestedLocale", locale);
		freeMarkerContext.put("showEmptyFieldLabel", showEmptyFieldLabel);

		return freeMarkerContext;
	}

	private String _ddmFormFieldOptionHTML(
		HttpServletRequest request, HttpServletResponse response,
		DDMFormField ddmFormField, boolean readOnly, Locale locale,
		Map<String, Object> freeMarkerContext) throws Exception {

		StringBundler fieldHTML = new StringBundler();

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		for (String value : ddmFormFieldOptions.getOptionsValues()) {
			Map<String, Object> fieldStructure = new HashMap<String, Object>();

			fieldStructure.put("children", StringPool.BLANK);
			fieldStructure.put("fieldNamespace", StringUtil.randomId());

			LocalizedValue label = ddmFormFieldOptions.getOptionLabels(value);

			fieldStructure.put("label", label.getString(locale));

			fieldStructure.put("name", StringUtil.randomId());
			fieldStructure.put("value", value);

			freeMarkerContext.put("fieldStructure", fieldStructure);

			fieldHTML.append(
				_processFTL(
					request, response, ddmFormField.getNamespace(), "option",
					readOnly, freeMarkerContext));
		}

		return fieldHTML.toString();
	}

	private void _layoutProperties(
		DDMFormField ddmFormField, Map<String, Object> fieldContext,
		Locale locale) {

		LocalizedValue label = ddmFormField.getLabel();

		fieldContext.put("label", label.getString(locale));

		LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

		fieldContext.put("predefinedValue", predefinedValue.getString(locale));

		LocalizedValue tip = ddmFormField.getTip();

		fieldContext.put("tip", tip.getString(locale));
	}

	private String _processFTL(
		HttpServletRequest request, HttpServletResponse response,
		String fieldNamespace, String type, boolean readOnly,
		Map<String, Object> freeMarkerContext) throws Exception {

		if (Validator.isNull(fieldNamespace)) {
			fieldNamespace = _DEFAULT_NAMESPACE;
		}

		TemplateResource templateResource = _defaultTemplateResource;

		Map<String, Object> fieldStructure =
			(Map<String, Object>)freeMarkerContext.get("fieldStructure");

		// TODO handle readonly

		String templateName = StringUtil.replaceFirst(
			type, fieldNamespace.concat(StringPool.DASH), StringPool.BLANK);

		StringBundler resourcePath = new StringBundler(5);

		resourcePath.append(StringUtil.toLowerCase(fieldNamespace));
		resourcePath.append(CharPool.SLASH);
		resourcePath.append(templateName);
		resourcePath.append(_TPL_EXT);

		String templatePath = resourcePath.toString();

		templateResource = getTemplateResource(templatePath);

		if (templateResource == null) {
			throw new Exception(
				"Unable to load template resource " + templatePath);
		}

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, templateResource, false);

		for (Map.Entry<String, Object> entry : freeMarkerContext.entrySet()) {
			template.put(entry.getKey(), entry.getValue());
		}

		return _processFTL(request, response, template);
	}

	private String _processFTL(
		HttpServletRequest request, HttpServletResponse response,
		Template template) throws Exception {

		// FreeMarker variables

		template.prepare(request);

		// Tag libraries

		Writer writer = new UnsyncStringWriter();

		// Portal JSP tag library factory

		TemplateHashModel portalTaglib =
			FreeMarkerTaglibFactoryUtil.createTaglibFactory(
				request.getServletContext());

		template.put("PortalJspTagLibs", portalTaglib);

		// FreeMarker JSP tag library support

		GenericServlet genericServlet = new JSPSupportServlet(
			request.getServletContext());

		ServletContextHashModel servletContextHashModel =
			new ServletContextHashModel(
				genericServlet, ObjectWrapper.DEFAULT_WRAPPER);

		template.put("Application", servletContextHashModel);

		HttpServletRequestWrapper httpServletRequestWrapper =
			new HttpServletRequestWrapper(request);

		HttpServletResponseWrapper httpServletResponseWrapper =
			new HttpServletResponseWrapper(response);

		HttpRequestHashModel httpRequestHashModel =
			new HttpRequestHashModel(
				httpServletRequestWrapper, httpServletResponseWrapper,
				ObjectWrapper.DEFAULT_WRAPPER);

		template.put("Request", httpRequestHashModel);

		// Merge templates

		template.processTemplate(writer);

		return writer.toString();
	}

	private void _structureProperties(
		DDMFormField ddmFormField, Map<String, Object> fieldContext) {

		fieldContext.put("dataType", ddmFormField.getDataType());

		fieldContext.put("fieldNamespace", ddmFormField.getNamespace());

		fieldContext.put("indexType", ddmFormField.getIndexType());

		fieldContext.put(
			"localizable", Boolean.toString(ddmFormField.isLocalizable()));

		fieldContext.put(
			"multiple", Boolean.toString(ddmFormField.isMultiple()));

		fieldContext.put("name", ddmFormField.getName());

		fieldContext.put(
			"readOnly", Boolean.toString(ddmFormField.isReadOnly()));

		fieldContext.put(
			"repeatable", Boolean.toString(ddmFormField.isRepeatable()));

		fieldContext.put(
			"required", Boolean.toString(ddmFormField.isRequired()));

		fieldContext.put(
			"showLabel", Boolean.toString(ddmFormField.isShowLabel()));

		fieldContext.put("type", ddmFormField.getType());
	}

	private TemplateResource getTemplateResource(String templatePath) {
		templatePath = _TPL_PATH.concat(templatePath);

		URL url = _bundle.getResource(templatePath);

		return new URLTemplateResource(templatePath, url);
	}

	private static final String _DEFAULT_NAMESPACE = "alloy";

	private static final String[] _SUPPORTED_DDM_FORM_FIELD_TYPES = {
		"checkbox", "fieldset", "option", "radio", "select", "text", "textarea"
	};

	private static final String _TPL_EXT = ".ftl";

	private static final String _TPL_PATH =
					"/com/liferay/portlet/dynamicdatamapping/dependencies/";

	private static Log _log = LogFactoryUtil.getLog(
		DDMFormFieldFreemarkerRenderer.class);

	private Bundle _bundle;
	private TemplateResource _defaultTemplateResource;

}
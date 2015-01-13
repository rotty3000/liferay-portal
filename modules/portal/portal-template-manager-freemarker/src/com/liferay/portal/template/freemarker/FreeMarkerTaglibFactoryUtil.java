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

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.JSPSupportServlet;
import com.liferay.portal.kernel.template.TaglibFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateConstants;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.io.Writer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Shuyang Zhou
 */
@Component(
		configurationPid = "com.liferay.portal.template.freemarker",
		configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
		service = TaglibFactoryUtil.class
)
public class FreeMarkerTaglibFactoryUtil implements TaglibFactoryUtil {

	public static TemplateHashModel staticCreateTaglibFactory(
		ServletContext servletContext) {

		return new TaglibFactoryWrapper(servletContext);
	}

	public FreeMarkerTaglibFactoryUtil() {
	}

	public void addStaticClassSupportFTL(
		Map<String, Object> contextObjects, String variableName,
		Class<?> variableClass) {

		try {
			BeansWrapper beansWrapper = BeansWrapper.getDefaultInstance();

			TemplateHashModel templateHashModel =
				beansWrapper.getStaticModels();

			TemplateModel templateModel = templateHashModel.get(
				variableClass.getCanonicalName());

			contextObjects.put(variableName, templateModel);
		}
		catch (TemplateModelException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Variable " + variableName + " registration fail", e);
			}
		}
	}

	@Override
	public void addTaglibSupportFTL(
			Map<String, Object> contextObjects, HttpServletRequest request,
			HttpServletResponse response)
		throws Exception {

		// FreeMarker servlet application

		GenericServlet genericServlet = new JSPSupportServlet(
			request.getServletContext());

		ServletContextHashModel servletContextHashModel =
			new ServletContextHashModel(
				genericServlet, ObjectWrapper.DEFAULT_WRAPPER);

		contextObjects.put(
			PortletDisplayTemplateConstants.FREEMARKER_SERVLET_APPLICATION,
			servletContextHashModel);

		// FreeMarker servlet request

		HttpRequestHashModel requestHashModel = new HttpRequestHashModel(
			request, response, ObjectWrapper.DEFAULT_WRAPPER);

		contextObjects.put(
			PortletDisplayTemplateConstants.FREEMARKER_SERVLET_REQUEST,
			requestHashModel);

		// Taglib Liferay hash

		TemplateHashModel taglibLiferayHash = createTaglibFactory(
			request.getServletContext());

		contextObjects.put(
			PortletDisplayTemplateConstants.TAGLIB_LIFERAY_HASH,
			taglibLiferayHash);
	}

	public TemplateHashModel createTaglibFactory(
		ServletContext servletContext) {

		return new TaglibFactoryWrapper(servletContext);
	}

	@Override
	public void includeContextHashModel(
		ServletContext servletContext, HttpServletRequest request,
		HttpServletResponse response, Template template) {

		GenericServlet genericServlet = new JSPSupportServlet(servletContext);

		ServletContextHashModel servletContextHashModel =
			new ServletContextHashModel(
				genericServlet, ObjectWrapper.DEFAULT_WRAPPER);

		template.put("Application", servletContextHashModel);

		HttpRequestHashModel httpRequestHashModel = new HttpRequestHashModel(
			request, response, ObjectWrapper.DEFAULT_WRAPPER);

		template.put("Request", httpRequestHashModel);
	}

	@Override
	public void includeTagLib(
		ServletContext servletContext, String tagLibName, Template template) {

		TemplateHashModel tagLib = createTaglibFactory(servletContext);

		template.put(tagLibName, tagLib);
	}

	@Override
	public String processFTL(
			HttpServletRequest request, HttpServletResponse response,
			Template template)
		throws Exception {

		// FreeMarker variables

		template.prepare(request);

		// Tag libraries

		Writer writer = new UnsyncStringWriter();

		// Portal JSP tag library factory

		TemplateHashModel portalTaglib = createTaglibFactory(
			request.getServletContext());

		template.put("PortalJspTagLibs", portalTaglib);

		// FreeMarker JSP tag library support

		GenericServlet genericServlet = new JSPSupportServlet(
			request.getServletContext());

		ServletContextHashModel servletContextHashModel =
			new ServletContextHashModel(
				genericServlet, ObjectWrapper.DEFAULT_WRAPPER);

		template.put("Application", servletContextHashModel);

		HttpRequestHashModel httpRequestHashModel = new HttpRequestHashModel(
			request, response, ObjectWrapper.DEFAULT_WRAPPER);

		template.put("Request", httpRequestHashModel);

		// Merge templates

		template.processTemplate(writer);

		return writer.toString();
	}

	private static FreeMarkerTaglibFactoryUtil _getInstance() {
		if (_instance != null) {
			return _instance;
		}

		return _instance;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FreeMarkerTaglibFactoryUtil.class);

	private static FreeMarkerTaglibFactoryUtil _instance = new FreeMarkerTaglibFactoryUtil();

	private Map<String, TemplateModel> _templateModels =
		new ConcurrentHashMap<String, TemplateModel>();

	private static class TaglibFactoryWrapper implements TemplateHashModel {

		public TaglibFactoryWrapper(ServletContext servletContext) {
			FreeMarkerTaglibFactoryUtil freeMarkerTaglibFactoryUtil =
				_getInstance();

			_templateModels = freeMarkerTaglibFactoryUtil._templateModels;
			_taglibFactory = new TaglibFactory(servletContext);
		}

		@Override
		public TemplateModel get(String uri) throws TemplateModelException {
			TemplateModel templateModel = _templateModels.get(uri);

			if (templateModel == null) {
				templateModel = _taglibFactory.get(uri);

				_templateModels.put(uri, templateModel);
			}

			return templateModel;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		private final TaglibFactory _taglibFactory;
		private final Map<String, TemplateModel> _templateModels;

	}

}
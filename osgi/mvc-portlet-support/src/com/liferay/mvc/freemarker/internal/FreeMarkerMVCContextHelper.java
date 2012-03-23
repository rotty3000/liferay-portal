/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.mvc.freemarker.internal;

import com.liferay.portal.freemarker.FreeMarkerTemplateLoader;
import com.liferay.portal.kernel.freemarker.FreeMarkerContext;
import com.liferay.portal.kernel.freemarker.FreeMarkerVariablesUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.freemarker.FreeMarkerTaglibFactoryUtil;

import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;

import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Raymond Augé
 */
public class FreeMarkerMVCContextHelper {

	public static final String TEMPLATE_REGISTRY_ATTRIBUTE =
		FreeMarkerContext.class.getName().concat(".templateRegistry");

	public static void addPortletJSPTaglibSupport(
			FreeMarkerContext freeMarkerContext, PortletRequest portletRequest,
			PortletResponse portletResponse, Set<String> templateRegistry)
		throws Exception {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
			portletRequest);
		HttpServletResponse response = PortalUtil.getHttpServletResponse(
			portletResponse);

		FreeMarkerVariablesUtil.insertVariables(freeMarkerContext, request);

		Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

		String servletContextName =
			portlet.getPortletApp().getServletContextName();

		final ServletContext servletContext = ServletContextPool.get(
			servletContextName);

		Object templateRegistryAttribute = servletContext.getAttribute(
			TEMPLATE_REGISTRY_ATTRIBUTE);

		if ((templateRegistry != null) && (templateRegistryAttribute == null)) {
			servletContext.setAttribute(
				TEMPLATE_REGISTRY_ATTRIBUTE, templateRegistry);
		}

		freeMarkerContext.put(
			"fullTemplatesPath",
			StringPool.SLASH + servletContextName +
			FreeMarkerTemplateLoader.SERVLET_SEPARATOR);

		TemplateHashModel taglibsFactory =
			FreeMarkerTaglibFactoryUtil.createTaglibFactory(servletContext);

		freeMarkerContext.put("PortletJspTagLibs", taglibsFactory);

		ServletConfig servletConfig =
			(ServletConfig)portletRequest.getAttribute(
				PortletServlet.PORTLET_SERVLET_CONFIG);

		final PortletServlet portletServlet = new PortletServlet();

		portletServlet.init(servletConfig);

		ServletContextHashModel servletContextHashModel =
			new ServletContextHashModel(
				portletServlet, ObjectWrapper.DEFAULT_WRAPPER);

		freeMarkerContext.put("Application", servletContextHashModel);

		HttpRequestHashModel httpRequestHashModel = new HttpRequestHashModel(
			request, response, ObjectWrapper.DEFAULT_WRAPPER);

		freeMarkerContext.put("Request", httpRequestHashModel);
	}

}
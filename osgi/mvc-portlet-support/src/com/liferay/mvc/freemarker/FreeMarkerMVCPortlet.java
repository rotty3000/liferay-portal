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

package com.liferay.mvc.freemarker;

import com.liferay.mvc.freemarker.internal.FreeMarkerMVCContextHelper;
import com.liferay.portal.kernel.concurrent.ConcurrentHashSet;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateContextType;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.io.Writer;

import java.net.URL;

import java.util.Set;

import javax.portlet.MimeResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * @author Raymond Augé
 */
public class FreeMarkerMVCPortlet extends MVCPortlet {

	protected Template getFreeMarkerTemplate(
			String templateId, String templateContent,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		Template freeMarkerTemplate = TemplateManagerUtil.getTemplate(
			TemplateManager.FREEMARKER, templateId, templateContent,
			TemplateContextType.STANDARD);

		freeMarkerTemplate.put("portletContext", getPortletContext());
		freeMarkerTemplate.put(
			"userInfo", portletRequest.getAttribute(PortletRequest.USER_INFO));

		FreeMarkerMVCContextHelper.addPortletJSPTaglibSupport(
			freeMarkerTemplate, portletRequest, portletResponse, _templateIds);

		return freeMarkerTemplate;
	}

	@Override
	protected void include(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, String lifecycle)
		throws IOException, PortletException {

		PortletContext portletContext = getPortletContext();

		URL resource = portletContext.getResource(path);

		if (resource == null) {
			_log.error(path + " is not a valid include");
		}
		else {
			try {
				Writer writer = null;

				if (portletResponse instanceof MimeResponse) {
					MimeResponse mimeResponse = (MimeResponse)portletResponse;

					writer = UnsyncPrintWriterPool.borrow(
						mimeResponse.getWriter());
				}
				else {
					writer = new UnsyncStringWriter();
				}

				// Merge templates

				String templateId = portletResponse.getNamespace() + path;

				if (!_templateIds.contains(templateId)) {
					_templateIds.add(templateId);
				}

				String templateContent = HttpUtil.URLtoString(resource);

				Template freeMarkerTemplate = getFreeMarkerTemplate(
					templateId, templateContent, portletRequest,
					portletResponse);

				freeMarkerTemplate.processTemplate(writer);
			}
			catch (Exception e) {
				throw new PortletException(e);
			}
		}

		if (clearRequestParameters) {
			if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
				portletResponse.setProperty("clear-request-parameters", "true");
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();

		TemplateManager freeMarkerTemplateManager =
			TemplateManagerUtil.getTemplateManager(TemplateManager.FREEMARKER);

		for (String templateId : _templateIds) {
			freeMarkerTemplateManager.clearCache(templateId);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(FreeMarkerMVCPortlet.class);

	private Set<String> _templateIds = new ConcurrentHashSet<String>();

}
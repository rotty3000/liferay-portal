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
import com.liferay.portal.kernel.freemarker.FreeMarkerContext;
import com.liferay.portal.kernel.freemarker.FreeMarkerEngineUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

	protected FreeMarkerContext getFreeMarkerContext(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		FreeMarkerContext freeMarkerContext =
			FreeMarkerEngineUtil.getWrappedStandardToolsContext();

		freeMarkerContext.put("portletContext", getPortletContext());
		freeMarkerContext.put(
			"userInfo", portletRequest.getAttribute(PortletRequest.USER_INFO));

		FreeMarkerMVCContextHelper.addPortletJSPTaglibSupport(
			freeMarkerContext, portletRequest, portletResponse, _templateIds);

		return freeMarkerContext;
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
				FreeMarkerContext freeMarkerContext = getFreeMarkerContext(
					portletRequest, portletResponse);

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

				String template = HttpUtil.URLtoString(resource);

				String templateId = portletResponse.getNamespace() + path;

				if (!_templateIds.contains(templateId)) {
					_templateIds.add(templateId);
				}

				FreeMarkerEngineUtil.mergeTemplate(
					templateId, template, freeMarkerContext, writer);
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

		for (String templateId : _templateIds) {
			FreeMarkerEngineUtil.flushTemplate(templateId);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(FreeMarkerMVCPortlet.class);

	private Set<String> _templateIds = new ConcurrentHashSet<String>();

}
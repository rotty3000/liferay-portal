/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.taglib.util;

import com.liferay.kernel.taglib.PortletViewExtension;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;

import com.liferay.registry.collections.ServiceTrackerMap;
import com.liferay.registry.collections.ServiceTrackerMapFactory;
import com.liferay.taglib.TagSupport;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.filter.RenderResponseWrapper;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Carlos Sierra Andrés
 */
public class ExtensionTag extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		List<PortletViewExtension> viewExtensions = _extensions.getService(
			getExtensionId());

		if ((viewExtensions != null) && !viewExtensions.isEmpty()) {
			for (PortletViewExtension viewExtension : viewExtensions) {
				try {
					viewExtension.render(
						getRenderRequest(), getRenderResponse());
				}
				catch (Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
			}
		}

		return super.doEndTag();
	}

	protected RenderResponse getRenderResponse() throws IOException {
		final RenderResponse renderResponse = getFromConstant(
			JavaConstants.JAVAX_PORTLET_RESPONSE);

		return new RenderResponseWrapper(renderResponse) {

			@Override
			public OutputStream getPortletOutputStream() throws IOException {

				return new OutputStream() {

					@Override
					public void write(int b) throws IOException {
						pageContext.getOut().write(b);
					}
				};
			}

			@Override
			public PrintWriter getWriter() throws IOException {
				return new PrintWriter(pageContext.getOut(), true);
			}

		};
	}

	protected RenderRequest getRenderRequest() {
		return getFromConstant(JavaConstants.JAVAX_PORTLET_REQUEST);
	}

	private <T> T getFromConstant(String constant) {

		ServletRequest request = pageContext.getRequest();

		T result = (T)request.getAttribute(constant);

		if (result == null) {
			throw new IllegalStateException(
				"This tag can only be used inside a portlet");
		}

		return result;
	}

	@Override
	public int doStartTag() throws JspException {
		List<PortletViewExtension> viewExtensions = _extensions.getService(
			getExtensionId());

		if (viewExtensions == null) {
			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	public String getExtensionId() {
		return _extensionId;
	}

	public void setExtensionId(String extensionId) {
		_extensionId = extensionId;
	}

	private String _extensionId;

	private static Log _log = LogFactoryUtil.getLog(ExtensionTag.class);

	private static ServiceTrackerMap<String, List<PortletViewExtension>>
		_extensions = ServiceTrackerMapFactory.createListServiceTracker(
		PortletViewExtension.class, "extension-id");

	static {
		_extensions.open();
	}

}
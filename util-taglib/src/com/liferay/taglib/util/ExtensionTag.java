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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;

import com.liferay.registry.collections.ServiceTrackerMap;
import com.liferay.registry.collections.ServiceTrackerMapFactory;
import com.liferay.taglib.TagSupport;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import java.util.List;

/**
 * @author Carlos Sierra Andrés
 */
public class ExtensionTag extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

	protected PortletResponse getPorletResponse() {
		return getFromConstant(JavaConstants.JAVAX_PORTLET_RESPONSE);
	}

	protected PortletRequest getPortletRequest() {
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
		List<ViewExtension> viewExtensions = _extensions.getService(
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

	private static ServiceTrackerMap<String, List<ViewExtension>>
		_extensions = ServiceTrackerMapFactory.createListServiceTracker(
		ViewExtension.class, "extension-id");

	static {
		_extensions.open();
	}

}
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

package com.liferay.taglib.portletext;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.taglib.FileAvailabilityUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.taglib.ui.IconTag;

import javax.portlet.PortletRequest;
import javax.portlet.WindowStateException;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class IconPortletCssTag extends IconTag {

	@Override
	protected String getPage() {
		if (FileAvailabilityUtil.isAvailable(servletContext, _PAGE)) {
			return _PAGE;
		}

		PortletDisplay portletDisplay =
			(PortletDisplay)pageContext.getAttribute("portletDisplay");

		if (!portletDisplay.isShowPortletCssIcon()) {
			return null;
		}

		setCssClass("portlet-css portlet-css-icon lfr-js-required");
		setImage("../portlet/portlet_css");
		setMessage("look-and-feel");

		String cssPortletURL = createCSSPortletURL();

		StringBundler onClickSB = new StringBundler(5);
		onClickSB.append("Liferay.Portlet.loadCSSEditor('");
		onClickSB.append(HtmlUtil.escapeJS(portletDisplay.getId()));
		onClickSB.append("', '");
		onClickSB.append(HtmlUtil.escapeJS(cssPortletURL));
		onClickSB.append("'); return false;");

		setOnClick(onClickSB.toString());

		setToolTip(false);
		setUrl(portletDisplay.getURLPortletCss());

		return super.getPage();
	}

	private String createCSSPortletURL() {
		String portletId = PortletKeys.PORTLET_CSS;
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			request, portletId, themeDisplay.getPlid(),
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setRenderPortletURL(true);

		try {
			liferayPortletURL.setWindowState(LiferayWindowState.EXCLUSIVE);
		}
		catch (WindowStateException e) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to set EXCLUSIVE state", e);
			}
		}

		return liferayPortletURL.toString();
	}

	private static final String _PAGE =
		"/html/taglib/portlet/icon_portlet_css/page.jsp";
	private static Log _log = LogFactoryUtil.getLog(IconPortletCssTag.class);

}
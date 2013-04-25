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

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Miguel Pastor
 */
public class PortletURLUtil {

	public static PortletURL clone(
			LiferayPortletURL liferayPortletURL, String lifecycle,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		LiferayPortletURL newLiferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(lifecycle);

		newLiferayPortletURL.setPortletId(liferayPortletURL.getPortletId());

		WindowState windowState = liferayPortletURL.getWindowState();

		if (windowState != null) {
			newLiferayPortletURL.setWindowState(windowState);
		}

		PortletMode portletMode = liferayPortletURL.getPortletMode();

		if (portletMode != null) {
			newLiferayPortletURL.setPortletMode(portletMode);
		}

		newLiferayPortletURL.setParameters(liferayPortletURL.getParameterMap());

		return newLiferayPortletURL;
	}

	public static PortletURL clone(
			PortletURL portletURL,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(
			liferayPortletURL, liferayPortletURL.getLifecycle(),
			liferayPortletResponse);
	}

	public static PortletURL clone(
			PortletURL portletURL, MimeResponse mimeResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(
			liferayPortletURL, liferayPortletURL.getLifecycle(),
			(LiferayPortletResponse)mimeResponse);
	}

	public static PortletURL clone(
			PortletURL portletURL, String lifecycle,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(liferayPortletURL, lifecycle, liferayPortletResponse);
	}

	public static PortletURL clone(
			PortletURL portletURL, String lifecycle, MimeResponse mimeResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(
			liferayPortletURL, lifecycle, (LiferayPortletResponse)mimeResponse);
	}

	public static PortletURL getCurrent(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		Enumeration<String> enu = liferayPortletRequest.getParameterNames();

		while (enu.hasMoreElements()) {
			String param = enu.nextElement();
			String[] values = liferayPortletRequest.getParameterValues(param);

			boolean addParam = true;

			// Don't set paramter values that are over 32 kb. See LEP-1755.

			for (int i = 0; i < values.length; i++) {
				if (values[i].length() > _CURRENT_URL_PARAMETER_THRESHOLD) {
					addParam = false;

					break;
				}
			}

			if (addParam) {
				portletURL.setParameter(param, values);
			}
		}

		return portletURL;
	}

	public static PortletURL getCurrent(
		PortletRequest portletRequest, MimeResponse mimeResponse) {

		return getCurrent(
			(LiferayPortletRequest)portletRequest,
			(LiferayPortletResponse)mimeResponse);
	}

	public static String getRefreshURL(
		HttpServletRequest request, ThemeDisplay themeDisplay) {

		long plid = themeDisplay.getPlid();

		Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

		String portletId = portlet.getPortletId();

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			request, portletId, plid, "0");

		portletURL.setLifecycle("0");
		portletURL.setParameter("p_t_lifecycle", themeDisplay.getLifecycle());

		WindowState windowState = WindowState.NORMAL;

		if (themeDisplay.isStatePopUp()) {
			windowState = LiferayWindowState.POP_UP;
		}
		else {
			LayoutTypePortlet layoutTypePortlet =
				themeDisplay.getLayoutTypePortlet();

			if (layoutTypePortlet.hasStateMaxPortletId(portletId)) {
				windowState = WindowState.MAXIMIZED;
			}
			else if (layoutTypePortlet.hasStateMinPortletId(portletId)) {
				windowState = WindowState.MINIMIZED;
			}
		}

		try {
			portletURL.setWindowState(windowState);
		} catch (WindowStateException e) {
			portletURL.setParameter("p_p_state", windowState.toString());
		}

		if (portlet.isStatic()) {
			portletURL.setParameter("p_p_static", "1");

			if (portlet.isStaticStart()) {
				portletURL.setParameter("p_p_static_start", "1");
			}
		}

		portletURL.setParameter("p_p_isolated", "1");

		long sourceGroupId = ParamUtil.getLong(request, "p_v_l_s_g_id");

		if (sourceGroupId > 0) {
			portletURL.setParameter(
				"p_v_l_s_g_id", Long.toString(sourceGroupId));
		}

		StringBundler sb = new StringBundler(8);

		sb.append(themeDisplay.getPathMain());
		sb.append("/portal/render_portlet?p_l_id=");
		sb.append(plid);
		sb.append(StringPool.AMPERSAND);

		String portletURLString = portletURL.toString();

		int queryStringIdx = portletURLString.indexOf(CharPool.QUESTION);
		sb.append(portletURLString.substring(queryStringIdx + 1));

		String currentURL = PortalUtil.getCurrentURL(request);
		sb.append("&currentURL=");
		sb.append(HttpUtil.encodeURL(currentURL));

		String ppid = ParamUtil.getString(request, "p_p_id");

		if (ppid.equals(portletId)) {
			String namespace = PortalUtil.getPortletNamespace(portletId);

			Map<String, String[]> parameters = request.getParameterMap();

			StringBundler paramsBundler = new StringBundler(parameters.size());

			for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
				String name = entry.getKey();

				if (!PortalUtil.isReservedParameter(name) &&
					!name.equals("currentURL") &&
					!isRefreshURLReservedParameter(name, namespace)) {

					String[] values = entry.getValue();

					for (int i = 0; i < values.length; i++) {
						String value = values[i];

						if (Validator.isNotNull(name) && (value != null)) {
							portletURL.setParameter(name, value);
						}

						paramsBundler.append(StringPool.AMPERSAND);
						paramsBundler.append(name);
						paramsBundler.append(StringPool.EQUAL);
						paramsBundler.append(HttpUtil.encodeURL(value));
					}
				}
			}

			sb.append(paramsBundler);
		}

		return sb.toString();
	}

	protected static boolean isRefreshURLReservedParameter(
		String parameter, String namespace) {

		if ((_PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS == null) ||
			(_PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS.length == 0)) {

			return false;
		}

		for (int i = 0; i < _PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS.length;
				i++) {

			String reservedParameter = namespace.concat(
				_PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS[i]);

			if (parameter.equals(reservedParameter)) {
				return true;
			}
		}

		return false;
	}

	private static final int _CURRENT_URL_PARAMETER_THRESHOLD = 32768;

	private static final String[] _PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS =
		PropsUtil.getArray(
			PropsKeys.PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS);

}
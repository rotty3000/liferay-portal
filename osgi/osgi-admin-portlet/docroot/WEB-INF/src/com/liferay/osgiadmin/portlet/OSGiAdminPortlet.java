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

package com.liferay.osgiadmin.portlet;

import com.liferay.mvc.freemarker.FreeMarkerMVCPortlet;
import com.liferay.osgiadmin.internal.BundleUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.OSGiException;
import com.liferay.portal.osgi.service.OSGiServiceUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Raymond Augé
 */
public class OSGiAdminPortlet extends FreeMarkerMVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		try {
			UploadPortletRequest uploadRequest =
				PortalUtil.getUploadPortletRequest(actionRequest);

			String cmd = ParamUtil.getString(uploadRequest, Constants.CMD);
			File file = uploadRequest.getFile("importBundle");
			String location = ParamUtil.getString(uploadRequest, "location");

			if (cmd.equals("install-from-upload")) {
				if (Validator.isNull(location)) {
					location = uploadRequest.getFullFileName("importBundle");
				}

				if ((file == null) || !file.exists()) {
					throw new OSGiException("file-does-not-exist");
				}

				OSGiServiceUtil.addBundle(location, new FileInputStream(file));
			}
			else if (cmd.equals("install-from-remote-location")) {
				OSGiServiceUtil.addBundle(location);
			}
			else if (cmd.equals("update-from-upload")) {
				long bundleId = ParamUtil.getLong(uploadRequest, "bundleId");

				if ((file == null) || !file.exists()) {
					throw new OSGiException("file-does-not-exist");
				}

				OSGiServiceUtil.updateBundle(
					bundleId, new FileInputStream(file));
			}
			else if (cmd.equals("update-from-remote-location")) {
				long bundleId = ParamUtil.getLong(uploadRequest, "bundleId");

				OSGiServiceUtil.updateBundle(bundleId);
			}
			else if (cmd.equals("uninstall")) {
				long bundleId = ParamUtil.getLong(uploadRequest, "bundleId");

				OSGiServiceUtil.uninstallBundle(bundleId);
			}

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception e) {
			if ((e instanceof OSGiException) ||
				(e instanceof PrincipalException)) {

				SessionErrors.add(actionRequest, e.getClass().getName());
			}
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException, IOException {

		Map<String, Object> ftlVariables = new HashMap<String,Object>();

		ftlVariables.put("BundleUtil", new BundleUtil());

		renderRequest.setAttribute(WebKeys.FTL_VARIABLES, ftlVariables);

		super.render(renderRequest, renderResponse);
	}

}
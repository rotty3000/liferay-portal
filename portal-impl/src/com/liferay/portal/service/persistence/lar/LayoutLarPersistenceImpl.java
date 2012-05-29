/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.service.persistence.lar;

import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.staging.LayoutStagingUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PrimitiveLongList;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.lar.LayoutCache;
import com.liferay.portal.model.Image;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutRevision;
import com.liferay.portal.model.LayoutStagingHandler;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.security.permission.ResourceActionsUtil;
import com.liferay.portal.service.ImageLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portal.service.persistence.LayoutRevisionUtil;
import com.liferay.portal.service.persistence.impl.BaseLarPersistenceImpl;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.journal.NoSuchArticleException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mate Thurzo
 */
public class LayoutLarPersistenceImpl extends BaseLarPersistenceImpl<Layout>
	implements LayoutLarPersistence {

	public static final String SAME_GROUP_FRIENDLY_URL =
		"/[$SAME_GROUP_FRIENDLY_URL$]";

	public void deserialize(Document document) {
		return;
	}

	public void serialize(Layout layout, PortletDataContext portletDataContext)
		throws Exception {

		String path = getEntityPath(layout);

		if (!portletDataContext.isPathNotProcessed(path)) {
			return;
		}

		LayoutRevision layoutRevision = null;

		if (LayoutStagingUtil.isBranchingLayout(layout)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			long layoutSetBranchId = ParamUtil.getLong(
				serviceContext, "layoutSetBranchId");

			if (layoutSetBranchId <= 0) {
				return;
			}

			layoutRevision = LayoutRevisionUtil.fetchByL_H_P(
				layoutSetBranchId, true, layout.getPlid());

			if (layoutRevision == null) {
				return;
			}

			LayoutStagingHandler layoutStagingHandler =
				LayoutStagingUtil.getLayoutStagingHandler(layout);

			layoutStagingHandler.setLayoutRevision(layoutRevision);
		}

		portletDataContext.setPlid(layout.getPlid());

		if (layout.isIconImage()) {
			Image image = ImageLocalServiceUtil.getImage(
				layout.getIconImageId());

			if (image != null) {
				imageLarPersistence.serialize(image, portletDataContext);
			}
		}

		//TODO layoutconfig portlet export
		/*_portletExporter.exportPortletData(
			portletDataContext, layoutConfigurationPortlet, layout, null,
			layoutElement);*/

		// Layout permissions

		Object exportPermissionsObj =
			portletDataContext.getAttribute("export-permissions");

		boolean exportPermissions = false;

		if (exportPermissionsObj != null) {
			exportPermissions = (Boolean)exportPermissionsObj;
		}

		if (exportPermissions) {
			exportLayoutPermissions(portletDataContext, new LayoutCache(),
				layout.getCompanyId(), layout.getGroupId(), layout);
		}

		fixTypeSettings(layout);

		addZipEntry(path, layout);

		addExpando(layout);
	}

	private String getLayoutIconPath(
		PortletDataContext portletDataContext, Layout layout, Image image) {

		StringBundler sb = new StringBundler(5);

		sb.append(portletDataContext.getLayoutPath(layout.getLayoutId()));
		sb.append("/icons/");
		sb.append(image.getImageId());
		sb.append(StringPool.PERIOD);
		sb.append(image.getType());

		return sb.toString();
	}

	private void exportLayoutPermissions(
			PortletDataContext portletDataContext, LayoutCache layoutCache,
			long companyId, long groupId, Layout layout)
		throws Exception {

		String resourceName = Layout.class.getName();
		String resourcePrimKey = String.valueOf(layout.getPlid());

		//TODO sort out querying up the roles
		List<Role> roles = null; //layoutCache.getGroupRoles_5(groupId, resourceName);

		List<String> actionIds = null;

		actionIds = ResourceActionsUtil.getModelResourceActions(
			resourceName);

		if (actionIds.isEmpty()) {
			return;
		}

		PrimitiveLongList roleIds = new PrimitiveLongList(roles.size());
		Map<Long, Role> roleIdsToRoles = new HashMap<Long, Role>();

		for (Role role : roles) {
			String name = role.getName();

			if (name.equals(RoleConstants.ADMINISTRATOR)) {
				continue;
			}

			roleIds.add(role.getRoleId());
			roleIdsToRoles.put(role.getRoleId(), role);
		}

		Map<Long, Set<String>> roleIdsToActionIds =
			ResourcePermissionLocalServiceUtil.
				getAvailableResourcePermissionActionIds(
					companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
					resourcePrimKey, roleIds.getArray(), actionIds);

		for (Role role : roleIdsToRoles.values()) {
			roleLarPersistence.serialize(role, portletDataContext);
		}
	}

	private void fixTypeSettings(Layout layout) throws Exception {
		if (!layout.isTypeURL()) {
			return;
		}

		UnicodeProperties typeSettings = layout.getTypeSettingsProperties();

		String url = GetterUtil.getString(typeSettings.getProperty("url"));

		String friendlyURLPrivateGroupPath =
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING;
		String friendlyURLPrivateUserPath =
			PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING;
		String friendlyURLPublicPath =
			PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING;

		if (!url.startsWith(friendlyURLPrivateGroupPath) &&
			!url.startsWith(friendlyURLPrivateUserPath) &&
			!url.startsWith(friendlyURLPublicPath)) {

			return;
		}

		int x = url.indexOf(CharPool.SLASH, 1);

		if (x == -1) {
			return;
		}

		int y = url.indexOf(CharPool.SLASH, x + 1);

		if (y == -1) {
			return;
		}

		String friendlyURL = url.substring(x, y);
		String groupFriendlyURL = layout.getGroup().getFriendlyURL();

		if (!friendlyURL.equals(groupFriendlyURL)) {
			return;
		}

		typeSettings.setProperty(
			"url",
			url.substring(0, x) + SAME_GROUP_FRIENDLY_URL + url.substring(y));
	}

	private static final Log _log =
		LogFactoryUtil.getLog(LayoutLarPersistenceImpl.class);

}

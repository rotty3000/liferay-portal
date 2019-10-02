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

package com.liferay.asset.vocabularies.selector.web.internal.portlet;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.asset.vocabularies.selector.web.internal.contants.AssetVocabulariesSelectorPortletKeys;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-asset-vocabularies-selector",
		"com.liferay.portlet.header-portlet-css=/css/tree.css",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Asset Vocabularies Selector",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + AssetVocabulariesSelectorPortletKeys.ASSET_VOCABULARIES_SELECTOR,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user"
	},
	service = Portlet.class
)
public class AssetVocabulariesSelectorPortlet extends MVCPortlet {

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		String resourceID = GetterUtil.getString(
			resourceRequest.getResourceID());

		if (resourceID.equals("getVocabularies")) {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long[] groupIds = getGroupIds(resourceRequest, themeDisplay);

			List<AssetVocabulary> vocabularies =
				_assetVocabularyService.getGroupsVocabularies(groupIds);

			for (AssetVocabulary vocabulary : vocabularies) {
				JSONObject jsonObject = JSONUtil.put(
					"icon", "vocabularies"
				).put(
					"id", vocabulary.getVocabularyId()
				).put(
					"key", vocabulary.getName()
				).put(
					"name", vocabulary.getTitle(themeDisplay.getLocale())
				);

				jsonArray.put(jsonObject);
			}

			writeJSON(resourceRequest, resourceResponse, jsonArray.toString());
		}
		else {
			super.serveResource(resourceRequest, resourceResponse);
		}
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			"assetVocabularyLocalService", _assetVocabularyLocalService);
		renderRequest.setAttribute(
			"assetVocabularyService", _assetVocabularyService);
		renderRequest.setAttribute("groupLocalService", _groupLocalService);
		renderRequest.setAttribute("html", _html);
		renderRequest.setAttribute("jsonFactory", _jsonFactory);
		renderRequest.setAttribute("language", _language);

		super.doDispatch(renderRequest, renderResponse);
	}

	private long[] getGroupIds(
		PortletRequest portletRequest, ThemeDisplay themeDisplay) {

		return ParamUtil.getLongValues(
			portletRequest, "groupIds",
			new long[] {
				themeDisplay.getCompanyGroupId(), themeDisplay.getScopeGroupId()
			});
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Html _html;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}
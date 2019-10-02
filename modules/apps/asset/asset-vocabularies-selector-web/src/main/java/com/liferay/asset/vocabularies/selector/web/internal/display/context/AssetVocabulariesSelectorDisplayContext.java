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

package com.liferay.asset.vocabularies.selector.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Raymond Augé
 */
public class AssetVocabulariesSelectorDisplayContext {

	public AssetVocabulariesSelectorDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse,
		HttpServletRequest httpServletRequest) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_httpServletRequest = httpServletRequest;
		_assetVocabularyService =
			(AssetVocabularyService)renderRequest.getAttribute(
				"assetVocabularyService");
		_assetVocabularyLocalService =
			(AssetVocabularyLocalService)renderRequest.getAttribute(
				"assetVocabularyLocalService");
		_groupLocalService = (GroupLocalService)renderRequest.getAttribute(
			"groupLocalService");
		_html = (Html)renderRequest.getAttribute("html");
		_jsonFactory = (JSONFactory)renderRequest.getAttribute("jsonFactory");
		_language = (Language)renderRequest.getAttribute("language");
	}

	public String getEventName() {
		if (Validator.isNotNull(_eventName)) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_renderResponse.getNamespace() + "selectVocabulary");

		return _eventName;
	}

	public String getSelectedVocabularies() {
		if (_selectedVocabularies != null) {
			return _selectedVocabularies;
		}

		_selectedVocabularies = ParamUtil.getString(
			_httpServletRequest, "selectedVocabularies");

		return _selectedVocabularies;
	}

	public JSONArray getVocabulariesJSONArray() throws Exception {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long[] groupIds = getGroupIds(themeDisplay);

		List<AssetVocabulary> vocabularies =
			_assetVocabularyService.getGroupsVocabularies(groupIds);

		for (AssetVocabulary vocabulary : vocabularies) {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			jsonObject.put(
				"icon", "vocabularies"
			).put(
				"id", vocabulary.getVocabularyId()
			).put(
				"key", vocabulary.getName()
			).put(
				"name", getVocabularyTitle(vocabulary.getVocabularyId())
			);

			if (getSelectedVocabularies().contains(
					String.valueOf(vocabulary.getVocabularyId()))) {

				jsonObject.put("selected", true);
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	public String getVocabularyTitle(long vocabularyId) throws PortalException {
		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(vocabularyId);

		StringBundler sb = new StringBundler(5);

		String title = assetVocabulary.getTitle(themeDisplay.getLocale());

		sb.append(_html.escape(title));

		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		if (assetVocabulary.getGroupId() == themeDisplay.getCompanyGroupId()) {
			sb.append(_language.get(_httpServletRequest, "global"));
		}
		else {
			Group group = _groupLocalService.fetchGroup(
				assetVocabulary.getGroupId());

			sb.append(group.getDescriptiveName(themeDisplay.getLocale()));
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb.toString();
	}

	private long[] getGroupIds(ThemeDisplay themeDisplay) {
		if (_groupIds != null) {
			return _groupIds;
		}

		return _groupIds = ParamUtil.getLongValues(
			_httpServletRequest, "groupIds",
			new long[] {
				themeDisplay.getCompanyGroupId(), themeDisplay.getScopeGroupId()
			});
	}

	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final AssetVocabularyService _assetVocabularyService;
	private String _eventName;
	private long[] _groupIds;
	private final GroupLocalService _groupLocalService;
	private final Html _html;
	private final HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory;
	private final Language _language;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private String _selectedVocabularies;

}
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

package com.liferay.portlet.journalcontent.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ServiceBeanMethodInvocationFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.journal.asset.JournalArticleAssetRenderer;
import com.liferay.portlet.journal.asset.JournalArticleAssetRendererFactory;
import com.liferay.portlet.journal.model.JournalArticle;

import java.lang.reflect.Method;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Douglas Wong
 * @author Raymond Augé
 */
public class ConfigurationActionImpl extends DefaultConfigurationAction {

	public ConfigurationActionImpl() {
		Method doProcessActionMethod = null;

		try {
			Class<?> clazz = getClass();

			doProcessActionMethod = clazz.getDeclaredMethod(
				"doProcessAction",
				new Class<?>[] {
					PortletConfig.class, ActionRequest.class,
					ActionResponse.class
				});
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		_doProcessActionMethod = doProcessActionMethod;
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String articleId = getArticleId(actionRequest);

		setPreference(actionRequest, "articleId", articleId);

		long articleGroupId = getArticleGroupId(actionRequest);

		setPreference(
			actionRequest, "articleGroupId", String.valueOf(articleGroupId));

		// This logic has to run in a transaction which we will invoke directly
		// since this is not a Spring bean

		ServiceBeanMethodInvocationFactoryUtil.proceed(
			this, ConfigurationActionImpl.class, _doProcessActionMethod,
			new Object[] {portletConfig, actionRequest, actionResponse},
			new String[] {"transactionAdvice"});
	}

	/**
	 * This method is invoked in a transaction because we may result in a
	 * persistence call before and/or after the call to super.processAction()
	 * which itself results in a persistence call.
	 */
	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRES_NEW,
		rollbackFor = {Exception.class}
	)
	protected void doProcessAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		PortletPreferences preferences = actionRequest.getPreferences();

		String articleId = getArticleId(actionRequest);

		String originalArticleId = preferences.getValue("articleId", null);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), portletResource);

		PortletLayoutListener portletLayoutListener =
			portlet.getPortletLayoutListenerInstance();

		if ((portletLayoutListener != null) &&
			Validator.isNotNull(originalArticleId) &&
			!originalArticleId.equals(articleId)) {

			// Results in a persistence call

			portletLayoutListener.onRemoveFromLayout(
				portletResource, layout.getPlid());
		}

		// Results in a persistence call

		super.processAction(portletConfig, actionRequest, actionResponse);

		if (SessionErrors.isEmpty(actionRequest) &&
			(portletLayoutListener != null)) {

			// Results in a persistence call

			portletLayoutListener.onAddToLayout(
				portletResource, layout.getPlid());
		}
	}

	protected long getArticleGroupId(PortletRequest portletRequest) {
		long assetEntryId = GetterUtil.getLong(
			getParameter(portletRequest, "assetEntryId"));

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			assetEntryId);

		return assetEntry.getGroupId();
	}

	protected String getArticleId(PortletRequest portletRequest)
		throws PortalException {

		long assetEntryId = GetterUtil.getLong(
			getParameter(portletRequest, "assetEntryId"));

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			assetEntryId);

		JournalArticleAssetRendererFactory articleAssetRendererFactory =
			(JournalArticleAssetRendererFactory)
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						JournalArticle.class.getName());

		JournalArticleAssetRenderer articleAssetRenderer =
			(JournalArticleAssetRenderer)articleAssetRendererFactory.
				getAssetRenderer(assetEntry.getClassPK());

		JournalArticle article = articleAssetRenderer.getArticle();

		return StringUtil.toUpperCase(article.getArticleId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationActionImpl.class);

	private final Method _doProcessActionMethod;

}
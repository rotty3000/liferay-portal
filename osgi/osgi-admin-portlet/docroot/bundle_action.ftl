<#--
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
-->

<#if (bundle??)>
	<#if (!headers?has_content)>
		<#assign headers = BundleUtil.getHeaders(bundle, themeDisplay.getLanguageId()) />

		<#assign bundleUpdateLocation = (headers[FrameworkConstants.BUNDLE_UPDATELOCATION]!bundle.getLocation()) />

		<#assign fragmentHost = headers[FrameworkConstants.FRAGMENT_HOST]! />
	</#if>

	<@liferay_ui["icon-menu"] cssClass="" showExpanded=(expandedView) showWhenSingleIcon=(expandedView)>
		<#if (permissionChecker.isOmniadmin())>
			<#if (!expandedView)>
				<@portlet["renderURL"] var="viewURL">
					<@portlet["param"] name="mvcPath" value="/edit_bundle.ftl" />
					<@portlet["param"] name="redirect" value=(currentURL) />
					<@portlet["param"] name="bundleId" value=(bundle.getBundleId()?string) />
				</@>

				<@liferay_ui["icon"] image="view" url=(viewURL) />
			</#if>

			<#if (bundle.getBundleId() != 0)>
				<#if (!fragmentHost?has_content)>

					<#assign taglibURL = "javascript:Liferay.OSGiAdmin.Util.start({bundleId:" + bundle.getBundleId() + ", message: '" + UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-start-this-bundle") + "', namespace: '" + renderResponse.getNamespace() + "'})" />
					<#assign cssClass = renderResponse.getNamespace() + "start_" + bundle.getBundleId() />

					<#if (bundle.getState() == Bundle.ACTIVE)>
						<#assign cssClass = "aui-helper-hidden " + renderResponse.getNamespace() + "start_" + bundle.getBundleId() />
					</#if>

					<@liferay_ui["icon"]
						cssClass=(cssClass)
						message="start"
						src=(themeDisplay.getPathThemeImages() + "/common/add.png")
						url=(taglibURL)
					/>

					<#assign taglibURL = "javascript:Liferay.OSGiAdmin.Util.stop({bundleId:" + bundle.getBundleId() + ", message: '" + UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-stop-this-bundle") + "', namespace: '" + renderResponse.getNamespace() + "'})" />
					<#assign cssClass = "aui-helper-hidden " + renderResponse.getNamespace() + "stop_" + bundle.getBundleId() />

					<#if (bundle.getState() == Bundle.ACTIVE)>
						<#assign cssClass = renderResponse.getNamespace() + "stop_" + bundle.getBundleId() />
					</#if>

					<@liferay_ui["icon"]
						cssClass=(cssClass)
						message="stop"
						src=(themeDisplay.getPathThemeImages() + "/application/close.png")
						url=(taglibURL)
					/>
				</#if>

				<#assign taglibURL = "javascript:" + renderResponse.getNamespace() + "uninstall('" + bundle.getBundleId() + "');" />

				<@liferay_ui["icon"]
					message="uninstall"
					src=(themeDisplay.getPathThemeImages() + "/common/delete.png")
					url=(taglibURL)
				/>

				<#if (bundleUpdateLocation?has_content)>
					<#assign taglibURL = "javascript:Liferay.OSGiAdmin.Util.update({bundleId:" + bundle.getBundleId() + ", message: '" + UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-update-this-bundle") + "', namespace: '" + renderResponse.getNamespace() + "'})" />

					<@liferay_ui["icon"]
						message="update"
						src=(themeDisplay.getPathThemeImages() + "/common/undo.png")
						url=(taglibURL)
					/>
				</#if>
			</#if>
		</#if>
	</@>
</#if>
<%--
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
--%>

<%@ taglib uri="/META-INF/c.tld" prefix="c" %>
<%@ taglib uri="/META-INF/aui.tld" prefix="aui" %>
<%@ taglib uri="/META-INF/liferay-portlet-ext.tld" prefix="liferay-portlet" %>
<%@ taglib uri="/META-INF/liferay-portlet_2_0.tld" prefix="portlet" %>
<%@ taglib uri="/META-INF/liferay-theme.tld" prefix="liferay-theme" %>
<%@ taglib uri="/META-INF/liferay-ui.tld" prefix="liferay-ui" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="com.liferay.portal.kernel.template.TemplateHandler" %><%@
page import="com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.StringBundler" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.model.Layout" %><%@
page import="com.liferay.portal.model.LayoutConstants" %><%@
page import="com.liferay.portal.model.LayoutSet" %><%@
page import="com.liferay.portal.security.permission.ActionKeys" %><%@
page import="com.liferay.portal.service.LayoutLocalServiceUtil" %><%@
page import="com.liferay.portal.service.permission.LayoutPermissionUtil" %><%@
page import="com.liferay.portal.theme.ThemeDisplay" %><%@
page import="com.liferay.portal.util.LayoutDescription" %><%@
page import="com.liferay.portal.util.LayoutListUtil" %><%@
page import="com.liferay.portal.util.PortalUtil" %><%@
page import="com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil" %><%@
page import="com.liferay.site.map.web.configuration.SiteMapConfiguration" %>

<%@ page import="java.util.List" %>

<liferay-theme:defineObjects />
<portlet:defineObjects />

<%
SiteMapConfiguration siteMapConfiguration = (SiteMapConfiguration)request.getAttribute(SiteMapConfiguration.class.getName());

String rootLayoutUuid = GetterUtil.getString(portletPreferences.getValue("rootLayoutUuid", siteMapConfiguration.getRootLayoutUuid()));
int displayDepth = GetterUtil.getInteger(portletPreferences.getValue("displayDepth", siteMapConfiguration.getDisplayDepth()));
String displayStyle = GetterUtil.getString(portletPreferences.getValue("displayStyle", siteMapConfiguration.getDisplayStyle()));
long displayStyleGroupId = GetterUtil.getLong(portletPreferences.getValue("displayStyleGroupId", siteMapConfiguration.getDisplayStyleGroupId()), themeDisplay.getScopeGroupId());
boolean includeRootInTree = GetterUtil.getBoolean(portletPreferences.getValue("includeRootInTree", siteMapConfiguration.includeRootInTree()));
boolean showCurrentPage = GetterUtil.getBoolean(portletPreferences.getValue("showCurrentPage", siteMapConfiguration.showCurrentPage()));
boolean useHtmlTitle = GetterUtil.getBoolean(portletPreferences.getValue("useHtmlTitle", siteMapConfiguration.useHtmlTitle()));
boolean showHiddenPages = GetterUtil.getBoolean(portletPreferences.getValue("showHiddenPages", siteMapConfiguration.showHiddenPages()));

Layout rootLayout = null;

long rootLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;

if (Validator.isNotNull(rootLayoutUuid)) {
	rootLayout = LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(rootLayoutUuid, scopeGroupId, layout.isPrivateLayout());

	if (rootLayout != null) {
		rootLayoutId = rootLayout.getLayoutId();
	}
}
else {
	includeRootInTree = false;
}

if (rootLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
	includeRootInTree = false;
}
%>

<%@ include file="/init-ext.jsp" %>
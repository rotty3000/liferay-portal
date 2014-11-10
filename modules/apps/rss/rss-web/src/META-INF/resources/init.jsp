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

<%@ taglib uri="/META-INF/aui.tld" prefix="aui" %>
<%@ taglib uri="/META-INF/c.tld" prefix="c" %>
<%@ taglib uri="/META-INF/liferay-portlet-ext.tld" prefix="liferay-portlet" %>
<%@ taglib uri="/META-INF/liferay-portlet_2_0.tld" prefix="portlet" %>
<%@ taglib uri="/META-INF/liferay-theme.tld" prefix="liferay-theme" %>
<%@ taglib uri="/META-INF/liferay-ui.tld" prefix="liferay-ui" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.sanitizer.Sanitizer" %><%@
page import="com.liferay.portal.kernel.sanitizer.SanitizerException" %><%@
page import="com.liferay.portal.kernel.sanitizer.SanitizerUtil" %><%@
page import="com.liferay.portal.kernel.search.BaseModelSearchResult" %><%@
page import="com.liferay.portal.kernel.search.Sort" %><%@
page import="com.liferay.portal.kernel.search.SortFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.ContentTypes" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Http" %><%@
page import="com.liferay.portal.kernel.util.HttpUtil" %><%@
page import="com.liferay.portal.kernel.util.ObjectValuePair" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PropsKeys" %><%@
page import="com.liferay.portal.kernel.util.PropsUtil" %><%@
page import="com.liferay.portal.kernel.util.StringBundler" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.security.permission.ResourceActionsUtil" %><%@
page import="com.liferay.portal.util.PortalUtil" %><%@
page import="com.liferay.portlet.journal.NoSuchArticleException" %><%@
page import="com.liferay.portlet.journal.action.EditArticleAction" %><%@
page import="com.liferay.portlet.journal.model.JournalArticle" %><%@
page import="com.liferay.portlet.journal.model.JournalArticleConstants" %><%@
page import="com.liferay.portlet.journal.search.ArticleSearch" %><%@
page import="com.liferay.portlet.journal.search.ArticleSearchTerms" %><%@
page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil" %><%@
page import="com.liferay.portlet.journal.service.JournalArticleServiceUtil" %><%@
page import="com.liferay.rss.web.configuration.RSSConfiguration" %><%@
page import="com.liferay.rss.web.util.RSSUtil" %><%@
page import="com.liferay.taglib.search.ResultRow" %>

<%@ page import="com.sun.syndication.feed.synd.SyndContent" %><%@
page import="com.sun.syndication.feed.synd.SyndEnclosure" %><%@
page import="com.sun.syndication.feed.synd.SyndEntry" %><%@
page import="com.sun.syndication.feed.synd.SyndFeed" %><%@
page import="com.sun.syndication.feed.synd.SyndImage" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %><%@
page import="java.util.Date" %><%@
page import="java.util.Enumeration" %><%@
page import="java.util.LinkedHashMap" %><%@
page import="java.util.List" %>

<%@ page import="javax.portlet.PortletURL" %><%@
page import="javax.portlet.ValidatorException" %><%@
page import="javax.portlet.WindowState" %>

<liferay-theme:defineObjects />
<portlet:defineObjects />

<%
WindowState windowState = liferayPortletRequest.getWindowState();

String portletResource = ParamUtil.getString(request, "portletResource");

RSSConfiguration rssConfiguration = (RSSConfiguration)request.getAttribute(RSSConfiguration.class.getName());

String[] urls = portletPreferences.getValues("urls", StringUtil.split(rssConfiguration.getUrls(), StringPool.COMMA));

String[] titles = portletPreferences.getValues("titles", StringUtil.split(rssConfiguration.getTitles(), StringPool.COMMA));
int entriesPerFeed = GetterUtil.getInteger(portletPreferences.getValue("entriesPerFeed", rssConfiguration.getEntriesPerFeed()));
int expandedEntriesPerFeed = GetterUtil.getInteger(portletPreferences.getValue("expandedEntriesPerFeed", rssConfiguration.getExpandedEntriesPerFeed()));
boolean showFeedTitle = GetterUtil.getBoolean(portletPreferences.getValue("showFeedTitle", rssConfiguration.showFeedTitle()));
boolean showFeedPublishedDate = GetterUtil.getBoolean(portletPreferences.getValue("showFeedPublishedDate", rssConfiguration.showFeedPublishedDate()));
boolean showFeedDescription = GetterUtil.getBoolean(portletPreferences.getValue("showFeedDescription", rssConfiguration.showFeedDescription()));
boolean showFeedImage = GetterUtil.getBoolean(portletPreferences.getValue("showFeedImage", rssConfiguration.showFeedImage()));
String feedImageAlignment = portletPreferences.getValue("feedImageAlignment", rssConfiguration.getFeedImageAlignment());
boolean showFeedItemAuthor = GetterUtil.getBoolean(portletPreferences.getValue("showFeedItemAuthor", rssConfiguration.showFeedItemAuthor()));

String[] headerArticleValues = portletPreferences.getValues("headerArticleValues", StringUtil.split(rssConfiguration.getHeaderArticleValues(), StringPool.COMMA));
long headerArticleGroupId = GetterUtil.getLong(headerArticleValues[0]);
String headerArticleId = headerArticleValues[1];

String[] footerArticleValues = portletPreferences.getValues("footerArticleValues", StringUtil.split(rssConfiguration.getFooterArticleValues(), StringPool.COMMA));
long footerArticleGroupId = GetterUtil.getLong(footerArticleValues[0]);
String footerArticleId = footerArticleValues[1];

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>
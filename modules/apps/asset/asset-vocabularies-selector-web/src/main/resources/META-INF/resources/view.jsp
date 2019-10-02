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

<%@ include file="/init.jsp" %>

<%
Map<String, Object> context = new HashMap<>();

context.put("itemSelectorSaveEvent", HtmlUtil.escapeJS(assetVocabulariesSelectorDisplayContext.getEventName()));
context.put("namespace", liferayPortletResponse.getNamespace());
context.put("nodes", assetVocabulariesSelectorDisplayContext.getVocabulariesJSONArray());
context.put("pathThemeImages", themeDisplay.getPathThemeImages());
context.put("viewType", "flat");
%>

<soy:component-renderer
	context="<%= context %>"
	module="js/SelectVocabulary.es"
	templateNamespace="com.liferay.asset.vocabularies.selector.web.SelectVocabulary.render"
/>
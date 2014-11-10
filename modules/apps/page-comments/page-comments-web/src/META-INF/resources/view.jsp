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

<% com.pfellwock.P.p("\n\n---------------------------------->1"); %>

asdfsfdsadfasdfasd

<c:if test="<%= LayoutPermissionUtil.contains(permissionChecker, layout, ActionKeys.VIEW) %>">
<% com.pfellwock.P.p("\n\n---------------------------------->2"); %>
	<portlet:actionURL name="/edit_page_discussion" var="discussionURL" />
<% com.pfellwock.P.p("\n\n---------------------------------->3"); %>
	<liferay-ui:discussion
		className="<%= Layout.class.getName() %>"
		classPK="<%= layout.getPlid() %>"
		formAction="<%= discussionURL %>"
		formName="fm"
		redirect="<%= currentURL %>"
		userId="<%= user.getUserId() %>"
	/>
	<% com.pfellwock.P.p("\n\n---------------------------------->4"); %>
</c:if>
<% com.pfellwock.P.p("\n\n---------------------------------->5"); %>
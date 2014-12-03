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
List<ListenerInfo> listeners = (List<ListenerInfo>)renderRequest.getAttribute("listeners");
List<IntegrationPoint> integrationPoints = (List<IntegrationPoint>)renderRequest.getAttribute("integrationPoints");
%>

<liferay-ui:search-container
	emptyResultsMessage="there-are-no-listeners"
>

	<liferay-ui:search-container-results results="<%= ListUtil.subList(integrationPoints, searchContainer.getStart(), searchContainer.getEnd()) %>" total="<%= integrationPoints.size() %>" />

	<liferay-ui:search-container-row
		className="com.liferay.osgi.diagnostics.model.IntegrationPoint"
		modelVar="integrationPoint"
	>

		<liferay-ui:search-container-column-text
			property="objectClass"
		/>

		<liferay-ui:search-container-column-text
			name="filters"
		>

			<ul>
				<%
				for (String filterString : integrationPoint.getFilters()) {
				%>
					<li><%= filterString %></li>
				<%
				}
				%>
			</ul>

		</liferay-ui:search-container-column-text>

	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>
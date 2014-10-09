<#--
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
-->

<#include "init.ftl">

<#assign ocdIterator = Request["ocdIterator"] />

<@liferay_ui["search-container"]
	emptyResultsMessage="no-services-were-found"
	iteratorURL=showAttributesURL
	total=ocdIterator.getTotal()
>

	<@liferay_ui["search-container-results"]
		results=ocdIterator.getResults(searchContainer.getStart(), searchContainer.getEnd())
	/>

	<@liferay_ui["search-container-row"]
		className="org.osgi.service.metatype.ObjectClassDefinition"
		keyProperty="ID"
		modelVar="ocd"
	>

		<@portlet["renderURL"] varImpl="editURL">
			<@portlet["param"] name="mvcPath" value="/edit_attributes.ftl" />
			<@portlet["param"] name="servicePID" value="${ocd.getID()}" />
		</@>

		<@liferay_ui["search-container-column-text"]
			href=editURL
			name="ID"
			value=ocd.getID()
		/>

		<@liferay_ui["search-container-column-text"]
			href=editURL
			name="name"
			value=ocd.getName()
		/>

		<@liferay_ui["search-container-column-text"]
			align="right"
			name=""
		>
			<@liferay_ui["icon-menu"]>
				<@liferay_ui["icon"]
					image="edit"
					label=true
					message="edit-attributes"
					method="post"
					url="${editURL}"
				/>
			</@>
		</@>
	</@>

	<@liferay_ui["search-iterator"] />
</@>
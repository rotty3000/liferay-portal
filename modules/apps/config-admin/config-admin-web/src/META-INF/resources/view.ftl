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
	total=ocdIterator.getTotal()
>

	<@liferay_ui["search-container-results"]
		results=ocdIterator.getResults(searchContainer.getStart(), searchContainer.getEnd())
	/>

	<@liferay_ui["search-container-row"]
		className="com.liferay.osgi.config.admin.util.ConfigurableService"
		keyProperty="pid"
		modelVar="ocd"
	>

		<@portlet["renderURL"] varImpl="editURL">
			<@portlet["param"] name="mvcPath" value="/edit_attributes.ftl" />
			<@portlet["param"] name="servicePID" value="${ocd.getPid()}" />
			<@portlet["param"] name="factoryPID" value="${ocd.getFactoryPid()}" />
		</@>

		<@portlet["renderURL"] varImpl="showConfigURL">
			<@portlet["param"] name="mvcPath" value="/view.ftl" />
			<@portlet["param"] name="servicePID" value="${ocd.getPid()}" />
			<@portlet["param"] name="factoryPID" value="${ocd.getFactoryPid()}" />
			<@portlet["param"] name="viewType" value="factoryInstances" />
		</@>

		<#if ocd.isFactory()>

			<@liferay_ui["search-container-column-text"]
				href=showConfigURL
				name="name"
				value=ocd.getName()
			/>
		<#else>
			<@liferay_ui["search-container-column-text"]
				href=editURL
				name="name"
				value=ocd.getName()	/>
		</#if>

		<@liferay_ui["search-container-column-text"]
			align="right"
			name="">

			<@liferay_ui["icon-menu"]>

				<#if ocd.isFactory()>

					<@portlet["renderURL"] varImpl="creatFactoryConfigURL">
						<@portlet["param"] name="mvcPath" value="/edit_attributes.ftl" />
						<@portlet["param"] name="factoryPID" value="${ocd.getPid()}" />
					</@>

					<@liferay_ui["icon"]
						image="add"
						label=true
						message="create-configuration"
						method="post"
						url="${creatFactoryConfigURL}"
					/>
				</#if>
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
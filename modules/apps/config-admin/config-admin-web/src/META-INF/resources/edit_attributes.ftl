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

<#assign ddmFormBuilder = Request["ddmFormBuilder"] />
<#assign editingHeaderTitle = Request["editingHeaderTitle"] />
<#assign servicePID = Request["servicePID"] />

<#assign redirectURL = renderResponse.createRenderURL() />

<@liferay_ui["header"]
	backURL="${redirectURL}"
	title='${editingHeaderTitle}'
/>

<@aui["form"] method="post" name="fmOCDAttribute">
	<@aui["input"] name="redirect" type="hidden" value="${redirectURL}" />
	<@aui["input"] name="servicePID" type="hidden" value="${servicePID}" />

	<@aui["fieldset"]>
		${ddmFormBuilder.renderServiceConfigurationForm(servicePID, renderRequest, renderResponse)}
	</@>

	<@aui["button-row"]>
		<@aui["button"] type="submit" />
		<@aui["button"] href="${redirectURL}" type="cancel" />
	</@>
</@>
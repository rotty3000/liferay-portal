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

<#assign configurationFormBuilder = Request["configurationFormBuilder"] />
<#assign editingHeaderTitle = Request["editingHeaderTitle"] />
<#assign pid = Request["servicePID"] />
<#assign factoryPid = Request["factoryPID"] />

<#assign redirectURL = renderResponse.createRenderURL() />

<@portlet["actionURL"] name="bindConfiguration" varImpl="bindConfigActionURL"/>
<@portlet["actionURL"] name="deleteConfiguration" varImpl="deleteConfigActionURL"/>

<@liferay_ui["header"]
	backURL="${redirectURL}"
	title='${editingHeaderTitle}'
/>

<#assign jsFormSubmit = "event.preventDefault();" + renderResponse.getNamespace() + "setDDMFieldNamespaceAndSubmit();">

<@aui["form"] method="post" name="fm" action="${bindConfigActionURL}" onSubmit=jsFormSubmit>

	<@aui["fieldset"]>
		${configurationFormBuilder.renderServiceConfigurationForm(pid, renderRequest, renderResponse)}
	</@>

	<#assign configFieldJSON = Request["configFieldJSON"] />
	<#assign scopeGroupId = Request["scopeGroupId"] />
	<#assign plId = Request["plId"] />

	<div class="lfr-ddm-container" id="xyz">

		<@aui["input"] name="configFields" type="hidden" />

		<@aui["script"] use="liferay-ddm-form">

			var fromGroupDiv = A.one('div.form-group.field-wrapper');

			var fieldNSValue = fromGroupDiv.getData('fieldnamespace');

			var isRepeatable = true;

			new Liferay.DDM.Form(
				{
					container: '#xyz',
					ddmFormValuesInput: '#<@portlet["namespace"]/>configFields',
					definition: "${configFieldJSON?js_string}",
					doAsGroupId: ${scopeGroupId},
					fieldsNamespace: fieldNSValue,
					p_l_id: ${plId},
					portletNamespace: "<@portlet["namespace"]/>",
					repeatable: isRepeatable
				}
			);
		</@>
	</div>

	<@aui["input"] name="redirect" type="hidden" value="${redirectURL}" />

	<#if pid??>
	<@aui["input"] name="pid" type="hidden" value="${pid}" />
	</#if>

	<#if factoryPid??>
	<@aui["input"] name="factoryPid" type="hidden" value="${factoryPid}" />
	</#if>


	<@aui["input"] name="fieldNamespace" type="hidden" value="" />


	<@aui["button-row"]>
		<#assign bindAttributesOnClickValue = renderResponse.getNamespace() + "bindConfig();">
		<@aui["button"] onClick=bindAttributesOnClickValue value="save" type="submit" />

		<#assign deleteAttributesOnClickValue = renderResponse.getNamespace() + "deleteConfig();">
		<@aui["button"] onClick=deleteAttributesOnClickValue value="delete" type="button" />

		<@aui["button"] type="reset" />
		<@aui["button"] href="${redirectURL}" type="cancel" />
	</@>
</@>


<@aui["script"] use="aui-base">
	Liferay.provide(
		window,
		'<@portlet["namespace"] />deleteConfig',
		function() {
			var actionURL = "${deleteConfigActionURL?js_string}";
			<@portlet["namespace"] />setDDMFieldNamespaceAndSubmit(actionURL);
		},
		['liferay-util-list-fields']
	);

	//Set the DDMFormField Namespace and submit the form

	<@portlet["namespace"] />setDDMFieldNamespaceAndSubmit = function(actionURL){

		var fromGroupDiv = A.one('div.form-group.field-wrapper');

		var fieldNSValue = fromGroupDiv.getData('fieldnamespace');

		document.<@portlet["namespace"] />fm.<@portlet["namespace"] />fieldNamespace.value = fieldNSValue;

		if(fieldNSValue){
			if(actionURL){
				document.<@portlet["namespace"] />fm.action=actionURL;
			}
			submitForm(document.<@portlet["namespace"] />fm);
		}
	}

</@>
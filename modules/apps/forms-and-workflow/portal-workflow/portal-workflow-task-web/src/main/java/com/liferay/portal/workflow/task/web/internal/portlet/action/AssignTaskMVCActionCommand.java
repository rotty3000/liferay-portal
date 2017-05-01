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

package com.liferay.portal.workflow.task.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.workflow.task.web.internal.permission.WorkflowTaskPermissionChecker;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK,
		"mvc.command.name=assignWorkflowTask"
	},
	service = MVCActionCommand.class
)
public class AssignTaskMVCActionCommand
	extends WorkflowTaskBaseMVCActionCommand {

	protected void checkWorkflowTaskAssignmentPermission(
			long workflowTaskId, ThemeDisplay themeDisplay)
		throws Exception {

		WorkflowTask workflowTask = workflowTaskManager.getWorkflowTask(
			themeDisplay.getCompanyId(), workflowTaskId);

		long groupId = MapUtil.getLong(
			workflowTask.getOptionalAttributes(), "groupId",
			themeDisplay.getSiteGroupId());

		if (!_workflowTaskPermissionChecker.hasPermission(
				groupId, workflowTask, themeDisplay.getPermissionChecker())) {

			throw new PrincipalException(
				String.format(
					"User %d does not have permission to assign task %d",
					themeDisplay.getUserId(), workflowTaskId));
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long workflowTaskId = ParamUtil.getLong(
			actionRequest, "workflowTaskId");

		long assigneeUserId = ParamUtil.getLong(
			actionRequest, "assigneeUserId");
		String comment = ParamUtil.getString(actionRequest, "comment");

		checkWorkflowTaskAssignmentPermission(workflowTaskId, themeDisplay);

		workflowTaskManager.assignWorkflowTaskToUser(
			themeDisplay.getCompanyId(), themeDisplay.getUserId(),
			workflowTaskId, assigneeUserId, comment, null, null);
	}

	@Reference
	protected WorkflowTaskManager workflowTaskManager;

	private final WorkflowTaskPermissionChecker _workflowTaskPermissionChecker =
		new WorkflowTaskPermissionChecker();

}
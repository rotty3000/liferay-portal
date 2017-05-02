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

package com.liferay.portal.workflow.kaleo.runtime.integration.impl.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Inácio Nery
 */
@RunWith(Arquillian.class)
@Sync
public class WorkflowTaskManagerImplTest
	extends BaseWorkflowTaskManagerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testApproveJoinXorWorkflow() throws Exception {
		createJoinXorWorkflow();

		activateWorkflow(BlogsEntry.class.getName(), 0, 0, "Join Xor", 1);

		BlogsEntry blogsEntry = addBlogsEntry();

		assignWorkflowTaskToUser(siteAdminUser, siteAdminUser, "task1");

		completeWorkflowTask(siteAdminUser, "join-xor", "task1");

		WorkflowTask workflowTask2 = getWorkflowTask("task2", true);

		Assert.assertTrue(workflowTask2.isCompleted());

		blogsEntry = BlogsEntryLocalServiceUtil.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testApproveJournalArticleAsAdmin() throws Exception {
		activateSingleApproverWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalArticle article = addJournalArticle(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(adminUser, adminUser);

		completeWorkflowTask(adminUser, Constants.APPROVE);

		checkWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = JournalArticleLocalServiceUtil.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveJournalArticleInFolderInheritedWorkflow()
		throws Exception {

		activateSingleApproverWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalFolder folder = addJournalFolder();

		JournalArticle article = addJournalArticle(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(adminUser, adminUser);

		completeWorkflowTask(adminUser, Constants.APPROVE);

		checkWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = JournalArticleLocalServiceUtil.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		deactivateWorkflow(
			JournalFolder.class.getName(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveJournalArticleInFolderStructureSpecificWorkflow()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName());

		JournalFolder folder = addJournalFolder(
			ddmStructure.getStructureId(),
			JournalFolderConstants.
				RESTRICTION_TYPE_DDM_STRUCTURES_AND_WORKFLOW);

		activateSingleApproverWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			ddmStructure.getStructureId());

		JournalArticle article = addJournalArticle(
			folder.getFolderId(), ddmStructure);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(adminUser, adminUser);

		completeWorkflowTask(adminUser, Constants.APPROVE);

		checkWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = JournalArticleLocalServiceUtil.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		deactivateWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			ddmStructure.getStructureId());
	}

	@Test
	public void testApproveJournalArticleUsingFolderSpecificWorkflow()
		throws Exception {

		JournalFolder folder = addJournalFolder(
			0, JournalFolderConstants.RESTRICTION_TYPE_WORKFLOW);

		activateSingleApproverWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);

		JournalArticle article = addJournalArticle(folder.getFolderId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, article.getStatus());

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(adminUser, adminUser);

		completeWorkflowTask(adminUser, Constants.APPROVE);

		checkWorkflowInstance(JournalArticle.class.getName(), article.getId());

		article = JournalArticleLocalServiceUtil.getArticle(article.getId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, article.getStatus());

		deactivateWorkflow(
			JournalFolder.class.getName(), folder.getFolderId(),
			JournalArticleConstants.DDM_STRUCTURE_ID_ALL);
	}

	@Test
	public void testApproveWorkflowBlogsEntryAsSiteAdmin() throws Exception {
		activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = addBlogsEntry();

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(siteAdminUser, siteAdminUser);

		completeWorkflowTask(siteAdminUser, Constants.APPROVE);

		blogsEntry = BlogsEntryLocalServiceUtil.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testApproveWorkflowDDLRecordAsAdmin() throws Exception {
		DDLRecordSet recordSet = addRecordSet();

		activateSingleApproverWorkflow(
			DDLRecordSet.class.getName(), recordSet.getRecordSetId(), 0);

		DDLRecord record = addRecord(recordSet);

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(adminUser, adminUser);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, record.getStatus());

		completeWorkflowTask(adminUser, Constants.APPROVE);

		DDLRecordVersion recordVersion = record.getRecordVersion();

		checkWorkflowInstance(
			DDLRecord.class.getName(), recordVersion.getRecordVersionId());

		record = DDLRecordLocalServiceUtil.getRecord(record.getRecordId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, record.getStatus());

		deactivateWorkflow(
			DDLRecordSet.class.getName(), recordSet.getRecordSetId(), 0);
	}

	@Test
	public void testAssignApproveWorkflowBlogsEntryAsPortalContentReviewer()
		throws Exception {

		activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = addBlogsEntry();

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(portalContentReviewerUser, adminUser);

		checkUserNotificationEventsByUsers(adminUser);

		assignWorkflowTaskToUser(adminUser, portalContentReviewerUser);

		checkUserNotificationEventsByUsers(portalContentReviewerUser);

		completeWorkflowTask(portalContentReviewerUser, Constants.APPROVE);

		blogsEntry = BlogsEntryLocalServiceUtil.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, blogsEntry.getStatus());

		deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

	@Test
	public void testRejectWorkflowBlogsEntryAndViewAssignee() throws Exception {
		activateSingleApproverWorkflow(BlogsEntry.class.getName(), 0, 0);

		BlogsEntry blogsEntry = addBlogsEntry();

		checkUserNotificationEventsByUsers(
			adminUser, portalContentReviewerUser, siteAdminUser);

		assignWorkflowTaskToUser(adminUser, portalContentReviewerUser);

		checkUserNotificationEventsByUsers(portalContentReviewerUser);

		completeWorkflowTask(portalContentReviewerUser, Constants.REJECT);

		checkUserNotificationEventsByUsers(adminUser);

		blogsEntry = BlogsEntryLocalServiceUtil.getBlogsEntry(
			blogsEntry.getEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, blogsEntry.getStatus());

		WorkflowTask workflowTask = getWorkflowTask();

		Assert.assertEquals(
			adminUser.getUserId(), workflowTask.getAssigneeUserId());

		deactivateWorkflow(BlogsEntry.class.getName(), 0, 0);
	}

}
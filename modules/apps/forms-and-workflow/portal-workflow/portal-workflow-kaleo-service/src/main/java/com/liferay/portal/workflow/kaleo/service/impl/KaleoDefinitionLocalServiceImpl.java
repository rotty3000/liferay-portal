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

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.base.KaleoDefinitionLocalServiceBaseImpl;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class KaleoDefinitionLocalServiceImpl
	extends KaleoDefinitionLocalServiceBaseImpl {

	@Override
	public void activateKaleoDefinition(
			long kaleoDefinitionId, long startKaleoNodeId,
			ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByPrimaryKey(kaleoDefinitionId);

		deactivatePreviousKaleoDefinition(
			kaleoDefinition.getName(), serviceContext);

		kaleoDefinition.setStartKaleoNodeId(startKaleoNodeId);
		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(true);

		kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Override
	public void activateKaleoDefinition(
			long kaleoDefinitionId, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByPrimaryKey(kaleoDefinitionId);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(true);

		kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Override
	public void activateKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByC_N_V(
				serviceContext.getCompanyId(), name, version);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(true);

		kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Override
	public KaleoDefinition addKaleoDefinition(
			String name, String title, String description, String content,
			int version, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		User user = userLocalService.getUser(serviceContext.getGuestOrUserId());
		Date now = new Date();

		long kaleoDefinitionId = counterLocalService.increment();

		KaleoDefinition kaleoDefinition = kaleoDefinitionPersistence.create(
			kaleoDefinitionId);

		long groupId = StagingUtil.getLiveGroupId(
			serviceContext.getScopeGroupId());

		kaleoDefinition.setGroupId(groupId);

		kaleoDefinition.setCompanyId(user.getCompanyId());
		kaleoDefinition.setUserId(user.getUserId());
		kaleoDefinition.setUserName(user.getFullName());
		kaleoDefinition.setCreateDate(now);
		kaleoDefinition.setModifiedDate(now);
		kaleoDefinition.setName(name);
		kaleoDefinition.setTitle(title);
		kaleoDefinition.setDescription(description);
		kaleoDefinition.setContent(content);
		kaleoDefinition.setVersion(version);
		kaleoDefinition.setActive(false);

		kaleoDefinitionPersistence.update(kaleoDefinition);

		// Kaleo definition version

		kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
			name, title, description, content, getVersion(version),
			serviceContext);

		return kaleoDefinition;
	}

	@Override
	public void deactivateKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByC_N_V(
				serviceContext.getCompanyId(), name, version);

		kaleoDefinition.setModifiedDate(new Date());
		kaleoDefinition.setActive(false);

		kaleoDefinitionPersistence.update(kaleoDefinition);
	}

	@Override
	public void deleteCompanyKaleoDefinitions(long companyId) {

		// Kaleo definitions

		kaleoDefinitionPersistence.removeByCompanyId(companyId);

		// Kaleo definition version

		kaleoDefinitionVersionPersistence.removeByCompanyId(companyId);

		// Kaleo condition

		kaleoConditionLocalService.deleteCompanyKaleoConditions(companyId);

		// Kaleo instances

		kaleoInstanceLocalService.deleteCompanyKaleoInstances(companyId);

		// Kaleo nodes

		kaleoNodeLocalService.deleteCompanyKaleoNodes(companyId);

		// Kaleo tasks

		kaleoTaskLocalService.deleteCompanyKaleoTasks(companyId);

		// Kaleo transitions

		kaleoTransitionLocalService.deleteCompanyKaleoTransitions(companyId);
	}

	@Override
	public void deleteKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition = getKaleoDefinition(
			name, version, serviceContext);

		if (kaleoDefinition.isActive()) {
			throw new WorkflowException(
				"Cannot delete active workflow definition " +
					kaleoDefinition.getKaleoDefinitionId());
		}

		if (kaleoDefinition.hasIncompleteKaleoInstances()) {
			throw new WorkflowException(
				"Cannot delete incomplete workflow definition " +
					kaleoDefinition.getKaleoDefinitionId());
		}

		kaleoDefinitionPersistence.remove(kaleoDefinition);

		// Kaleo definition version

		List<KaleoDefinitionVersion> kaleoDefinitionVersions =
			kaleoDefinitionVersionPersistence.findByC_N(
				serviceContext.getCompanyId(), name);

		for (KaleoDefinitionVersion kaleoDefinitionVersion :
				kaleoDefinitionVersions) {

			BigDecimal kaleoDefinitionVersionBigDecimal = new BigDecimal(
				kaleoDefinitionVersion.getVersion());

			int value1 = kaleoDefinitionVersionBigDecimal.compareTo(
				new BigDecimal(version));
			int value2 = kaleoDefinitionVersionBigDecimal.compareTo(
				new BigDecimal(version + 1));

			if ((value1 >= 0) && (value2 < 0)) {
				kaleoDefinitionVersionPersistence.remove(
					kaleoDefinitionVersion);
			}
		}

		// Kaleo condition

		kaleoConditionLocalService.deleteKaleoDefinitionKaleoCondition(
			kaleoDefinition.getKaleoDefinitionId());

		// Kaleo instances

		kaleoInstanceLocalService.deleteKaleoDefinitionKaleoInstances(
			kaleoDefinition.getKaleoDefinitionId());

		// Kaleo nodes

		kaleoNodeLocalService.deleteKaleoDefinitionKaleoNodes(
			kaleoDefinition.getKaleoDefinitionId());

		// Kaleo tasks

		kaleoTaskLocalService.deleteKaleoDefinitionKaleoTasks(
			kaleoDefinition.getKaleoDefinitionId());

		// Kaleo transitions

		kaleoTransitionLocalService.deleteKaleoDefinitionKaleoTransitions(
			kaleoDefinition.getKaleoDefinitionId());
	}

	@Override
	public KaleoDefinition fetchLatestKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException {

		List<KaleoDefinition> kaleoDefinitions =
			kaleoDefinitionPersistence.findByC_N(
				serviceContext.getCompanyId(), name, 0, 1);

		if (kaleoDefinitions.isEmpty()) {
			return null;
		}

		return kaleoDefinitions.get(0);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String name, int version, ServiceContext serviceContext)
		throws PortalException {

		return kaleoDefinitionPersistence.findByC_N_V(
			serviceContext.getCompanyId(), name, version);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByC_A(
			serviceContext.getCompanyId(), active, start, end,
			orderByComparator);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByCompanyId(
			serviceContext.getCompanyId(), start, end, orderByComparator);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		String name, boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByC_N_A(
			serviceContext.getCompanyId(), name, active, start, end,
			orderByComparator);
	}

	@Override
	public List<KaleoDefinition> getKaleoDefinitions(
		String name, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.findByC_N(
			serviceContext.getCompanyId(), name, start, end, orderByComparator);
	}

	@Override
	public int getKaleoDefinitionsCount(
		boolean active, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_A(
			serviceContext.getCompanyId(), active);
	}

	@Override
	public int getKaleoDefinitionsCount(ServiceContext serviceContext) {
		return kaleoDefinitionPersistence.countByCompanyId(
			serviceContext.getCompanyId());
	}

	@Override
	public int getKaleoDefinitionsCount(
		String name, boolean active, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_N_A(
			serviceContext.getCompanyId(), name, active);
	}

	@Override
	public int getKaleoDefinitionsCount(
		String name, ServiceContext serviceContext) {

		return kaleoDefinitionPersistence.countByC_N(
			serviceContext.getCompanyId(), name);
	}

	@Override
	public KaleoDefinition getLatestKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition = fetchLatestKaleoDefinition(
			name, serviceContext);

		if (kaleoDefinition == null) {
			throw new NoSuchDefinitionException();
		}

		return kaleoDefinition;
	}

	@Override
	public KaleoDefinition incrementKaleoDefinition(
			Definition definition, String title, ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition = getLatestKaleoDefinition(
			definition.getName(), serviceContext);

		return addKaleoDefinition(
			definition.getName(), title, definition.getDescription(),
			definition.getContent(), kaleoDefinition.getVersion() + 1,
			serviceContext);
	}

	@Override
	public KaleoDefinition updateTitle(
			String name, int version, String title,
			ServiceContext serviceContext)
		throws PortalException {

		// Kaleo definition

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionPersistence.findByC_N_V(
				serviceContext.getCompanyId(), name, version);

		kaleoDefinition.setTitle(title);

		kaleoDefinitionPersistence.update(kaleoDefinition);

		// Kaleo definition version

		kaleoDefinitionVersionLocalService.updateKaleoDefinitionVersionTitle(
			serviceContext.getCompanyId(), name, getVersion(version), title);

		return kaleoDefinition;
	}

	protected void deactivatePreviousKaleoDefinition(
			String name, ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition previousKaleoDefinition = fetchLatestKaleoDefinition(
			name, serviceContext);

		if (previousKaleoDefinition == null) {
			return;
		}

		previousKaleoDefinition.setModifiedDate(new Date());
		previousKaleoDefinition.setActive(false);

		kaleoDefinitionPersistence.update(previousKaleoDefinition);
	}

	protected String getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

}
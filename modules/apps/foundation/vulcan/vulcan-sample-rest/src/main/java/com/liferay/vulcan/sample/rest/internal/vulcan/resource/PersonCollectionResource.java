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

package com.liferay.vulcan.sample.rest.internal.vulcan.resource;

import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.contributor.APIContributor;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.CollectionResource;
import com.liferay.vulcan.resource.SingleResource;

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class PersonCollectionResource
	implements APIContributor, CollectionResource<User> {

	@Override
	public SingleResource<User> getCollectionItemSingleResource(String id) {
		long userId = GetterUtil.getLong(id);

		if (userId == GetterUtil.DEFAULT_LONG) {
			throw new BadRequestException();
		}

		try {
			User user = _userService.getUserById(userId);

			return new PersonSingleResource(user);
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException();
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Override
	public Page<User> getPage(Pagination paginationParams) {
		try {
			List<User> users = _userService.getCompanyUsers(
				company.getCompanyId(), paginationParams.getStartPosition(),
				paginationParams.getEndPosition());
			int count = _userService.getCompanyUsersCount(
				company.getCompanyId());

			return paginationParams.createPage(users, count);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Override
	public String getPath() {
		return "people";
	}

	@Context
	protected Company company;

	@Reference
	private UserService _userService;

}
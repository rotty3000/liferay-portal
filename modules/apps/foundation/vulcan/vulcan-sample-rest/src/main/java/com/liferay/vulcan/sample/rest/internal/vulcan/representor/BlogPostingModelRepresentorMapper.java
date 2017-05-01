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

package com.liferay.vulcan.sample.rest.internal.vulcan.representor;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.representor.builder.RepresentorBuilder;

import java.text.DateFormat;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true,
	service = {
		BlogPostingModelRepresentorMapper.class, ModelRepresentorMapper.class
	}
)
public class BlogPostingModelRepresentorMapper
	implements ModelRepresentorMapper<BlogsEntry> {

	@Override
	public void buildRepresentor(
		RepresentorBuilder<BlogsEntry> representorBuilder) {

		RepresentorBuilder.FirstStep<BlogsEntry> firstStep =
			representorBuilder.getFirstStep(
				blogsEntry -> String.valueOf(blogsEntry.getEntryId()));

		firstStep.addEmbedded("author", User.class, this::_getUserOptional);
		firstStep.addEmbedded("creator", User.class, this::_getUserOptional);
		firstStep.addField("alternativeHeadline", BlogsEntry::getSubtitle);
		firstStep.addField("articleBody", BlogsEntry::getContent);

		Function<Date, Object> formatFunction = date -> {
			if (date == null) {
				return null;
			}

			DateFormat dateFormat = DateUtil.getISO8601Format();

			return dateFormat.format(date);
		};

		firstStep.addField(
			"createDate",
			blogsEntry -> formatFunction.apply(blogsEntry.getCreateDate()));

		firstStep.addField("fileFormat", blogsEntry -> "text/html");
		firstStep.addField("headline", BlogsEntry::getTitle);
		firstStep.addField(
			"modifiedDate",
			blogsEntry -> formatFunction.apply(blogsEntry.getModifiedDate()));
		firstStep.addField(
			"publishedDate",
			blogsEntry -> formatFunction.apply(
				blogsEntry.getLastPublishDate()));
		firstStep.addType("BlogPosting");
	}

	private Optional<User> _getUserOptional(BlogsEntry blogsEntry) {
		try {
			return Optional.ofNullable(
				_userService.getUserById(blogsEntry.getUserId()));
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException();
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private UserService _userService;

}
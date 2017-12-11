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

package com.liferay.adaptive.media.document.library.thumbnails.internal.service;

import com.liferay.adaptive.media.document.library.thumbnails.internal.util.AMCompanyThumbnailConfigurationInitializer;
import com.liferay.adaptive.media.exception.AMImageConfigurationException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(immediate = true, service = ServiceWrapper.class)
public class AMThumbnailsCompanyLocalServiceWrapper
	extends CompanyLocalServiceWrapper {

	public AMThumbnailsCompanyLocalServiceWrapper() {
		super(null);
	}

	public AMThumbnailsCompanyLocalServiceWrapper(
		CompanyLocalService companyLocalService) {

		super(companyLocalService);
	}

	@Override
	public Company addCompany(
			String webId, String virtualHostname, String mx, boolean system,
			int maxUsers, boolean active)
		throws PortalException {

		Company company = super.addCompany(
			webId, virtualHostname, mx, system, maxUsers, active);

		try {
			_amCompanyThumbnailConfigurationInitializer.initializeCompany(
				company);
		}
		catch (AMImageConfigurationException | IOException e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to automatically create Adaptive Media thumbnail " +
						"configurations",
					e);
			}
		}

		return company;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AMThumbnailsCompanyLocalServiceWrapper.class);

	@Reference
	private AMCompanyThumbnailConfigurationInitializer
		_amCompanyThumbnailConfigurationInitializer;

}
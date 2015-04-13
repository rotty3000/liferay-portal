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

package com.liferay.portal.ldap.configuration;

import static com.liferay.portal.configuration.PKConfigurationConstants.COMPANY_ID;
import static com.liferay.portal.configuration.ValidationResult.Type.ERROR;
import static com.liferay.portal.configuration.ValidationResult.Type.WARNINIG;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.NoSuchCompanyException;
import com.liferay.portal.configuration.PKConfigurationConstants;
import com.liferay.portal.configuration.PKConfigurationValidator;
import com.liferay.portal.configuration.ValidationResult;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;

/**
 * @author Milen Dyankov
 */
@Component(immediate = true, service = LDAPIntegrationConfigurationValidator.class)
public class LDAPIntegrationConfigurationValidator extends PKConfigurationValidator<LDAPIntegrationConfigurationInstance> {

	public List<ValidationResult> check(LDAPIntegrationConfigurationInstance configuration)
		throws PortalException {

		List<ValidationResult> result = checkPK(configuration);
		if (result.isEmpty()) {
			checkCompanyId(configuration, result);
		}
		return result;
	}

	protected void checkCompanyId(LDAPIntegrationConfigurationInstance configuration, List<ValidationResult> result)
		throws PortalException {

		int companyId = GetterUtil.get(configuration.getKey(PKConfigurationConstants.COMPANY_ID), -1);
		if (companyId < 0) {
			result.add(new ValidationResult(ERROR, "Invalid value for '" + COMPANY_ID + "' property !"));
		}
		else {
			try {
				CompanyLocalServiceUtil.getCompanyById(companyId);
			}
			catch (NoSuchCompanyException e) {
				result.add(new ValidationResult(WARNINIG, "Company with id " + companyId + " does not exists!"));
			}
		}
	}

}

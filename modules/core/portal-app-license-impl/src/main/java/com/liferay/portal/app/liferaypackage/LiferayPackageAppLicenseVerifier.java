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

package com.liferay.portal.app.liferaypackage;

import com.liferay.portal.app.license.AppLicenseVerifier;
import com.liferay.portal.app.liferaypackage.servlet.filters.LiferayPackageFilter;
import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.deploy.hot.LiferayPackageHotDeployException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Amos Fong
 */
@Component(
	immediate = true,
	property = {"version=2.3.1"},
	service = AppLicenseVerifier.class
)
public class LiferayPackageAppLicenseVerifier implements AppLicenseVerifier {

	@Activate
	protected void activate(Map<String, Object> properties) {
		System.out.println("\n\n\n\n\n\n\n##### ACTIAVTE");
		System.out.println("##### ACTIAVTE");
		System.out.println("##### ACTIAVTE\n\n\n\n\n\n");
		System.out.println("#" + AppLicenseVerifier.class.getClassLoader());
	}

	public void destroy() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Override
	public boolean verify(String productId, String productType, String productVersion) {
		try {
			System.out.println("##### VERIFYING: " + productId + " " + productType + " " + productVersion);
				
			//_verify(); [redacted]

			// registerFilter(); [redacted]
		}
		catch (Exception e) {
			//log e
		}

		return false; // test
	}

	private ServiceRegistration<Filter> _serviceRegistration;

}
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

package com.liferay.portal.configuration;

import static com.liferay.portal.configuration.ValidationResult.Type.ERROR;

import java.util.LinkedList;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;


/**
 * 
 * @author Milen Dyankov
 *
 */

public abstract class PKConfigurationValidator<T extends PKConfiguration>  {

	public abstract List<ValidationResult> check (T configuration) throws PortalException;

	public List<ValidationResult> checkPK (T configuration) throws PortalException {
		List<ValidationResult> result = new LinkedList<ValidationResult>();
		if (!configuration.isValid()) {
			result.add(new ValidationResult(ERROR, "Configuration does not have values for all required primary key fields! Please make sure the following properties are set " + configuration.getKeys()));
		}
		return result;
	}

}

/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.cas;

import com.liferay.portal.kernel.cas.CasService;
import com.liferay.portal.kernel.cas.LiferayCasPrincipal;
import com.liferay.portal.kernel.cas.exception.CasNotAvailableException;

import javax.portlet.PortletSession;

/**
 * @author Carlos Sierra Andrés
 */
public class CasServiceImpl implements CasService {


	@Override
	public LiferayCasPrincipal getLiferayCasPrincipal(
			PortletSession portletSession)
		throws CasNotAvailableException {

		LiferayCasPrincipal liferayCasPrincipal =
			(LiferayCasPrincipal)portletSession.getAttribute(
				CasService.CAS_PRINCIPAL_SESSION_KEY,
				PortletSession.APPLICATION_SCOPE);

		if (liferayCasPrincipal == null) {
			throw new CasNotAvailableException(
				"Could not find " + CasService.CAS_PRINCIPAL_SESSION_KEY +
					" in the provided session");
		}

		return liferayCasPrincipal;
	}

}
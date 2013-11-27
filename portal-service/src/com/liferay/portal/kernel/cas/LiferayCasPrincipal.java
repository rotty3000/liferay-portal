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

package com.liferay.portal.kernel.cas;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Carlos Sierra Andrés
 */
public interface LiferayCasPrincipal extends Serializable {

	/**
	 * Retrieves a CAS proxy ticket for this specific principal.
	 *
	 * @param service the service we wish to proxy this user to.
	 * @return a String representing the proxy ticket.
	 */
	String getProxyTicketFor(String service);

	/**
	 * The Map of key/value pairs associated with this principal.
	 * @return the map of key/value pairs associated with this principal.
	 */
	Map getAttributes();

}
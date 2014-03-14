/*
 * Copyright (c) OSGi Alliance (2012, 2014). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.service.http.runtime;

import org.osgi.service.http.HttpConstants;

/**
 * Represents a {@code Servlet} service registered as an error page used by the
 * Http Service runtime.
 * 
 * @NotThreadSafe
 * @author $Id: ec5feef3bf7f77996b06134bf2b6bef09d835c99 $
 * @since 1.3
 */
public class ErrorPageDTO extends BaseServletDTO {
	/**
	 * The exceptions this error page is registered for. This error might be
	 * empty.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_SERVLET_ERROR_PAGE
	 */
	public String[]	exceptions;

	/**
	 * The error codes this error page is registered for. This error might be
	 * empty.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_SERVLET_ERROR_PAGE
	 */
	public long[]	errorCodes;

	/**
	 * Service property identifying this whiteboard service. This value is 0 or
	 * a positive number and the corresponding service registration can be
	 * looked up from the service registry by querying for the service with the
	 * {@link org.osgi.framework.Constants#SERVICE_ID} set to this value.
	 */
	public long		serviceId;
}

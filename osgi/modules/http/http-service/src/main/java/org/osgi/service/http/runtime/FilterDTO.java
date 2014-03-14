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

import org.osgi.dto.DTO;
import org.osgi.service.http.HttpConstants;

/**
 * Represents a servlet {@code Filter} service used by the Http Service runtime.
 * 
 * @NotThreadSafe
 * @author $Id: 6e84a0ca31c79a4f741f4aaf3ad479bf84e493e4 $
 * @since 1.3
 */
public class FilterDTO extends DTO {
	/**
	 * The name of the servlet filter.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_FILTER_NAME
	 */
	public String				name;

	/**
	 * The request mappings for the servlet filter.
	 * 
	 * <p>
	 * The specified patterns are used to determine whether a request should be
	 * mapped to the servlet filter.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_FILTER_PATTERN
	 */
	public String[]				patterns;

	/**
	 * The servlet names for the servlet filter.
	 * 
	 * <p>
	 * The specified names are used to determine the servlets whose requests
	 * should be mapped to the servlet filter.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_FILTER_NAME
	 */
	public String[]				servletNames;

	/**
	 * Specifies whether the servlet filter supports asynchronous processing.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_FILTER_ASYNC_SUPPORTED
	 */
	public boolean				asyncSupported;

	/**
	 * The dispatcher associations for the servlet filter.
	 * 
	 * <p>
	 * The specified names are used to determine in what occasions the servlet
	 * filter is called
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_FILTER_DISPATCHER
	 */
	public String[]				dispatcher;

	/**
	 * Service property identifying this whiteboard service. This value is 0 or
	 * a positive number and the corresponding service registration can be
	 * looked up from the service registry by querying for the service with the
	 * {@link org.osgi.framework.Constants#SERVICE_ID} set to this value.
	 */
	public long					serviceId;

	/**
	 * The service id of the {@code ServletContext} for the servlet filter.
	 */
	public long		servletContextId;
}

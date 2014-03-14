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
 * Represents a resource definition used by the Http Service runtime.
 * 
 * @NotThreadSafe
 * @author $Id: e591a1261dfe8a180a601451f6fffce658a98d90 $
 * @since 1.3
 */
public class ResourceDTO extends DTO {
	/**
	 * The request mappings for the resource
	 * 
	 * <p>
	 * The specified patterns are used to determine whether a request should be
	 * mapped to the resource.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_SERVLET_PATTERN
	 */
	public String[]				patterns;

	/**
	 * The prefix of the resource.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_RESOURCE_PREFIX
	 */
	public String				prefix;

	/**
	 * Service property identifying the service. In the case of a whiteboard
	 * service's registration, this value is 0 or a positive number and the
	 * corresponding service registration can be looked up from the service
	 * registry by querying for the service with the
	 * {@link org.osgi.framework.Constants#SERVICE_ID} set to this value. If
	 * this service has not been registered through the whiteboard service the
	 * value will be less than zero and the Http Service assigns unique negative
	 * numbers in this case.
	 */
	public long					serviceId;

	/**
	 * The service id of the {@code ServletContext} for the resource.
	 */
	public long		servletContextId;
}

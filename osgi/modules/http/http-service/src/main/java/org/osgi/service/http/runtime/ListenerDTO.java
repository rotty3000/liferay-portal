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

/**
 * Represents a listener service used by the Http Service runtime.
 * 
 * @NotThreadSafe
 * @author $Id: 84c639f2fa77fd83edfa24d16a836e195c5122d1 $
 * @since 1.3
 */
public class ListenerDTO extends DTO {

	/**
	 * The fully qualified type name the listener.
	 */
	public String				type;

	/**
	 * Service property identifying this whiteboard service. This value is 0 or
	 * a positive number and the corresponding service registration can be
	 * looked up from the service registry by querying for the service with the
	 * {@link org.osgi.framework.Constants#SERVICE_ID} set to this value.
	 */
	public long					serviceId;

	/**
	 * The service id of the {@code ServletContext} for the listener.
	 */
	public long		servletContextId;
}

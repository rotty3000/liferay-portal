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
 * Represents common information about {@code Servlet} a service used by the
 * Http Service runtime.
 * 
 * @NotThreadSafe
 * @author $Id: 7b3fa7f19be380699abb6281c0ba8ea1dcb55c31 $
 * @since 1.3
 */
public abstract class BaseServletDTO extends DTO {
	/**
	 * The name of the servlet.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_SERVLET_NAME
	 */
	public String				name;

	/**
	 * The information string from the servlet.
	 * 
	 * <p>
	 * This is the value returned by the {@code Servlet.getServletInfo()}
	 * method.
	 */
	public String				servletInfo;

	/**
	 * Specifies whether the servlet supports asynchronous processing.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED
	 */
	public boolean				asyncSupported;

	/**
	 * The service id of the {@code ServletContext} for the servlet.
	 */
	public long		servletContextId;
}

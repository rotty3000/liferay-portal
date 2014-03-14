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

import java.util.Map;
import org.osgi.dto.DTO;
import org.osgi.service.http.HttpConstants;

/**
 * Represents a {@code ServletContext} created for registered servlets,
 * resources, servlet filters, and listeners backed by a
 * {@link org.osgi.service.http.ServletContextHelper} service.
 * 
 * @NotThreadSafe
 * @author $Id: a7c13d211a661177a189136977b2feca0c975d89 $
 * @since 1.3
 */
public class ServletContextDTO extends DTO {
	/**
	 * The names of the http context.
	 * 
	 * An array of the names the corresponding
	 * {@link org.osgi.service.http.ServletContextHelper} has been registered
	 * with or {@code null} for Http Service managed contexts.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_CONTEXT_NAME
	 */
	public String[]				names;

	/**
	 * Specifies whether the http context is shared.
	 * 
	 * @see HttpConstants#HTTP_WHITEBOARD_CONTEXT_SHARED
	 */
	public boolean				shared;

	/**
	 * The name of the servlet context.
	 * 
	 * <p>
	 * This is the value returned by the
	 * {@code ServletContext.getServletContextName()} method.
	 */
	public String				contextName;

	/**
	 * The servlet context path.
	 * 
	 * This is the value returned by the {@code ServletContext.getContextPath()}
	 * method.
	 */
	public String				contextPath;

	/**
	 * The servlet context initialization parameters.
	 */
	public Map<String, String>	initParams;

	/**
	 * The servlet context attributes.
	 * 
	 * <p>
	 * The value type must be a numerical type, Boolean, String, DTO or an array
	 * of any of the former. Therefore this method will only return the
	 * attributes of the servlet context conforming to this constraint.
	 */
	public Map<String, Object>	attributes;

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
	 * Returns the representations of the {@code Servlet} services associated
	 * with this context.
	 * 
	 * The representations of the {@code Servlet} services associated with this
	 * context. The returned array may be empty if this context is currently not
	 * associated with any {@code Servlet} services.
	 */
	public ServletDTO[]			servletDTOs;

	/**
	 * Returns the representations of the resource services associated with this
	 * context.
	 * 
	 * The representations of the resource services associated with this
	 * context. The returned array may be empty if this context is currently not
	 * associated with any resource services.
	 */
	public ResourceDTO[]		resourceDTOs;

	/**
	 * Returns the representations of the servlet {@code Filter} services
	 * associated with this context.
	 * 
	 * The representations of the servlet {@code Filter} services associated
	 * with this context. The returned array may be empty if this context is
	 * currently not associated with any servlet {@code Filter} services.
	 */
	public FilterDTO[]			filterDTOs;

	/**
	 * Returns the representations of the error page {@code Servlet} services
	 * associated with this context.
	 * 
	 * The representations of the error page {@code Servlet} services associated
	 * with this context. The returned array may be empty if this context is
	 * currently not associated with any error pages.
	 */
	public ErrorPageDTO[]		errorPageDTOs;

	/**
	 * Returns the representations of the listener services associated with this
	 * context.
	 * 
	 * The representations of the listener services associated with this
	 * context. The returned array may be empty if this context is currently not
	 * associated with any listener services.
	 */
	public ListenerDTO[]		listenerDTOs;
}

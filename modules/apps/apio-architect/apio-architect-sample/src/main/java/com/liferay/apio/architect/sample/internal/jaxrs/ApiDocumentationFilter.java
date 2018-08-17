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

package com.liferay.apio.architect.sample.internal.jaxrs;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;

/**
 * JAX-RS filter that add a Link header to the responses of the base endpoint.
 * This Link header will include the url of the hydra api documentation to be
 * discovered by a hypermedia consumer.
 *
 * @author Víctor Galán
 * @review
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(liferay.apio.architect.application=true)",
		"osgi.jaxrs.extension=true"
	}
)
public class ApiDocumentationFilter implements ContainerResponseFilter {

	@Override
	public void filter(
			ContainerRequestContext requestContext,
			ContainerResponseContext responseContext)
		throws IOException {

		UriInfo uriInfo = requestContext.getUriInfo();

		if ("/".equals(uriInfo.getPath())) {
			StringBuilder linkValueStringBuilder = new StringBuilder();

			linkValueStringBuilder.append("Link: <");
			linkValueStringBuilder.append(uriInfo.getBaseUri());
			linkValueStringBuilder.append("doc>; rel=");
			linkValueStringBuilder.append(_HYDRA_DOCUMENTATION_REF);

			MultivaluedMap<String, Object> headers =
				responseContext.getHeaders();

			headers.add("Link", linkValueStringBuilder);
		}
	}

	private static final String _HYDRA_DOCUMENTATION_REF =
		"http://www.w3.org/ns/hydra/core#apiDocumentation";

}
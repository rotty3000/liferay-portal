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

package com.liferay.vulcan.liferay.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.vulcan.contributor.APIContributor;
import com.liferay.vulcan.contributor.ResourceMapper;
import com.liferay.vulcan.endpoint.RootEndpoint;
import com.liferay.vulcan.liferay.scope.GroupScoped;
import com.liferay.vulcan.resource.Resource;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
@Path("/")
public class LiferayRootEndpoint implements RootEndpoint {

	@Activate
	public void activate(BundleContext bundleContext) {
		ServiceReferenceMapper<String, APIContributor> serviceReferenceMapper =
			ServiceReferenceMapperFactory.create(
				bundleContext, (service, emitter) ->
					emitter.emit(service.getPath()));

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, APIContributor.class, null, serviceReferenceMapper);
	}

	@Deactivate
	public void deactivate() {
		_serviceTrackerMap.close();
	}

	@Path("/group/{id}/")
	public LiferayRootEndpoint getGroupLiferayRootEndpoint(
		@PathParam("id") long id) {

		GroupThreadLocal.setGroupId(id);

		return this;
	}

	@Override
	@Path("/{path}")
	public Resource getResource(@PathParam("path") String path) {
		if (!_serviceTrackerMap.containsKey(path)) {
			throw new NotFoundException();
		}

		APIContributor apiContributor = _serviceTrackerMap.getService(path);

		if (apiContributor instanceof Resource) {
			Resource resource = (Resource)apiContributor;

			if (resource instanceof GroupScoped) {
				GroupScoped groupScoped = (GroupScoped)resource;

				groupScoped.setGroupId(GroupThreadLocal.getGroupId());
			}

			resourceContext.initResource(resource);

			return resource;
		}
		else if (apiContributor instanceof ResourceMapper) {
			LiferayDispatcherResource liferayDispatcherResource =
				new LiferayDispatcherResource((ResourceMapper)apiContributor);

			resourceContext.initResource(liferayDispatcherResource);

			return liferayDispatcherResource;
		}

		throw new NotFoundException();
	}

	@Context
	protected ResourceContext resourceContext;

	private ServiceTrackerMap<String, APIContributor> _serviceTrackerMap;

}
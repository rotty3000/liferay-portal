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

package com.liferay.osgi.diagnostics.jsonws;

import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;

import org.osgi.dto.DTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"json.web.service.context.name=diagnostics",
		"json.web.service.context.path=/diagnostics"
	},
	service = Object.class
)
@JSONWebService
public class DiagnosticsInfo {

	@JSONWebService
	public JSONSerializable getBundleWiring(long bundleId) {
		Bundle bundle = _bundleContext.getBundle(bundleId);

		final BundleWiringDTO bundleWiringDTO = bundle.adapt(
			BundleWiringDTO.class);

		return new JSONSerializable() {

			@Override
			public String toJSONString() {
				return  bundleWiringDTO.toString();
			}

		};
	}

	@JSONWebService
	public JSONSerializable getBundleWirings(long bundleId) {
		Bundle bundle = _bundleContext.getBundle(bundleId);

		final BundleWiringsDTO bundleWiringsDTO = new BundleWiringsDTO();

		bundleWiringsDTO.bundleWiringDTOs = bundle.adapt(
			BundleWiringDTO[].class);

		return new JSONSerializable() {

			@Override
			public String toJSONString() {
				return  bundleWiringsDTO.toString();
			}

		};
	}

	@JSONWebService
	public JSONSerializable getFrameworkInfo() {
		Bundle systemBundle = _bundleContext.getBundle(0);

		final FrameworkDTO frameworkDTO = systemBundle.adapt(
			FrameworkDTO.class);

		return new JSONSerializable() {

			@Override
			public String toJSONString() {
				return  frameworkDTO.toString();
			}

		};
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext = null;
	}

	private BundleContext _bundleContext;

	class BundleWiringsDTO extends DTO {

		public BundleWiringDTO[] bundleWiringDTOs;

	}

}
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

package com.liferay.portal.app.resolver;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.framework.ServiceReference;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;

import com.liferay.portal.app.license.AppLicenseVerifier;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.Bundle;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import java.util.Collection;
import java.util.Dictionary;

/**
 * @author Amos Fong
 */
public class AppResolverHook implements ResolverHook {

	@Override
	public void filterSingletonCollisions(
		BundleCapability singleton,
		Collection<BundleCapability> collisionCandidates) {
	}

	@Override
	public void filterResolvable(Collection<BundleRevision> candidates) {
	}

	@Override
	public void filterMatches(
		BundleRequirement requirement,
		Collection<BundleCapability> candidates) {

		try {
			doFilterMatches(requirement);
		}
		catch (Exception e) {
			_log.error(e, e);

			candidates.clear();
		}
	}

	@Override
	public void end() {
	}

	protected void doFilterMatches(BundleRequirement requirement)
		throws Exception {

		BundleRevision bundleRevision = requirement.getResource();

		Bundle bundle = bundleRevision.getBundle();

//		System.out.println("####HOOK RESOLVER FILTERING: " + bundle.getBundleId());

		Dictionary<String, String> headers = bundle.getHeaders();

		String marketplaceProperties = headers.get("X-Liferay-Marketplace");
		
		if (marketplaceProperties == null) {
			return;
		}

		Attrs parameters = OSGiHeader.parseProperties(marketplaceProperties);

		String productId = parameters.get("productId");
		String productType = parameters.get("productType");
		String productVersion = parameters.get("productVersion");
		String licenseVersion = parameters.get("licenseVersion");

		System.out.println("productId: " + productId);
		System.out.println("productType: " + productType);
		System.out.println("productVersion: " + productVersion);
		System.out.println("licenseVersion: " + licenseVersion);
		
		if (productId == null) {
			return;
		}

		Registry registry = RegistryUtil.getRegistry();

		String filter = "(version=*)"; // "(version=" + licenseVersion;

		Collection<AppLicenseVerifier> appLicenseVerifiers =
			registry.getServices(AppLicenseVerifier.class, filter);

		if ((appLicenseVerifiers == null) || appLicenseVerifiers.isEmpty()) {
			
			System.out.println("# NO LICENSE VERIFIERS FOUND");

			// throw new License verifier exception
		}

		for (AppLicenseVerifier appLicenseVerifier : appLicenseVerifiers) {
//			if (!appLicenseVerifier.verify()) {
//				System.out.println("####HOOK RESOLVER NOT VERIFIED");
//
//				candidates.clear();
//			}
		}
	}


	private static final Log _log = LogFactoryUtil.getLog(
		AppResolverHook.class);

}
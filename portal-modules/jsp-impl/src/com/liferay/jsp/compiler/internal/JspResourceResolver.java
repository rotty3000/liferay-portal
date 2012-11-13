/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.jsp.compiler.internal;

import com.liferay.portal.module.framework.ModuleFrameworkConstants;
import com.liferay.portal.security.pacl.PACLClassLoaderUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;

import org.phidias.compile.ResourceResolver;

/**
 * @author Raymond Augé
 */
public class JspResourceResolver
	implements ModuleFrameworkConstants, ResourceResolver {

	public JspResourceResolver(JspResourceCache jspResourceCache) {
		_jspResourceCache = jspResourceCache;
	}

	public URL getResource(BundleWiring bundleWiring, String name) {
		URL resource = bundleWiring.getBundle().getResource(name);

		if ((resource == null) &&
			(bundleWiring.getBundle().getBundleId() == 0)) {

			return PACLClassLoaderUtil.getPortalClassLoader().getResource(name);
		}

		return bundleWiring.getBundle().getResource(name);
	}

	public Collection<String> resolveResources(
		BundleWiring bundleWiring, String path, String filePattern,
		int options) {

		Collection<String> resources = bundleWiring.listResources(
			path, filePattern, options);

		if ((resources == null) || (resources.isEmpty()) &&
			(bundleWiring.getBundle().getBundleId() == 0)) {

			return handleSystemBundle(bundleWiring, path, filePattern, options);
		}

		return resources;
	}

	protected Collection<String> handleSystemBundle(
		BundleWiring bundleWiring, final String path, final String filePattern,
		int options) {

		Collection<String> resources = _jspResourceCache.getResources(
			bundleWiring, path.concat(filePattern));

		if (resources != null) {
			return resources;
		}

		resources = Collections.emptyList();

		String packageName = path.replace('/', '.');
		String matcherPath = path.concat("/");
		String matcherPattern = matcherPath.concat(
			filePattern.replace("*", "[^\\/]*"));

		List<BundleCapability> capabilities = bundleWiring.getCapabilities(
			BundleRevision.PACKAGE_NAMESPACE);

		for (BundleCapability capability : capabilities) {
			Map<String, Object> attributes = capability.getAttributes();

			Object packageAttribute = attributes.get(
				BundleRevision.PACKAGE_NAMESPACE);

			if ((packageAttribute == null) ||
				!packageAttribute.equals(packageName)) {

				continue;
			}

			BundleRevision revision = capability.getRevision();
			String symbolicName = revision.getSymbolicName();

			if (((revision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) &&
				symbolicName.startsWith(SYSTEM_GENERATED_FRAGMENT_PREFIX) &&
				!symbolicName.contains("portal-impl") &&
				!symbolicName.contains("portal-service")) {

				URI locationURI = null;

				try {
					locationURI = new URI(revision.getBundle().getLocation());
				}
				catch (URISyntaxException e1) {
					e1.printStackTrace();

					continue;
				}

				File location = new File(locationURI);

				if (!location.exists() || !location.canRead()) {
					continue;
				}

				List<String> resourcePaths = new ArrayList<String>();

				if (location.isDirectory()) {
					// Unlikely but make it work in any case
					File[] listFiles = new File(location, path).listFiles(
						new FilenameFilter() {

							public boolean accept(File dir, String name) {
								return name.matches(filePattern);
							}

						}
					);

					for (File file : listFiles) {
						resourcePaths.add(matcherPath.concat(file.getName()));
					}
				}
				else {
					// We're only going to operate here if it's actually a jar
					try {
						ZipFile zipFile = new ZipFile(location);

						Enumeration<? extends ZipEntry> entries =
							zipFile.entries();

						while (entries.hasMoreElements()) {
							ZipEntry nextElement = entries.nextElement();

							String name = nextElement.getName();

							if (name.matches(matcherPattern)) {
								resourcePaths.add(name);
							}
						}
					}
					catch (IOException e) {
						// Ok, so it's not jar..
						e.printStackTrace();
					}
				}

				resources = resourcePaths;
			}
		}

		_jspResourceCache.putResources(
			bundleWiring, path.concat(filePattern), resources);

		return resources;
	}

	private JspResourceCache _jspResourceCache;

}
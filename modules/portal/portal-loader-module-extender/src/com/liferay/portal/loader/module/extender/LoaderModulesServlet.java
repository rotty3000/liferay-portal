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

package com.liferay.portal.loader.module.extender;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import java.net.URL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"osgi.http.whiteboard.servlet.name=Loader Modules Servlet",
		"osgi.http.whiteboard.servlet.pattern=/loader_modules",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = {LoaderModulesServlet.class, Servlet.class}
)
public class LoaderModulesServlet extends HttpServlet {

	protected long getTrackingCount() {
		return _trackingCount.get();
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/javascript; charset=UTF-8");

		PrintWriter writer = response.getWriter();

		writer.println("(function() {");
		writer.println("Liferay.PATHS = {");

		Collection<LoaderModule> values = _loaderModules.values();

		Iterator<LoaderModule> iterator = values.iterator();
		Set<String> _processed = new HashSet<>();

		while (iterator.hasNext()) {
			LoaderModule loaderModule = iterator.next();

			String name = loaderModule.getName();
			String config = loaderModule.getConfig();

			if (config.length() == 0) {
				continue;
			}

			if (!_processed.contains(name)) {
				_processed.add(name);

				writer.write("'");
				writer.write(name);
				writer.write("': '");
				writer.write(loaderModule.getContextPath());
				writer.println("',");
			}

			writer.write("'");
			writer.write(name);
			writer.write('@');
			writer.write(loaderModule.getVersion());
			writer.write("': '");
			writer.write(loaderModule.getContextPath());
			writer.write("'");

			if (iterator.hasNext()) {
				writer.write(",");
			}

			writer.println();
		}

		writer.println("};");
		writer.println("Liferay.MODULES = {");

		boolean started = false;
		_processed = new HashSet<>();

		for (LoaderModule loaderModule : values) {
			String name = loaderModule.getName();
			String config = loaderModule.getConfig();

			if (config.length() == 0) {
				continue;
			}

			if (started) {
				writer.write(",");
			}

			if (!_processed.contains(name)) {
				_processed.add(name);

				String configRaw = config.replace(
					name + "@" + loaderModule.getVersion(), name);

				writer.print(configRaw);
				writer.println(",");
			}

			writer.println(config);
			started = true;
		}

		writer.println("};");
		writer.println("}());");
		writer.close();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		service = ServletContext.class,
		target = "(osgi.web.contextpath=*)"
	)
	protected void setServletContext(
		ServiceReference<ServletContext> serviceReference) {

		String contextPath = (String)serviceReference.getProperty(
			"osgi.web.contextpath");

		LoaderModule loaderModule = new LoaderModule(
			serviceReference.getBundle(), contextPath);

		loaderModule = _loaderModules.putIfAbsent(
			serviceReference, loaderModule);

		if (loaderModule == null) {
			_trackingCount.incrementAndGet();
		}
	}

	protected void unsetServletContext(
		ServiceReference<ServletContext> serviceReference) {

		LoaderModule loaderModule = _loaderModules.remove(serviceReference);

		if (loaderModule != null) {
			_trackingCount.incrementAndGet();
		}
	}

	private AtomicLong _trackingCount = new AtomicLong(0);
	private ConcurrentMap<ServiceReference<ServletContext>, LoaderModule>
		_loaderModules = new ConcurrentSkipListMap<>();

	public class LoaderModule {

		public LoaderModule(Bundle bundle, String contextPath) {
			_bundle = bundle;
			_contextPath = contextPath;

			Version version = _bundle.getVersion();

			_version = version.toString();

			BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

			List<BundleCapability> capabilities = bundleWiring.getCapabilities(
				WR_NS);

			if (capabilities.isEmpty()) {
				_name = _bundle.getSymbolicName();
				_config = "";

				return;
			}

			BundleCapability bundleCapability = capabilities.get(0);

			Map<String, Object> attributes = bundleCapability.getAttributes();

			_name = (String)attributes.get(WR_NS);
			_config = _urlToString(_bundle.getEntry("config.js"));
		}

		public String getConfig() {
			return _config;
		}

		public String getContextPath() {
			return _contextPath;
		}

		public String getName() {
			return _name;
		}

		public String getVersion() {
			return _version;
		}

		private String _normalize(JSONObject jsonObject) {
			JSONArray names = jsonObject.names();

			for (int i = 0; i < names.length(); i++) {
				String name = (String)names.get(i);

				int x = name.indexOf('/');

				if (x == -1) {
					continue;
				}

				String moduleName = name.substring(0, x);
				String modulePath = name.substring(x);

				if (!moduleName.equals(getName())) {
					continue;
				}

				moduleName = getName() + "@" + getVersion() + modulePath;

				JSONObject curObject = jsonObject.getJSONObject(name);

				JSONArray jsonArray = curObject.getJSONArray("dependencies");

				for (int j = 0; j < jsonArray.length(); j++) {
					String dependency = jsonArray.getString(j);

					int y = dependency.indexOf('/');

					if (y == -1) {
						continue;
					}

					String dependencyName = dependency.substring(0, y);
					String dependencyPath = dependency.substring(y);

					if (!dependencyName.equals(getName())) {
						continue;
					}

					dependencyName =
						getName() + "@" + getVersion() + dependencyPath;

					jsonArray.put(j, dependencyName);
				}

				jsonObject.remove(name);

				jsonObject.put(moduleName, curObject);
			}

			String normalized = jsonObject.toString();

			if (normalized.startsWith("{") && normalized.endsWith("}")) {
				normalized = normalized.substring(1, normalized.length() - 1);
			}

			return normalized;
		}

		private String _urlToString(URL url) {
			if (url == null) {
				return "";
			}

			try (Reader reader = new InputStreamReader(url.openStream())) {
				JSONTokener jsonTokener = new JSONTokener(reader);

				return _normalize(new JSONObject(jsonTokener));
			}
			catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		private static final String WR_NS = "osgi.webresource";

		private final Bundle _bundle;
		private final String _config;
		private final String _contextPath;
		private final String _name;
		private final String _version;

	}

}
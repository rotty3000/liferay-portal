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

package com.liferay.osgi.config.admin.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 *
 */
public class ConfigurationsHelper {

	public ConfigurationsHelper(
		BundleContext bundleContext, String languageId) {

		_bundleContext = bundleContext;

		_ocdHelper = new ObjectClassDefinitionsHelper(
			bundleContext, languageId);

		_configurationAdminRef = _bundleContext.getServiceReference(
			ConfigurationAdmin.class);
	}

	public List<ConfigurableService> getConfigurableSevices(
			String locale, String pidFilter)
		throws Exception {

		List<ConfigurableService> configurableServices =
						new ArrayList<ConfigurableService>();

		Map<String, String> managedServicesMap = _collectManagedServices(
						pidFilter, locale);

		Set<Entry<String, String>> entrySet = managedServicesMap.entrySet();

		for (Entry<String, String> entry : entrySet) {
			String pid = entry.getKey();
			String ocdName = entry.getValue();
			ConfigurableService configurableService = new ConfigurableService(
				false, StringPool.BLANK, ocdName, pid);
			configurableServices.add(configurableService);
		}

		Map<String, String> managedServiceFactoryMap =
						_collectManagedServiceFactories(pidFilter, locale);

		entrySet = managedServiceFactoryMap.entrySet();

		for (Entry<String, String> entry : entrySet) {
			String factoryPid = entry.getKey();

			String ocdName = entry.getValue();

			ConfigurableService configurableService = new ConfigurableService(
							true, factoryPid, ocdName, factoryPid);

			//If it factory we just use it

			if (configurableServices.contains(configurableService)) {
				int idx = configurableServices.indexOf(configurableService);
				configurableServices.remove(idx);
				configurableServices.add(configurableService);
			}
		}

		return configurableServices;
	}

	public Configuration[] getConfigurations(String filter)
		throws InvalidSyntaxException, IOException {

		if (_log.isDebugEnabled()) {
			_log.debug("Using filter:"+filter);
		}

		ConfigurationAdmin configurationAdmin = _configuationAdmin();

		Configuration[] configs = configurationAdmin.listConfigurations(filter);

		if (configs != null) {
			return configs;
		}

		return null;
	}

	public ObjectClassDefinition getObjectClassDefintion(
		Configuration configuration) {

		String pid = null;

		if (configuration!= null) {
			pid = configuration.getPid();

			if (Validator.isNull(pid)) {
				pid = configuration.getFactoryPid();
			}
		}

		return _ocdHelper.getObjectClassDefinition(pid);
	}

	private Map<String, String> _collectManagedServiceFactories(
			String pidFilter, String locale)
		throws InvalidSyntaxException {

		String serviceClazz = ManagedServiceFactory.class.getName();

		Map<String, String> servicePidOcdMap = getServicesToPidMap(
			serviceClazz, locale, pidFilter);

		//Get ones which are not managed services but has metatypes
		_collectMetaTypes(
			servicePidOcdMap,
			_ocdHelper.objectClassDefinitions(
				ObjectClassDefinitionsHelper.FACTORY_PIDS, locale), pidFilter,
				ConfigurationAdmin.SERVICE_FACTORYPID);

		return servicePidOcdMap;
	}

	private Map<String, String> _collectManagedServices(
			String pidFilter, String locale)
		throws InvalidSyntaxException {

		String serviceClazz = ManagedService.class.getName();

		Map<String, String> servicePidOcdMap = getServicesToPidMap(
			serviceClazz, locale, pidFilter);

		//Get ones which are not managed services but has metatypes
		_collectMetaTypes(
			servicePidOcdMap,
			_ocdHelper.objectClassDefinitions(
				ObjectClassDefinitionsHelper.PIDS, locale), pidFilter,
				Constants.SERVICE_PID);

		//TODO Handle configurations

		return servicePidOcdMap;
	}

	private void _collectMetaTypes(
		Map<String, String> servicePidOcdMap,
		Map<String, ObjectClassDefinition> objectClassDefinitions,
		String criteria, String servicePidType) {

		Filter filter = null;

		if (criteria!= null) {
			try {
				filter = _bundleContext.createFilter(criteria);
			}
			catch (InvalidSyntaxException e) {
				_log.error("Error appending metatypes",e);
			}
		}

		Set<String> keys = objectClassDefinitions.keySet();

		for (String pid : keys) {
			ObjectClassDefinition ocd = objectClassDefinitions.get(pid);

			if (filter == null) {
				servicePidOcdMap.put(pid, ocd.getName());
			}
			else {
				Dictionary<String, String> serviceProps =
								new Hashtable<String, String>();
				serviceProps.put(servicePidType, pid);

				if (filter.match(serviceProps)) {
					servicePidOcdMap.put(pid, ocd.getName());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ConfigurationAdmin _configuationAdmin() {
		return (ConfigurationAdmin) _bundleContext.getService(
			_configurationAdminRef);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, String> getServicesToPidMap(
			String serviceClazz, String locale, String serviceFilter)
		throws InvalidSyntaxException {

		Map<String, String> servicePidOcdMap = new HashMap<String, String>();

		ServiceReference[] serviceReferences =
						_bundleContext.getServiceReferences(
							serviceClazz, serviceFilter);

		for (int i = 0; ((serviceReferences!= null) &&
						(i < serviceReferences.length) ); i++) {

			ServiceReference reference = serviceReferences[i];

			Object objPid = reference.getProperty(Constants.SERVICE_PID);

			if (objPid!= null) {
				String pid = String.valueOf(objPid);

				Bundle bundle = reference.getBundle();

				ObjectClassDefinition ocd = _ocdHelper.getObjectClassDefinition(
								bundle, pid, locale);

				if (ocd!= null) {
					servicePidOcdMap.put(pid, ocd.getName());
				}
			}
		}

		return servicePidOcdMap;
	}

	private static Log _log = LogFactoryUtil.getLog(ConfigurationsHelper.class);

	private BundleContext _bundleContext;
	private ServiceReference _configurationAdminRef;
	private ObjectClassDefinitionsHelper _ocdHelper;

}
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

package com.liferay.portal.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.liferay.portal.configuration.PKConfiguration.PK;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 * @author Milen Dyankov
 */
public abstract class PKConfigurationRegistry<T extends PKConfiguration> {

	public T getConfiguration(PK pk) {

		String pid = _pk2pid.get(pk);
		return _configsByPid.get(pid);
	}

	public Set<PK> getKeys() {

		return Collections.unmodifiableSet(_configsByPK.keySet());
	}

	public Set<T> getAllConfigs(PK key) {

		return Collections.unmodifiableSet(_configsByPK.get(key));
	}

	protected void addConfiguration(T configuration) {
		String pid = configuration.getPid();
		
		if (pid == null || pid.trim().isEmpty()) {
			/* 
			 * This happens when the bundle starts and there is NO configuration!
			 * ConfingAdmin will call add method passing "empty" configuration (default)!
			 */
			setDefault(configuration);
			return;
		}
		
		if (hasErrors(configuration)) {
			_log.warn("Provided configuration seem to have errors (see previous messages) and will be ignored!");
		}
		else {
			save(configuration);
		}
	}

	protected void removeConfigurationInstance(T configuration) {

		String pid = configuration.getPid();
		remove(pid);
	}

	protected void updatedConfigurationInstance(T configuration) {

		String pid = configuration.getPid();
		
		if (pid == null || pid.trim().isEmpty()) {
			
			/* This happens when the last configuration is removed!
			 * ConfingAdmin will NOT call remove method! 
			 * It will call update method instead, passing "empty" configuration!
			 */
			
			for (PK pk : _pk2pid.keySet()) {
				_log.warn("There is no valid configuration for '" + pk + "'! Using defaults!");
			}
			
			_configsByPid.clear();
			_configsByPK.clear();
			_pid2pk.clear();
			_pk2pid.clear();
		}

		if (!_configsByPid.containsKey(pid)) {
			addConfiguration(configuration);
			return;
		}
		
		if (hasErrors(configuration)) {
			_log.warn("Updated configuration seem to have errors (see previous messages) and will be removed !");
			remove(pid);
			return;
		}
		
		PK newPK = configuration.getKey();
		PK oldPK = _pid2pk.get(configuration.getPid());
		
		_configsByPid.put(pid, configuration);
		addConfigForPK(configuration);

		if (!newPK.equals(oldPK)) {
			_log.warn("Updated configuration have different primary key!");
			removeConfigForPK(oldPK, configuration);
			_pid2pk.put(pid, newPK);
			updateCurrent(newPK);
			updateCurrent(oldPK);
		}
	}
	
	protected abstract PKConfigurationValidator<T> getValidator();

	protected abstract void setDefault(T configuration);

	protected boolean hasErrors(T configuration) {

		List<ValidationResult> results;
		try {
			results = getValidator().check(configuration);
		}
		catch (PortalException e) {
			_log.error("Unexpected error occured while validating configuration!", e);
			return true;
		}
		boolean errors = false;
		if (!results.isEmpty()) {
			for (ValidationResult validationResult : results) {
				switch (validationResult.getType()) {
				case ERROR:
					errors = true;
				default:
					_log.warn(validationResult.getMessage());
					break;
				}
			}
		}
		return errors;
	}

	private void updateCurrent(PK pk) {

		String pid = _pk2pid.get(pk);

		if (!_configsByPid.containsKey(pid) || !_pid2pk.get(pid).equals(pk)) {
			_pk2pid.remove(pk);
			Set<T> configs = _configsByPK.get(pk);
			if (configs != null && !configs.isEmpty()) {
				String newPid = configs.iterator().next().getPid();
				_pk2pid.put(pk, newPid);
				_log.warn("Updated configuration for '" + pk + "'! Now using configuration '" + newPid + "'!");
			} else {
				_log.warn("There is no valid configuration for '" + pk + "'! Using defaults!");
			}
		}
	}


	private void addConfigForPK(T configuration) {

		PK pk = configuration.getKey();
		Set<T> configs = _configsByPK.get(pk);
		if (configs == null) {
			configs = new HashSet<T>();
			_configsByPK.put(pk, configs);
		}
		configs.add(configuration);
	}

	private void removeConfigForPK(PK pk, T configuration) {
		
		Set<T> configs = _configsByPK.get(pk);
		if (configs != null && configs.contains(configuration)) {
			configs.remove(configuration);
		}
		if (configs.isEmpty()) {
			_configsByPK.remove(pk);
		}
	}
	
	private void remove(String pid) {

		T configuration = null;
		if (_configsByPid.containsKey(pid)) {
			configuration = _configsByPid.remove(pid);
			_pid2pk.remove(pid);
		}

		if (configuration != null) {
			PK pk = configuration.getKey();
			removeConfigForPK(pk, configuration);
			updateCurrent(pk);
		}

	}

	private void save(T configuration) {

		_configsByPid.put(configuration.getPid(), configuration);
		_pid2pk.put(configuration.getPid(), configuration.getKey());
		addConfigForPK(configuration);
		updateCurrent(configuration.getKey());
	}

	private static final Log _log = LogFactoryUtil.getLog(PKConfigurationRegistry.class);

	private Map<String, T> _configsByPid = new HashMap<String, T>();
	private Map<PK, Set<T>> _configsByPK = new HashMap<PK, Set<T>>();
	private Map<String, PK> _pid2pk = new HashMap<String, PK>();
	private Map<PK, String> _pk2pid = new HashMap<PK, String>();

}

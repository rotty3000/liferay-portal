/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade.registry;

import com.liferay.portal.output.stream.container.OutputStreamContainer;

import java.util.List;

/**
 * @author Carlos Sierra Andrés
 */
public class UpgradeTask {

	public UpgradeTask(
		String bundleSymbolicName, List<UpgradeInfo> upgradeInfoList,
		OutputStreamContainer outputStreamContainer) {

		_bundleSymbolicName = bundleSymbolicName;
		_upgradeInfoList = upgradeInfoList;
		_outputStreamContainer = outputStreamContainer;
	}

	public String getBundleSymbolicName() {
		return _bundleSymbolicName;
	}

	public OutputStreamContainer getOutputStreamContainer() {
		return _outputStreamContainer;
	}

	public List<UpgradeInfo> getUpgradeInfoList() {
		return _upgradeInfoList;
	}

	private final String _bundleSymbolicName;
	private final OutputStreamContainer _outputStreamContainer;
	private final List<UpgradeInfo> _upgradeInfoList;

}
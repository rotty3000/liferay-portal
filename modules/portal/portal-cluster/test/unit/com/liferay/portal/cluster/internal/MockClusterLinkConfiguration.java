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

package com.liferay.portal.cluster.internal;

import com.liferay.portal.cluster.configuration.ClusterLinkConfiguration;

/**
 * @author Michael C. Han
 */
public class MockClusterLinkConfiguration implements ClusterLinkConfiguration {

	public MockClusterLinkConfiguration(boolean debugEnabled, boolean enabled) {
		_debugEnabled = debugEnabled;
		_enabled = enabled;
	}

	@Override
	public String autoDetectAddress() {
		return "localhost";
	}

	@Override
	public String channelNamePrefix() {
		return "liferay-cluster-link";
	}

	@Override
	public String channelSystemProperties() {
		return "";
	}

	@Override
	public boolean debugEnabled() {
		return _debugEnabled;
	}

	@Override
	public boolean enabled() {
		return _enabled;
	}

	private final boolean _debugEnabled;
	private final boolean _enabled;

}
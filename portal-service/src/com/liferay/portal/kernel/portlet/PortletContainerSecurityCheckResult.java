/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.portlet;

/**
 * @author Tomas Polesovsky
 */
public class PortletContainerSecurityCheckResult {

	public boolean accessAllowed() {
		return isExecutionAllowed() && hasPermission();
	}

	public boolean hasPermission() {
		return _hasPermission;
	}

	public boolean isControlPanelPortlet() {
		return _controlPanelPortlet;
	}

	public boolean isControlPanelSystemPortlet() {
		return _controlPanelSystemPortlet;
	}

	public boolean isEmbeddedPortlet() {
		return _embeddedPortlet;
	}

	public boolean isExecutionAllowed() {
		return _executionAllowed;
	}

	public boolean isOnDemandPortlet() {
		return _onDemandPortlet;
	}

	public boolean isPortletConfiguration() {
		return _portletConfiguration;
	}

	public boolean isPortletOnPage() {
		return _portletOnPage;
	}

	public boolean isRuntimePortlet() {
		return _runtimePortlet;
	}

	public void setExecutingControlPanelPortlet() {
		_controlPanelPortlet = true;
		_executionAllowed = true;
	}

	public void setExecutingControlPanelSystemPortlet() {
		_controlPanelSystemPortlet = true;
		_executionAllowed = true;
	}

	public void setExecutingEmbeddedPortlet() {
		_executionAllowed = true;
		_embeddedPortlet = true;
	}

	public void setExecutingOnDemandPortlet() {
		_executionAllowed = true;
		_onDemandPortlet = true;
	}

	public void setExecutingPortletConfiguration() {
		_executionAllowed = true;
		_portletConfiguration = true;
	}

	public void setExecutingPortletOnPage() {
		_executionAllowed = true;
		_portletOnPage = true;
	}

	public void setExecutingRuntimePortlet() {
		_executionAllowed = true;
		_runtimePortlet = true;
	}

	public void setHasPermissions() {
		_hasPermission = true;
	}

	private boolean _controlPanelPortlet;
	private boolean _controlPanelSystemPortlet;
	private boolean _embeddedPortlet;
	private boolean _executionAllowed;
	private boolean _hasPermission;
	private boolean _onDemandPortlet;
	private boolean _portletConfiguration;
	private boolean _portletOnPage;
	private boolean _runtimePortlet;

}
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
public class PortletContainerSecurityCheck {

	public PortletContainerSecurityCheck(PortletContainerSecurityCheck parent) {
		_parent = parent;
	}

	public PortletContainerSecurityCheck getParent() {
		return _parent;
	}

	public boolean isControlPanelPortlet() {
		return _controlPanelPortlet;
	}

	public boolean isEmbeddedPortlet() {
		return _embeddedPortlet;
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

	public boolean isSystemPortlet() {
		return _systemPortlet;
	}

	public void setExecutingControlPanelPortlet() {
		_controlPanelPortlet = true;
	}

	public void setExecutingEmbeddedPortlet() {
		_embeddedPortlet = true;
	}

	public void setExecutingOnDemandPortlet() {
		_onDemandPortlet = true;
	}

	public void setExecutingPortletConfiguration() {
		_portletConfiguration = true;
	}

	public void setExecutingPortletOnPage() {
		_portletOnPage = true;
	}

	public void setExecutingRuntimePortlet() {
		_runtimePortlet = true;
	}

	public void setExecutingSystemPortlet() {
		_systemPortlet = true;
	}

	public void setParent(PortletContainerSecurityCheck parent) {
		_parent = parent;
	}

	private boolean _controlPanelPortlet;
	private boolean _embeddedPortlet;
	private boolean _onDemandPortlet;
	private PortletContainerSecurityCheck _parent;
	private boolean _portletConfiguration;
	private boolean _portletOnPage;
	private boolean _runtimePortlet;
	private boolean _systemPortlet;

}
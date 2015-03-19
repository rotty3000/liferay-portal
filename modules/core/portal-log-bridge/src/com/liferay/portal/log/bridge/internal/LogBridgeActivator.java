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

package com.liferay.portal.log.bridge.internal;

import org.eclipse.equinox.log.ExtendedLogReaderService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 * @author Kamesh Sampath
 */
public class LogBridgeActivator
	implements BundleActivator,
	ServiceTrackerCustomizer<ExtendedLogReaderService, ExtendedLogReaderService> {

	@Override
	public ExtendedLogReaderService addingService(
		ServiceReference<ExtendedLogReaderService> serviceReference) {

		ExtendedLogReaderService extensionLogReaderService =
			_bundleContext.getService(serviceReference);

		extensionLogReaderService.addLogListener(_portalLogListener);

		return extensionLogReaderService;
	}

	@Override
	public void modifiedService(
		ServiceReference<ExtendedLogReaderService> serviceReference,
		ExtendedLogReaderService logReaderService) {
	}

	@Override
	public void removedService(
		ServiceReference<ExtendedLogReaderService> serviceReference,
		ExtendedLogReaderService logReaderService) {

		logReaderService.removeLogListener(_portalLogListener);
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		_serviceTracker = new ServiceTracker<>(
			bundleContext, ExtendedLogReaderService.class, this);

		_serviceTracker.open();

		_portalLogListener = new PortalLogListenerImpl();
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		_bundleContext = null;

		_serviceTracker.close();

		_portalLogListener = null;

		_serviceTracker = null;
	}

	private BundleContext _bundleContext;
	private PortalLogListenerImpl _portalLogListener;
	private ServiceTracker<ExtendedLogReaderService, ExtendedLogReaderService> _serviceTracker;

}
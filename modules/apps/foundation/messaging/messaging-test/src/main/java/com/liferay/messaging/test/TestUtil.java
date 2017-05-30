package com.liferay.messaging.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.messaging.MessageBus;

import java.net.URL;

import java.io.IOException;
import java.io.InputStream;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class TestUtil {

	@After
	public void after() {
		messageBusTracker.close();
	}

	@Before
	public void before() {
		messageBusTracker = new ServiceTracker<>(
				bundleContext, MessageBus.class, null);

		messageBusTracker.open();
	}

	public MessageBus getMessageBus() {
		try {
			MessageBus messageBus = messageBusTracker.waitForService(timeout);

			assertNotNull(messageBus);

			return messageBus;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	public InputStream getInputStream(String bundlePath) {
		try {
			URL url = bundle.getEntry(bundlePath);

			return url.openStream();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public Bundle install(String bundlePath) {
		try {
			return bundleContext.installBundle(
					bundlePath, getInputStream(bundlePath));
		}
		catch (BundleException be) {
			throw new RuntimeException(be);
		}
	}

	protected <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T instance, Dictionary<String, Object> properties) {

		return bundleContext.registerService(clazz, instance, properties);
	}

	protected <T> ServiceRegistration<T> registerService(
		Class<T> clazz, T instance, Object ... parts) {

		assertTrue((parts.length % 2) == 0);

		Dictionary<String, Object> properties = new Hashtable<>();

		for (int i = 0; i < parts.length; i += 2) {
			properties.put(String.valueOf(parts[i]), parts[i + 1]);
		}

		return registerService(clazz, instance, properties);
	}

	protected Bundle bundle = FrameworkUtil.getBundle(TestUtil.class);
	protected BundleContext bundleContext = bundle.getBundleContext();
	protected ServiceTracker<MessageBus, MessageBus> messageBusTracker;
	protected long timeout = 1000;

}
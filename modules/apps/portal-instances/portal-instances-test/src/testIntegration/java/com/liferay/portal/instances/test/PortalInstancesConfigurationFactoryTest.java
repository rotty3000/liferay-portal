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

package com.liferay.portal.instances.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instances.configuration.PortalInstancesConfiguration;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.function.CloseableHolder;
import com.liferay.portal.test.function.ConfigurationHolder;
import com.liferay.portal.test.function.CreatingFactoryConfigurationHolder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class PortalInstancesConfigurationFactoryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			PortalInstancesConfigurationFactoryTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testCreateVirtualInstanceFromFactory() throws Exception {
		String webId = RandomTestUtil.randomString();

		try (ConfigurationHolder configurationHolder =
				new CreatingFactoryConfigurationHolder(
					_configurationAdmin,
					PortalInstancesConfiguration.class.getName(), webId)) {

			configurationHolder.update(
				HashMapDictionaryBuilder.<String, Object>put(
					"mx", webId.concat(".foo.bar")
				).put(
					"virtualHostname", webId.concat(".foo.bar")
				).build());

			try (PortalInstancesConfigurationFactoryHolder
					portalInstancesConfigurationFactoryHolder =
						new PortalInstancesConfigurationFactoryHolder(
							_bundleContext)) {

				// It can take a very long time to instantiate a company. But by
				// the time the factoryInstance for a particular configuration
				// is available the company should have been created.

				Object factoryInstance =
					portalInstancesConfigurationFactoryHolder.waitForService(
						40000);

				Assert.assertNotNull(factoryInstance);

				Company company = _companyLocalService.getCompanyByWebId(webId);

				Assert.assertNotNull(company);
			}
		}
	}

	public static class PortalInstancesConfigurationFactoryHolder
		extends CloseableHolder<ServiceTracker<Object, Object>> {

		public PortalInstancesConfigurationFactoryHolder(
				BundleContext bundleContext)
			throws Exception {

			super(
				serviceTracker -> serviceTracker.close(),
				() -> {
					ServiceTracker<Object, Object> serviceTracker =
						new ServiceTracker<>(
							bundleContext,
							FrameworkUtil.createFilter(
								StringBundler.concat(
									"(component.name=com.liferay.portal.",
									"instances.internal.configuration.",
									"PortalInstancesConfigurationFactory)")),
							null);

					serviceTracker.open();

					return serviceTracker;
				});
		}

		public Object waitForService(long timeout) throws Exception {
			ServiceTracker<Object, Object> serviceTracker = get();

			return serviceTracker.waitForService(timeout);
		}

	}

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private BundleContext _bundleContext;

	@Inject
	private CompanyLocalService _companyLocalService;

}
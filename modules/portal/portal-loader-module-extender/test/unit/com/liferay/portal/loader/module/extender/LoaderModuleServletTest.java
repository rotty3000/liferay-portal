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

import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.json.JSONException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Raymond Augé
 */
@RunWith(PowerMockRunner.class)
public class LoaderModuleServletTest extends PowerMockito {

	@Test
	public void testBasicOutput() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse =
			new MockHttpServletResponse();

		loaderModulesServlet.service(httpServletRequest, httpServletResponse);

		Assert.assertNotNull(httpServletResponse.getContentAsString());
		Assert.assertEquals(
			"text/javascript; charset=UTF-8",
			httpServletResponse.getContentType());
	}

	@Test
	public void testSingleModuleOutput() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0,
				_getResource("dependencies/config1.js"));

		loaderModulesServlet.setServletContext(serviceReference);

		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse =
			new MockHttpServletResponse();

		loaderModulesServlet.service(httpServletRequest, httpServletResponse);

		String content = httpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("'test': '/test-1.0.0'"));
		Assert.assertTrue(content.contains("'test@1.0.0': '/test-1.0.0'"));
		Assert.assertTrue(
			content.contains(
				"\"test/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test/other.es\"]},\"test/other.es\":{\"dependencies\":" +
						"[\"exports\"]}"));
		Assert.assertTrue(
			content.contains(
				"\"test@1.0.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.0.0/other.es\"]},\"test@1.0.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"));
	}

	@Test
	public void testSingleModuleOutputNoConfig() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0, null);

		loaderModulesServlet.setServletContext(serviceReference);

		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse =
			new MockHttpServletResponse();

		loaderModulesServlet.service(httpServletRequest, httpServletResponse);

		String content = httpServletResponse.getContentAsString();

		Assert.assertFalse(content.contains("'test': '/test-1.0.0'"));
		Assert.assertFalse(content.contains("'test@1.0.0': '/test-1.0.0'"));
		Assert.assertFalse(
			content.contains(
				"\"test/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test/other.es\"]},\"test/other.es\":{\"dependencies\":" +
						"[\"exports\"]}"));
		Assert.assertFalse(
			content.contains(
				"\"test@1.0.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.0.0/other.es\"]},\"test@1.0.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"));
	}

	@Test(expected = JSONException.class)
	public void testSingleModuleOutputEmptyConfig() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0,
				_getResource("dependencies/empty.js"));

		loaderModulesServlet.setServletContext(serviceReference);
	}

	@Test(expected = JSONException.class)
	public void testSingleModuleOutputMalformedConfig() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0,
				_getResource("dependencies/malformed.js"));

		loaderModulesServlet.setServletContext(serviceReference);
	}

	@Test
	public void testSingleModuleOutputIdempotent() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0,
				_getResource("dependencies/config1.js"));

		loaderModulesServlet.setServletContext(serviceReference);
		loaderModulesServlet.setServletContext(serviceReference);

		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse =
			new MockHttpServletResponse();

		loaderModulesServlet.service(httpServletRequest, httpServletResponse);

		String content = httpServletResponse.getContentAsString();

		Assert.assertEquals(
			content.indexOf("'test': '/test-1.0.0'"),
			content.lastIndexOf("'test': '/test-1.0.0'"));
		Assert.assertEquals(
			content.indexOf("'test@1.0.0': '/test-1.0.0'"),
			content.lastIndexOf("'test@1.0.0': '/test-1.0.0'"));
		Assert.assertEquals(
			content.indexOf(
				"\"test/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test/other.es\"]},\"test/other.es\":{\"dependencies\":" +
						"[\"exports\"]}"),
			content.lastIndexOf(
				"\"test/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test/other.es\"]},\"test/other.es\":{\"dependencies\":" +
						"[\"exports\"]}"));
		Assert.assertEquals(
			content.indexOf(
				"\"test@1.0.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.0.0/other.es\"]},\"test@1.0.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"),
			content.lastIndexOf(
				"\"test@1.0.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.0.0/other.es\"]},\"test@1.0.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"));
	}

	@Test
	public void testMultiModuleOutput() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0,
				_getResource("dependencies/config1.js"));

		loaderModulesServlet.setServletContext(serviceReference);

		serviceReference = _buildServiceReference(
			"foo", new Version("13.2.23"), true, 0,
			_getResource("dependencies/config2.js"));

		loaderModulesServlet.setServletContext(serviceReference);

		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse =
			new MockHttpServletResponse();

		loaderModulesServlet.service(httpServletRequest, httpServletResponse);

		String content = httpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("'test': '/test-1.0.0'"));
		Assert.assertTrue(content.contains("'test@1.0.0': '/test-1.0.0'"));
		Assert.assertTrue(
			content.contains(
				"\"test/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test/other.es\"]},\"test/other.es\":{\"dependencies\":" +
						"[\"exports\"]}"));
		Assert.assertTrue(
			content.contains(
				"\"test@1.0.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.0.0/other.es\"]},\"test@1.0.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"));

		Assert.assertTrue(content.contains("'foo': '/foo-13.2.23'"));
		Assert.assertTrue(content.contains("'foo@13.2.23': '/foo-13.2.23'"));
		Assert.assertTrue(
			content.contains(
				"\"foo/foo.es\":{\"dependencies\":[\"exports\"," +
					"\"foo/fum.es\",\"jquery@2.15.3/jquery.js\"]}," +
						"\"foo/fum.es\":{\"dependencies\":[\"exports\"]}"));
		Assert.assertTrue(
			content.contains(
				"\"foo@13.2.23/foo.es\":{\"dependencies\":[\"exports\"," +
					"\"foo@13.2.23/fum.es\",\"jquery@2.15.3/jquery.js\"]}," +
						"\"foo@13.2.23/fum.es\":{\"dependencies\":" +
							"[\"exports\"]}"));
	}

	@Test
	public void testMultiVersionModuleOutput() throws Exception {
		LoaderModulesServlet loaderModulesServlet =
			_buildLoaderModulesServlet();

		ServiceReference<ServletContext> serviceReference =
			_buildServiceReference(
				"test", new Version("1.0.0"), true, 0,
				_getResource("dependencies/config1.js"));

		loaderModulesServlet.setServletContext(serviceReference);

		serviceReference = _buildServiceReference(
			"test", new Version("1.2.0"), true, 0,
			_getResource("dependencies/config1.js"));

		loaderModulesServlet.setServletContext(serviceReference);

		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse httpServletResponse =
			new MockHttpServletResponse();

		loaderModulesServlet.service(httpServletRequest, httpServletResponse);

		String content = httpServletResponse.getContentAsString();

		System.out.println(content);

		Assert.assertTrue(content.contains("'test': '/test-1.2.0'"));
		Assert.assertTrue(content.contains("'test@1.2.0': '/test-1.2.0'"));
		Assert.assertTrue(content.contains("'test@1.0.0': '/test-1.0.0'"));
		Assert.assertTrue(
			content.contains(
				"\"test/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test/other.es\"]},\"test/other.es\":{\"dependencies\":" +
						"[\"exports\"]}"));
		Assert.assertTrue(
			content.contains(
				"\"test@1.2.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.2.0/other.es\"]},\"test@1.2.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"));
		Assert.assertTrue(
			content.contains(
				"\"test@1.0.0/some.es\":{\"dependencies\":[\"exports\"," +
					"\"test@1.0.0/other.es\"]},\"test@1.0.0/other.es\":" +
						"{\"dependencies\":[\"exports\"]}"));
	}

	private LoaderModulesServlet _buildLoaderModulesServlet()
		throws ServletException {

		LoaderModulesServlet loaderModulesServlet = new LoaderModulesServlet();

		MockServletContext mockServletContext = new MockServletContext();

		mockServletContext.setContextPath("/loader");

		loaderModulesServlet.init(new MockServletConfig(mockServletContext));

		return loaderModulesServlet;
	}

	private ServiceReference<ServletContext> _buildServiceReference(
		String bsn, Version version, boolean capability, int ranking, URL url) {

		Bundle bundle = mock(Bundle.class);

		_mockBundle(bundle, bsn, version, url, capability);

		MockServiceReference mockServiceReference = new MockServiceReference(
			bundle, new String[] {ServletContext.class.getName()},
			_counter.incrementAndGet(), ranking);

		mockServiceReference.setProperties(
			Collections.<String, Object>singletonMap(
				"osgi.web.contextpath", "/" + bsn + "-" + version.toString()));

		return mockServiceReference;
	}

	private URL _getResource(String resourceName) {
		Class<?> clazz = getClass();

		return clazz.getResource(resourceName);
	}

	private void _mockBundle(
		Bundle bundle, String bsn, Version version, URL url,
		boolean capability) {

		BundleWiring bundleWiring = mock(BundleWiring.class);
		BundleCapability bundleCapability = mock(BundleCapability.class);

		doReturn(
			Collections.<String, Object>singletonMap("osgi.webresource", bsn)
		).
		when(bundleCapability).getAttributes();

		doReturn(
			capability ? Arrays.asList(bundleCapability) :
				Collections.emptyList()
		).
		when(bundleWiring).getCapabilities("osgi.webresource");

		doReturn(
			bundleWiring
		).
		when(bundle).adapt(BundleWiring.class);

		doReturn(
			url
		).when(bundle).getEntry("config.js");

		doReturn(
			bsn
		).when(bundle).getSymbolicName();

		doReturn(
			version
		).when(bundle).getVersion();
	}

	private AtomicInteger _counter = new AtomicInteger(0);

	private class MockServiceReference
		implements ServiceReference<ServletContext> {

		public MockServiceReference(
			Bundle bundle, String[] objectClasses, int id, int ranking) {

			_bundle = bundle;
			_objectClasses = objectClasses;
			_id = id;
			_ranking = ranking;

			_properties.put("objectClass", objectClasses);
			_properties.put("service.id", _id);
			_properties.put("service.ranking", _ranking);
		}

		@Override
		public int compareTo(Object object) {
			MockServiceReference other = (MockServiceReference)object;

			if (_ranking != other._ranking) {
				if (_ranking < other._ranking) {
					return -1;
				}
				return 1;
			}

			if (_id == other._id) {
				return 0;
			}

			if (_id < other._id) {
				return 1;
			}

			return -1;
		}

		@Override
		public Object getProperty(String key) {
			return _properties.get(key);
		}

		@Override
		public String[] getPropertyKeys() {
			return _properties.keySet().toArray(new String[0]);
		}

		@Override
		public Bundle getBundle() {
			return _bundle;
		}

		@Override
		public Bundle[] getUsingBundles() {
			return null;
		}

		@Override
		public boolean isAssignableTo(Bundle bundle, String className) {
			return false;
		}

		public void setProperties(Map<String, Object> properties) {
			_properties.putAll(properties);
			_properties.put("objectClass", _objectClasses);
			_properties.put("service.id", _id);
			_properties.put("service.ranking", _ranking);
		}

		private Bundle _bundle;
		private int _id;
		private String[] _objectClasses;
		private Map<String, Object> _properties = new HashMap<>();
		private int _ranking;

	}

}
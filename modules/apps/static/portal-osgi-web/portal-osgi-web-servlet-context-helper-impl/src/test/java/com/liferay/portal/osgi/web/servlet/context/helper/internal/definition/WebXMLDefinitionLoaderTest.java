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

package com.liferay.portal.osgi.web.servlet.context.helper.internal.definition;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.FilterDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ListenerDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.ServletDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.internal.order.OrderBeforeAndAfterException;
import com.liferay.portal.osgi.web.servlet.context.helper.internal.order.OrderCircularDependencyException;
import com.liferay.portal.osgi.web.servlet.context.helper.internal.order.OrderUtil;
import com.liferay.portal.osgi.web.servlet.context.helper.order.Order;

import aQute.bnd.osgi.Descriptors;

import java.io.File;

import java.net.URI;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mock;

import org.osgi.framework.Bundle;

/**
 * @author Miguel Pastor
 */
public class WebXMLDefinitionLoaderTest {

	@Test
	public void testLoadCustomWebAbsoluteOrdering1XML() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-absolute-ordering-1.xml");

		List<String> absoluteOrderingNames = new ArrayList<>();

		absoluteOrderingNames.add("fragment2");
		absoluteOrderingNames.add("fragment1");
		absoluteOrderingNames.add(Order.OTHERS);

		testWebXMLDefinition(
			webXMLDefinition, 1, 1, 1, null, null, absoluteOrderingNames);
	}

	@Test
	public void testLoadCustomWebAbsoluteOrdering1XMLMetadataIncomplete()
		throws Exception {

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-absolute-ordering-1.xml");

		Assert.assertFalse(webXMLDefinition.isMetadataComplete());
	}

	@Test
	public void testLoadCustomWebFragment1XML() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-fragment-1.xml");

		testWebXMLDefinition(
			webXMLDefinition, 1, 1, 0, "fragment1", null, null);
	}

	@Test
	public void testLoadCustomWebFragment2XML() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-fragment-2.xml");

		Order order = new Order();

		EnumMap<Order.Path, String[]> routes = order.getRoutes();

		routes.put(Order.Path.AFTER, new String[] {"fragment1"});

		testWebXMLDefinition(
			webXMLDefinition, 0, 0, 0, "fragment2", order, null);
	}

	@Test
	public void testLoadCustomWebFragment4XML() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-fragment-4.xml");

		Order order = new Order();

		EnumMap<Order.Path, String[]> routes = order.getRoutes();

		routes.put(Order.Path.BEFORE, new String[] {Order.OTHERS});

		testWebXMLDefinition(
			webXMLDefinition, 0, 0, 0, "fragment4", order, null);
	}

	@Test
	public void testLoadCustomWebXML() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		testWebXMLDefinition(webXMLDefinition, 1, 1, 1);
	}

	@Test
	public void testLoadCustomWebXMLMetadataComplete() throws Exception {
		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		Assert.assertTrue(webXMLDefinition.isMetadataComplete());
	}

	@Test
	public void testLoadWebXML() throws Exception {
		Bundle bundle = new MockBundle();
		Set<String> classes = Collections.emptySet();

		WebXMLDefinitionLoader webXMLDefinitionLoader =
			new WebXMLDefinitionLoader(
				bundle, null, SAXParserFactory.newInstance(), classes, classes);

		WebXMLDefinition webXMLDefinition =
			webXMLDefinitionLoader.loadWebXMLDefinition(
				bundle.getEntry("WEB-INF/web.xml"));

		testWebXMLDefinition(webXMLDefinition, 0, 0, 0);
	}

	@Test
	public void testLoadWebXMLReadAnnotations() throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Bundle bundle = new MockBundle() {

			@Override
			public URL getResource(String name) {
				return classLoader.getResource(name);
			}

		};

		Set<String> resources = getPackageResources("a");

		Set<String> classes = Collections.emptySet();

		WebXMLDefinitionLoader webXMLDefinitionLoader =
			new WebXMLDefinitionLoader(
				bundle, null, SAXParserFactory.newInstance(), resources, classes);

		WebXMLDefinition webXMLDefinition = webXMLDefinitionLoader.loadWebXML();

		testWebXMLDefinition(webXMLDefinition, 1, 1, 1);
	}

	@Test
	public void testOrderBeforeAndAfterException() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-5.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		boolean threwOrderBeforeAndAfterException = false;

		try {
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());
		}
		catch (Exception e) {
			if (e instanceof OrderBeforeAndAfterException) {
				threwOrderBeforeAndAfterException = true;
			}
		}

		Assert.assertTrue(threwOrderBeforeAndAfterException);
	}

	@Test
	public void testOrderCircularDependencyException() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition(
				"dependencies/custom-web-fragment-circular-1.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition(
				"dependencies/custom-web-fragment-circular-2.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		boolean threwOrderCircularDependencyException = false;

		try {
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());
		}
		catch (Exception e) {
			if (e instanceof OrderCircularDependencyException) {
				threwOrderCircularDependencyException = true;
			}
		}

		Assert.assertTrue(threwOrderCircularDependencyException);
	}

	@Test
	public void testOrderCustomWebFragments1() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-3.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-1.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-2.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-absolute-ordering-1.xml");

		List<WebXMLDefinition> orderedWebXMLDefinitions =
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());

		Assert.assertEquals(
			orderedWebXMLDefinitions.toString(), 3,
			orderedWebXMLDefinitions.size());

		WebXMLDefinition firstWebXMLDefinition = orderedWebXMLDefinitions.get(
			0);

		Assert.assertEquals(
			"fragment2", firstWebXMLDefinition.getFragmentName());

		WebXMLDefinition secondWebXMLDefinition = orderedWebXMLDefinitions.get(
			1);

		Assert.assertEquals(
			"fragment1", secondWebXMLDefinition.getFragmentName());

		WebXMLDefinition thirdWebXMLDefinition = orderedWebXMLDefinitions.get(
			2);

		Assert.assertEquals(
			"fragment3", thirdWebXMLDefinition.getFragmentName());
	}

	@Test
	public void testOrderCustomWebFragments2() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-3.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-2.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-1.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web-absolute-ordering-2.xml");

		List<WebXMLDefinition> orderedWebXMLDefinitions =
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());

		Assert.assertEquals(
			orderedWebXMLDefinitions.toString(), 2,
			orderedWebXMLDefinitions.size());

		WebXMLDefinition firstWebXMLDefinition = orderedWebXMLDefinitions.get(
			0);

		Assert.assertEquals(
			"fragment1", firstWebXMLDefinition.getFragmentName());

		WebXMLDefinition secondWebXMLDefinition = orderedWebXMLDefinitions.get(
			1);

		Assert.assertEquals(
			"fragment2", secondWebXMLDefinition.getFragmentName());
	}

	@Test
	public void testOrderCustomWebFragments3() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-3.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-2.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-1.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		List<WebXMLDefinition> orderedWebXMLDefinitions =
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());

		Assert.assertEquals(
			orderedWebXMLDefinitions.toString(), 3,
			orderedWebXMLDefinitions.size());

		WebXMLDefinition firstWebXMLDefinition = orderedWebXMLDefinitions.get(
			0);

		Assert.assertEquals(
			"fragment1", firstWebXMLDefinition.getFragmentName());

		WebXMLDefinition secondWebXMLDefinition = orderedWebXMLDefinitions.get(
			1);

		Assert.assertEquals(
			"fragment3", secondWebXMLDefinition.getFragmentName());

		WebXMLDefinition thirdWebXMLDefinition = orderedWebXMLDefinitions.get(
			2);

		Assert.assertEquals(
			"fragment2", thirdWebXMLDefinition.getFragmentName());
	}

	@Test
	public void testOrderCustomWebFragments4() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-2.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-1.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-4.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		List<WebXMLDefinition> orderedWebXMLDefinitions =
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());

		Assert.assertEquals(
			orderedWebXMLDefinitions.toString(), 3,
			orderedWebXMLDefinitions.size());

		WebXMLDefinition firstWebXMLDefinition = orderedWebXMLDefinitions.get(
			0);

		Assert.assertEquals(
			"fragment4", firstWebXMLDefinition.getFragmentName());

		WebXMLDefinition secondWebXMLDefinition = orderedWebXMLDefinitions.get(
			1);

		Assert.assertEquals(
			"fragment1", secondWebXMLDefinition.getFragmentName());

		WebXMLDefinition thirdWebXMLDefinition = orderedWebXMLDefinitions.get(
			2);

		Assert.assertEquals(
			"fragment2", thirdWebXMLDefinition.getFragmentName());
	}

	@Test
	public void testUnorderedWebFragments() throws Exception {
		List<WebXMLDefinition> webXMLDefinitions = new ArrayList<>();

		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-1.xml"));
		webXMLDefinitions.add(
			loadWebXMLDefinition("dependencies/custom-web-fragment-3.xml"));

		WebXMLDefinition webXMLDefinition = loadWebXMLDefinition(
			"dependencies/custom-web.xml");

		List<WebXMLDefinition> orderedWebXMLDefinitions =
			OrderUtil.getOrderedWebXMLDefinitions(
				webXMLDefinitions, webXMLDefinition.getAbsoluteOrderingNames());

		Assert.assertEquals(
			orderedWebXMLDefinitions.toString(), 2,
			orderedWebXMLDefinitions.size());

		WebXMLDefinition firstWebXMLDefinition = orderedWebXMLDefinitions.get(
			0);

		Assert.assertEquals(
			"fragment1", firstWebXMLDefinition.getFragmentName());

		WebXMLDefinition secondWebXMLDefinition = orderedWebXMLDefinitions.get(
			1);

		Assert.assertEquals(
			"fragment3", secondWebXMLDefinition.getFragmentName());
	}

	protected Set<String> getPackageResources(String subpackage)
		throws Exception {

		Class<?> clazz = getClass();

		Package packaze = clazz.getPackage();

		String packageName = packaze.getName();

		String packagePath = Descriptors.fqnToBinary(packageName);

		packagePath = packagePath + "/" + subpackage;

		URL url = clazz.getResource(subpackage);

		String protocol = url.getProtocol();

		if (!protocol.equals("file")) {
			throw new IllegalStateException(
				"Test classes are not on the file system");
		}

		String basePath = url.getPath();

		File baseDir = new File(basePath);

		String rootPath = basePath.substring(
			0, basePath.length() - packagePath.length());

		try (Stream<Path> stream = Files.find(
				baseDir.toPath(), 10,
				(path, basicFileAttributes) -> {
					File file = path.toFile();

					String fileName = file.getName();

					return !file.isDirectory() && fileName.endsWith(".class");
				})) {

			return stream.map(
				Path::toUri
			).map(
				URI::getPath
			).map(
				p -> p.substring(rootPath.length())
			).map(
				p -> p.substring(0, p.length() - 6)
			).map(
				Descriptors::binaryToFQN
			).collect(
				Collectors.toSet()
			);
		}
	}

	protected WebXMLDefinition loadWebXMLDefinition(String path)
		throws Exception {

		TestBundle testBundle = new TestBundle(path);
		Set<String> classes = Collections.emptySet();

		WebXMLDefinitionLoader webXMLDefinitionLoader =
			new WebXMLDefinitionLoader(
				testBundle, null, SAXParserFactory.newInstance(), classes,
				classes);

		return webXMLDefinitionLoader.loadWebXMLDefinition(testBundle.getURL());
	}

	protected void testWebXMLDefinition(
			WebXMLDefinition webXMLDefinition, int listenerDefinitionsCount,
			int filterDefinitionsCount, int servletDefinitionsCount)
		throws Exception {

		testWebXMLDefinition(
			webXMLDefinition, listenerDefinitionsCount, filterDefinitionsCount,
			servletDefinitionsCount, null, null, null);
	}

	protected void testWebXMLDefinition(
			WebXMLDefinition webXMLDefinition, int listenerDefinitionsCount,
			int filterDefinitionsCount, int servletDefinitionsCount,
			String fragmentName, Order order,
			List<String> absoluteOrderingNames)
		throws Exception {

		if (Validator.isNotNull(fragmentName)) {
			Assert.assertEquals(
				fragmentName, webXMLDefinition.getFragmentName());
		}

		if (order != null) {
			EnumMap<Order.Path, String[]> expectedRoutes = order.getRoutes();

			Order webXMLDefinitionOrder = webXMLDefinition.getOrder();

			EnumMap<Order.Path, String[]> actualRoutes =
				webXMLDefinitionOrder.getRoutes();

			Assert.assertArrayEquals(
				expectedRoutes.get(Order.Path.AFTER),
				actualRoutes.get(Order.Path.AFTER));
			Assert.assertArrayEquals(
				expectedRoutes.get(Order.Path.BEFORE),
				actualRoutes.get(Order.Path.BEFORE));
		}

		if (ListUtil.isNotEmpty(absoluteOrderingNames)) {
			List<String> webXMLDefinitionAbsoluteOrderingNames =
				webXMLDefinition.getAbsoluteOrderingNames();

			Assert.assertArrayEquals(
				absoluteOrderingNames.toArray(new String[0]),
				webXMLDefinitionAbsoluteOrderingNames.toArray(new String[0]));
		}

		List<ListenerDefinition> listenerDefinitions =
			webXMLDefinition.getListenerDefinitions();

		Assert.assertEquals(
			listenerDefinitions.toString(), listenerDefinitionsCount,
			listenerDefinitions.size());

		for (ListenerDefinition listenerDefinition : listenerDefinitions) {
			EventListener eventListener = listenerDefinition.getEventListener();

			Assert.assertTrue(eventListener instanceof ServletContextListener);
		}

		Map<String, ServletDefinition> servletDefinitions =
			webXMLDefinition.getServletDefinitions();

		Assert.assertEquals(
			servletDefinitions.toString(), servletDefinitionsCount,
			servletDefinitions.size());

		Map<String, FilterDefinition> filterDefinitions =
			webXMLDefinition.getFilterDefinitions();

		Assert.assertEquals(
			filterDefinitions.toString(), filterDefinitionsCount,
			filterDefinitions.size());
	}

	@Mock
	private Servlet _servlet;

	@Mock
	private ServletContextListener _servletContextListener;

	private static class TestBundle extends MockBundle {

		public TestBundle(String path) {
			_path = path;
		}

		public URL getURL() {
			Class<?> clazz = getClass();

			return clazz.getResource(_path);
		}

		private final String _path;

	}

}
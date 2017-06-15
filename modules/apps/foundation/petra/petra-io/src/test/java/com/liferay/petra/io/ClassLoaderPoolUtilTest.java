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

package com.liferay.petra.io;

import com.liferay.petra.io.internal.loader.DefaultClassLoaderPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ClassLoaderPoolUtilTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Before
	public void setUp() {
		_defaultClassLoaderPool = ReflectionTestUtil.getFieldValue(
			ClassLoaderPoolUtil.class, "_classLoaderPool");

		_classLoaders = ReflectionTestUtil.getFieldValue(
			_defaultClassLoaderPool, "_classLoaders");

		_classLoaders.clear();

		_contextNames = ReflectionTestUtil.getFieldValue(
			_defaultClassLoaderPool, "_contextNames");

		_contextNames.clear();
	}

	@Test
	public void testConstructor() throws Exception {
		Constructor<ClassLoaderPoolUtil> constructor =
			ClassLoaderPoolUtil.class.getDeclaredConstructor();

		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));

		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testGetClassLoaderWithInvalidContextName() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		Assert.assertSame(
			contextClassLoader,
			ClassLoaderPoolUtil.getClassLoader(StringPool.NULL));
		Assert.assertSame(
			contextClassLoader, ClassLoaderPoolUtil.getClassLoader(null));
	}

	@Test
	public void testGetClassLoaderWithValidContextName() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		Assert.assertSame(
			classLoader, ClassLoaderPoolUtil.getClassLoader(_CONTEXT_NAME));
	}

	@Test
	public void testGetContextNameWithInvalidClassLoader() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		Assert.assertEquals(
			StringPool.NULL,
			ClassLoaderPoolUtil.getContextName(new URLClassLoader(new URL[0])));
		Assert.assertEquals(
			StringPool.NULL, ClassLoaderPoolUtil.getContextName(null));
	}

	@Test
	public void testGetContextNameWithValidClassLoader() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		Assert.assertEquals(
			_CONTEXT_NAME, ClassLoaderPoolUtil.getContextName(classLoader));
	}

	@Test
	public void testRegister() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		Assert.assertEquals(_contextNames.toString(), 1, _contextNames.size());
		Assert.assertEquals(_classLoaders.toString(), 1, _classLoaders.size());
		Assert.assertSame(classLoader, _classLoaders.get(_CONTEXT_NAME));
		Assert.assertEquals(_CONTEXT_NAME, _contextNames.get(classLoader));
	}

	@Test
	public void testRegisterWithNullClassLoader() {
		try {
			ClassLoaderPoolUtil.register(StringPool.BLANK, null);

			Assert.fail();
		}
		catch (NullPointerException npe) {
		}
	}

	@Test
	public void testRegisterWithNullContextName() {
		try {
			ClassLoaderPoolUtil.register(null, null);

			Assert.fail();
		}
		catch (NullPointerException npe) {
		}
	}

	@Test
	public void testUnregisterWithInvalidClassLoader() {
		ClassLoaderPoolUtil.unregister(new URLClassLoader(new URL[0]));

		assertEmptyMaps();
	}

	@Test
	public void testUnregisterWithInvalidContextName() {
		ClassLoaderPoolUtil.unregister(_CONTEXT_NAME);

		assertEmptyMaps();
	}

	@Test
	public void testUnregisterWithValidClassLoader() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		ClassLoaderPoolUtil.unregister(classLoader);

		assertEmptyMaps();
	}

	@Test
	public void testUnregisterWithValidContextName() {
		ClassLoader classLoader = new URLClassLoader(new URL[0]);

		ClassLoaderPoolUtil.register(_CONTEXT_NAME, classLoader);

		ClassLoaderPoolUtil.unregister(_CONTEXT_NAME);

		assertEmptyMaps();
	}

	protected void assertEmptyMaps() {
		Assert.assertTrue(_contextNames.isEmpty());
		Assert.assertTrue(_classLoaders.isEmpty());
	}

	private static final String _CONTEXT_NAME = "contextName";

	private static DefaultClassLoaderPool _defaultClassLoaderPool;
	private static Map<String, ClassLoader> _classLoaders;
	private static Map<ClassLoader, String> _contextNames;

}
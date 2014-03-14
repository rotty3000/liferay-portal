/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.osgi.http.internal;

import com.liferay.osgi.http.internal.context.ServletContextImpl;
import com.liferay.osgi.util.TU;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpConstants;
import org.osgi.service.http.runtime.ServletContextDTO;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Raymond Augé
 */
@PrepareForTest({FrameworkUtil.class})
@RunWith(PowerMockRunner.class)
public class HttpServiceImplTest extends BaseHttpTesting {

	@Test
	public void testGetHttpServiceAttributes() {
		Map<String, Object> attributes = _httpServiceImpl.getAttributes();

		Assert.assertNotNull(attributes);

		String[] endpoints = (String[])attributes.get(
			HttpConstants.HTTP_SERVICE_ENDPOINT_ATTRIBUTE);

		Arrays.sort(endpoints);

		int result = Arrays.binarySearch(endpoints, "/o");

		Assert.assertTrue(result >= 0);
	}

	@Test
	public void testGetHttpServiceDTOs() {
		ServletContextDTO[] servletContextDTOs =
			_httpServiceImpl.getServletContextDTOs();

		Assert.assertNotNull(servletContextDTOs);

		Assert.assertTrue(servletContextDTOs.length > 0);

		ServletContextDTO servletContextDTO = servletContextDTOs[0];

		Assert.assertEquals("", servletContextDTO.contextName);
		Assert.assertEquals("", servletContextDTO.contextPath);
		Assert.assertTrue(servletContextDTO.shared);
	}

	@Test
	public void testGetContextPath() {
		String contextPath = _httpServiceImpl.getContextPath();

		Assert.assertNotNull(contextPath);

		Assert.assertEquals("/o", contextPath);
	}

	@Test
	public void testGetServletContext_Default() {
		Set<ServletContextImpl> servletContexts =
			_httpServiceImpl.getServletContexts("");

		Assert.assertFalse(servletContexts.isEmpty());

		Assert.assertEquals(
			"", servletContexts.iterator().next().getContextPath());
	}

	@Test
	public void testResettingServletContext_Default() {
		Set<ServletContextImpl> servletContexts =
			_httpServiceImpl.getServletContexts("");

		int length = _httpServiceImpl.getServletContextDTOs().length;

		ServletContextImpl servletContextImpl =
			servletContexts.iterator().next();

		_httpServiceImpl.setServletContextHelper(
			servletContextImpl.getServletContextHelper(),
			servletContextImpl.getInitParameters());

		Assert.assertEquals(
			length, _httpServiceImpl.getServletContextDTOs().length);
	}

	@Test
	public void testRemoveServletContext_Default() {
		Set<ServletContextImpl> servletContexts =
			_httpServiceImpl.getServletContexts("");

		int length = _httpServiceImpl.getServletContextDTOs().length;

		ServletContextImpl servletContextImpl =
			servletContexts.iterator().next();

		_httpServiceImpl.unsetServletContextHelper(
			servletContextImpl.getServletContextHelper(),
			servletContextImpl.getInitParameters());

		Assert.assertEquals(
			length, _httpServiceImpl.getServletContextDTOs().length);
	}

	@Test
	public void testSetGetServletContext_Other_NoPath() {
		_httpServiceImpl.setServletContextHelper(
			_servletContextHelper,
			TU.serviceMap(HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "other"));

		Set<ServletContextImpl> servletContextImpls =
			_httpServiceImpl.getServletContexts("");

		Assert.assertFalse(servletContextImpls.isEmpty());

		ServletContextImpl servletContextImpl =
			servletContextImpls.iterator().next();

		Assert.assertEquals("", servletContextImpl.getContextPath());
	}

	@Test
	public void testSetGetServletContext_Other_WithPath() {
		_httpServiceImpl.setServletContextHelper(
			_servletContextHelper,
			TU.serviceMap(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "other").add(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/other"));

		Set<ServletContextImpl> servletContextImpls =
			_httpServiceImpl.getServletContexts("/other");

		Assert.assertFalse(servletContextImpls.isEmpty());

		ServletContextImpl servletContextImpl =
			servletContextImpls.iterator().next();

		Assert.assertEquals("/other", servletContextImpl.getContextPath());
	}

}
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

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.PortletParameterUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Sampsa Sohlman
 */
public class DynamicServletRequestTest {

	@Before
	public void setUp() {
		mockRequest = new MockHttpServletRequest();
	}

	@Test
	public void testAddPortletDynamicServletRequest() throws Exception {
		doTestAddQueryString(true, false, false);
	}

	@Test
	public void testAddPortletDynamicServletRequestRemovePpId()
		throws Exception {

		doTestAddQueryString(true, false, true);
	}

	@Test
	public void testAddPortletDynamicServletRequestWithInherit()
		throws Exception {

		doTestAddQueryString(true, true, false);
	}

	@Test
	public void testAddPortletDynamicServletRequestWithInheritRemovePpId()
		throws Exception {

		doTestAddQueryString(true, true, true);
	}

	@Test
	public void testAddPortletHttpRequestWithInherit() throws Exception {
		doTestAddQueryString(false, true, false);
	}

	@Test
	public void testAddPortletHttpRequestWithInheritRemovePpId()
		throws Exception {

		doTestAddQueryString(false, true, true);
	}

	@Test
	public void testAddPortletHttpServletRequest() throws Exception {
		doTestAddQueryString(false, false, false);
	}

	@Test
	public void testAddPortletHttpServletRequestRemovePpId() throws Exception {
		doTestAddQueryString(false, false, true);
	}

	protected void assertMultiValues(
		HttpServletRequest request, String name, String[] values) {

		int count = 0;

		for (String paramValue : request.getParameterValues(name)) {
			for (String value : values) {
				if (paramValue.equals(value)) {
					count++;
				}
			}
		}

		Assert.assertEquals(values.length, count);
	}

	protected void assertNames(Set names, boolean exist) {
		Assert.assertEquals(exist, names.contains("p_p_mode"));
		Assert.assertEquals(exist, names.contains("p_p_state"));
		Assert.assertEquals(exist, names.contains("p_p_lifecycle"));
		Assert.assertEquals(exist, names.contains("p_p_anything"));
		Assert.assertEquals(exist, names.contains("other"));
	}

	protected void doTestAddQueryString(
			boolean isDynamic, boolean inherit, boolean removePortletParameters)
		throws Exception {

		mockRequest.addParameter("p_p_id", "16");
		mockRequest.addParameter(
			"p_p_mode", LiferayPortletMode.EDIT.toString());
		mockRequest.addParameter("p_p_state", "normal");
		mockRequest.addParameter("p_p_lifecycle", "1");
		mockRequest.addParameter("p_p_anything", "absc");
		mockRequest.addParameter("other", "otherValue");
		mockRequest.addParameter("_145_multi0", new String[]{"multi0value1"});

		HttpServletRequest request = mockRequest;

		if (isDynamic) {
			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("_145_multi1", new String[]{"multi1value1"});
			map.put("_145_multi2", new String[]{"multi2value1"});
			map.put(
				"_145_multi3", new String[]{"multi3value1", "multi3value2"});
			request  = new DynamicServletRequest(mockRequest, map);
		}

		String queryString = PortletParameterUtil.addNamespace(
			"145", "abc=123&xyz=890&multi2=multi2value2&multi2=multi2value3&" +
			"multi3=multi3value3");

		request = DynamicServletRequest.addQueryString(
			request, queryString, inherit, removePortletParameters);

		Set names = new HashSet(Collections.list(request.getParameterNames()));

		Set mapNames = request.getParameterMap().keySet();

		Assert.assertEquals("145", request.getParameter("p_p_id"));
		Assert.assertEquals("123", request.getParameter("_145_abc"));
		Assert.assertEquals("890", request.getParameter("_145_xyz"));
		Assert.assertTrue(names.contains("p_p_id"));
		Assert.assertTrue(names.contains("_145_abc"));
		Assert.assertTrue(names.contains("_145_xyz"));

		if (inherit) {
			Assert.assertEquals(
				removePortletParameters,
				request.getParameter("p_p_mode")== null);

			Assert.assertEquals(
				removePortletParameters,
				request.getParameter("p_p_state")== null);

			Assert.assertEquals(
				removePortletParameters,
				request.getParameter("p_p_lifecycle")== null);

			Assert.assertEquals(
				removePortletParameters,
				request.getParameter("p_p_anything")== null);

			Assert.assertEquals("otherValue", request.getParameter("other"));

			assertMultiValues(
				request, "_145_multi0", new String[]{"multi0value1"});

			if (isDynamic) {
				assertMultiValues(
					request, "_145_multi1", new String[]{"multi1value1"});
				assertMultiValues(
					request, "_145_multi2",
					new String[]{"multi2value1", "multi2value2"});
				assertMultiValues(
					request, "_145_multi3",
					new String[]{"multi3value1", "multi3value2",
						"multi3value3"});
			}

			Assert.assertEquals(
				!removePortletParameters, names.contains("p_p_mode"));

			Assert.assertEquals(
				!removePortletParameters, names.contains("p_p_state"));

			Assert.assertEquals(
				!removePortletParameters, names.contains("p_p_lifecycle"));

			Assert.assertEquals(
				!removePortletParameters, names.contains("p_p_anything"));

			Assert.assertEquals(true, names.contains("other"));

			Assert.assertEquals(
				!removePortletParameters, mapNames.contains("p_p_mode"));

			Assert.assertEquals(
				!removePortletParameters, mapNames.contains("p_p_state"));

			Assert.assertEquals(
				!removePortletParameters, mapNames.contains("p_p_lifecycle"));

			Assert.assertEquals(
				!removePortletParameters, mapNames.contains("p_p_anything"));

			Assert.assertEquals(true, mapNames.contains("other"));
		}
		else {
			Assert.assertNull(request.getParameter("p_p_mode"));
			Assert.assertNull(request.getParameter("p_p_state"));
			Assert.assertNull(request.getParameter("p_p_lifecycle"));
			Assert.assertNull(request.getParameter("p_p_anything"));
			Assert.assertNull(request.getParameter("other"));

			assertNames(names, false);
			assertNames(mapNames, false);
		}
	}

	protected MockHttpServletRequest mockRequest;

}
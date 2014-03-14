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

import com.liferay.osgi.util.TU;
import com.liferay.osgi.util.TU.HandyMap;

import java.io.IOException;

import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpConstants;
import org.osgi.service.http.NamespaceException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Raymond Augé
 */
@PrepareForTest({FrameworkUtil.class})
@RunWith(PowerMockRunner.class)
@SuppressWarnings("deprecation")
public class BridgeServletTest extends BaseHttpTesting {

	@Test
	public void testNotFound() throws IOException, ServletException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/a");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_bridgeServlet.service(request, response);

		Assert.assertTrue(
			HttpServletResponse.SC_NOT_FOUND == response.getStatus());
	}

	@Test
	public void testFound() throws IOException, ServletException {
		TestServlet testServlet = new TestServlet();
		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");

		_servletCollector.setServlet(testServlet, serviceMap);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_bridgeServlet.service(request, response);

		Assert.assertTrue(
			HttpServletResponse.SC_OK == response.getStatus());
		Assert.assertEquals("hello", response.getContentAsString());
	}

	@Test
	public void testFound_2() throws IOException, ServletException {
		TestServlet testServlet = new TestServlet();
		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b/*");

		_servletCollector.setServlet(testServlet, serviceMap);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/b/some.txt");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_bridgeServlet.service(request, response);

		Assert.assertTrue(
			HttpServletResponse.SC_OK == response.getStatus());
		Assert.assertEquals("hello", response.getContentAsString());
	}

	@Test
	public void testFound_3() throws IOException, ServletException {
		TestServlet testServlet = new TestServlet();
		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "*.txt");

		_servletCollector.setServlet(testServlet, serviceMap);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/some.txt");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_bridgeServlet.service(request, response);

		Assert.assertTrue(
			HttpServletResponse.SC_OK == response.getStatus());
		Assert.assertEquals("hello", response.getContentAsString());
	}

	@Test
	public void testRegUnreg() {

		// First check without the service, should get 404

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		try {
			_bridgeServlet.service(request, response);

			Assert.assertTrue(
				HttpServletResponse.SC_NOT_FOUND == response.getStatus());
		}
		catch (Exception e) {
			Assert.fail();
		}

		// Now register the servlet and make sure the request succeeds

		TestServlet testServlet = new TestServlet();
		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b");

		_servletCollector.setServlet(testServlet, serviceMap);

		Assert.assertTrue(testServlet.inited.get());

		request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		try {
			_bridgeServlet.service(request, response);

			Assert.assertTrue(
				HttpServletResponse.SC_OK == response.getStatus());
			Assert.assertEquals("hello", response.getContentAsString());
		}
		catch (Exception e) {
			Assert.fail();
		}

		// Now unset the servlet. This should trigger destroy.

		_servletCollector.unsetServlet(testServlet, serviceMap);

		Assert.assertFalse(testServlet.inited.get());

		// Redo the request, get 404

		request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		try {
			_bridgeServlet.service(request, response);

			Assert.assertTrue(
				HttpServletResponse.SC_NOT_FOUND == response.getStatus());
		}
		catch (Exception e) {
			Assert.fail();
		}

		// Re-register, make sure init triggered.

		_servletCollector.setServlet(testServlet, serviceMap);

		Assert.assertTrue(testServlet.inited.get());

		request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		try {
			_bridgeServlet.service(request, response);

			Assert.assertTrue(
				HttpServletResponse.SC_OK == response.getStatus());
			Assert.assertEquals("hello", response.getContentAsString());
		}
		catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testMappedToMissingContext()
		throws IOException, ServletException {

		TestServlet testServlet = new TestServlet();
		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b").add(
			HttpConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "other");

		_servletCollector.setServlet(testServlet, serviceMap);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_bridgeServlet.service(request, response);

		Assert.assertTrue(
			HttpServletResponse.SC_NOT_FOUND == response.getStatus());
	}

	@Test
	public void testMappedWithContext()
		throws IOException, ServletException {

		_httpServiceImpl.setServletContextHelper(
			_servletContextHelper,
			TU.serviceMap(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "other"));

		TestServlet testServlet = new TestServlet();

		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b").add(
			HttpConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "other");

		_servletCollector.setServlet(testServlet, serviceMap);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/b");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		try {
			_bridgeServlet.service(request, response);

			System.out.println("Status: " + response.getStatus());
			System.out.println("Content: " + response.getContentAsString());

			Assert.assertTrue(
				HttpServletResponse.SC_OK == response.getStatus());
			Assert.assertEquals("hello", response.getContentAsString());
		}
		catch (Exception e) {
			e.printStackTrace();

			Assert.fail();
		}
	}

	@Test
	public void testMappedWithContextAndPath()
		throws IOException, ServletException {

		_httpServiceImpl.setServletContextHelper(
			_servletContextHelper,
			TU.serviceMap(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "other").add(
				HttpConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/other"));

		TestServlet testServlet = new TestServlet();
		HandyMap serviceMap = TU.serviceMap(
			HttpConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/b").add(
			HttpConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "other");

		_servletCollector.setServlet(testServlet, serviceMap);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/other/b");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		try {
			_bridgeServlet.service(request, response);

			Assert.assertTrue(
				HttpServletResponse.SC_OK == response.getStatus());
			Assert.assertEquals("hello", response.getContentAsString());
		}
		catch (Exception e) {
			Assert.fail();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRegisterResource() throws NamespaceException {
		AtomicReference<Object[]> args = new AtomicReference<Object[]>();

		mockRegisterService(args);

		_httpServiceFactory.registerResources("/blah", "/", null);

		Mockito.verify(_bundleContext).registerService(
			(Class)args.get()[0], args.get()[1], (Dictionary)args.get()[2]);

		_httpServiceFactory.unregister("/blah");

		Mockito.verify(_serviceRegistration).unregister();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRequestResource()
		throws IOException, NamespaceException, ServletException {

		AtomicReference<Object[]> args = new AtomicReference<Object[]>();

		mockRegisterService(args);

		_httpServiceFactory.registerResources("/blah", "/", null);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/o/blah");
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		_bridgeServlet.service(request, response);

		Assert.assertTrue(
			HttpServletResponse.SC_OK == response.getStatus());
		Assert.assertEquals("hello", response.getContentAsString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRegisterServlet()
		throws NamespaceException, ServletException {

		AtomicReference<Object[]> args = new AtomicReference<Object[]>();

		mockRegisterService(args);

		TestServlet testServlet = new TestServlet();

		_httpServiceFactory.registerServlet("/blah", testServlet, null, null);

		Mockito.verify(_bundleContext).registerService(
			(Class)args.get()[0], args.get()[1], (Dictionary)args.get()[2]);

		Assert.assertTrue(testServlet.inited.get());

		_httpServiceFactory.unregister("/blah");

		Mockito.verify(_serviceRegistration).unregister();

		Assert.assertFalse(testServlet.inited.get());
	}

}
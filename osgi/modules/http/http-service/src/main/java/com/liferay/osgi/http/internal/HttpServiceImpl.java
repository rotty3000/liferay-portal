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

import com.liferay.osgi.http.engine.HttpEngine;
import com.liferay.osgi.http.internal.context.DefaultServletContextHelper;
import com.liferay.osgi.http.internal.context.ServletContextHelperProperties;
import com.liferay.osgi.http.internal.context.ServletContextImpl;
import com.liferay.osgi.http.internal.servlet.ServletProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.http.HttpConstants;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.ServletContextHelper;
import org.osgi.service.http.runtime.HttpServiceRuntime;
import org.osgi.service.http.runtime.ServletContextDTO;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	service = {
		HttpService.class, HttpServlet.class, HttpServiceImpl.class
	}
)
@SuppressWarnings("deprecation")
public class HttpServiceImpl extends HttpServlet implements HttpServiceRuntime {

	public HttpServiceImpl() {
		_contextNameMap = new ConcurrentHashMap<String, ServletContextImpl>();
		_contextPathMap =
			new ConcurrentHashMap<String, Set<ServletContextImpl>>();
		_lock = new ReentrantLock(true);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return _httpServiceProperties.getRawProperties();
	}

	public RequestDispatcher getRequestDispatcher(String requestURI) {
		String parentContextPath = getContextPath();

		if (requestURI.indexOf(parentContextPath) == 0) {
			requestURI = requestURI.substring(parentContextPath.length());
		}

		String originalRequestURI = requestURI;

		int pos = requestURI.lastIndexOf('/');

		do {
			Set<ServletContextImpl> servletContextImpls = getServletContexts(
				requestURI);

			if (servletContextImpls != null) {
				for (ServletContextImpl servletContextImpl :
						servletContextImpls) {

					String subURI = originalRequestURI.substring(
						requestURI.length());

					RequestDispatcher requestDispatcher =
						servletContextImpl.getRequestDispatcher(subURI);

					if (requestDispatcher != null) {
						return requestDispatcher;
					}
				}
			}

			if (pos > -1) {
				requestURI = requestURI.substring(0, pos);
				pos = requestURI.lastIndexOf('/');

				continue;
			}

			break;
		}
		while (true);

		return null;
	}

	public ServletContextImpl getServletContext(
		ServletContextHelperProperties properties) {

		String contextName = properties.getContextName();

		return _contextNameMap.get(contextName);
	}

	public ServletContextImpl getServletContext(
		ServletProperties properties) {

		String contextName = properties.getContextName();

		return _contextNameMap.get(contextName);
	}

	@Override
	public ServletContextDTO[] getServletContextDTOs() {
		_lock.lock();

		try {
			List<ServletContextDTO> servletContextDTOs =
				new ArrayList<ServletContextDTO>();

			for (Map.Entry<String, ServletContextImpl> entry :
					_contextNameMap.entrySet()) {

				ServletContextImpl context = entry.getValue();

				servletContextDTOs.add(context.toDTO());
			}

			return servletContextDTOs.toArray(
				new ServletContextDTO[servletContextDTOs.size()]);
		}
		finally {
			_lock.unlock();
		}
	}

	@Activate
	protected void activate(
			ComponentContext componentContext, Map<String, Object> properties)
		throws InvalidSyntaxException {

		_lock.lock();

		try {
			_componentContext = componentContext;

			properties = new HashMap<String, Object>(properties);

			properties.put(
				HttpConstants.HTTP_SERVICE_ENDPOINT_ATTRIBUTE,
				_httpEngine.getHttpServiceEndpoints());

			_dschProperties = ServletContextHelperProperties.cnv(properties);

			_defaultServletContextHelper = new DefaultServletContextHelper(
				_componentContext.getBundleContext().getBundle(),
				_dschProperties.getContextName());

			_httpServiceProperties = HttpServiceProperties.cnv(properties);

			Bundle bundle = _componentContext.getBundleContext().getBundle();

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			ServletContextImpl servletContext = new ServletContextImpl(
				_httpEngine.getServletContext(),
				_defaultServletContextHelper,
				_dschProperties, bundleWiring.getClassLoader());

			_contextNameMap.put("", servletContext);

			Set<ServletContextImpl> contextSet = getContextSet(
				_dschProperties.getContextPath(), true);

			contextSet.add(servletContext);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			_lock.unlock();
		}
	}

	@Deactivate
	protected void deactivate() {
		_lock.lock();

		try {
			_contextNameMap.clear();

			_componentContext = null;
		}
		finally {
			_lock.unlock();
		}
	}

	protected String getContextPath() {
		return _httpServiceProperties.getContextPath();
	}

	protected Set<ServletContextImpl> getServletContexts(String contextPath) {
		return _contextPathMap.get(contextPath);
	}

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY
	)
	protected void setHttpEngine(HttpEngine httpEngine) {
		_httpEngine = httpEngine;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(" + HttpConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=*)"
	)
	protected void setServletContextHelper(
		ServletContextHelper servletContextHelper,
		Map<String, Object> properties) {

		_lock.lock();

		try {
			if (_defaultServletContextHelper == servletContextHelper) {
				return;
			}

			ServletContextHelperProperties schProperties =
				ServletContextHelperProperties.cnv(properties);

			String contextName = schProperties.getContextName();

			if (_contextNameMap.containsKey(contextName)) {
				return;
			}

			Bundle bundle = FrameworkUtil.getBundle(
				servletContextHelper.getClass());

			BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

			ServletContextImpl servletContext = new ServletContextImpl(
				_httpEngine.getServletContext(), servletContextHelper,
				schProperties, bundleWiring.getClassLoader());

			_contextNameMap.put(contextName, servletContext);

			String contextPath = schProperties.getContextPath();

			Set<ServletContextImpl> contextSet = getContextSet(
				contextPath, true);

			contextSet.add(servletContext);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			_lock.unlock();
		}
	}

	protected void unsetHttpEngine(HttpEngine httpEngine) {
		_httpEngine = null;
	}

	protected void unsetServletContextHelper(
		ServletContextHelper servletContextHelper,
		Map<String, Object> properties) {

		_lock.lock();

		try {
			if (_defaultServletContextHelper == servletContextHelper) {
				return;
			}

			ServletContextHelperProperties props =
				ServletContextHelperProperties.cnv(properties);

			ServletContextImpl servletContextImpl = _contextNameMap.remove(
				props.getContextName());

			if (servletContextImpl == null) {
				return;
			}

			Set<ServletContextImpl> contextSet = getContextSet(
				props.getContextPath(), false);

			if (contextSet != null) {
				contextSet.remove(servletContextImpl);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			_lock.unlock();
		}
	}

	protected Set<ServletContextImpl> getContextSet(String path, boolean add) {
		Set<ServletContextImpl> set = _contextPathMap.get(path);

		if ((set != null) || !add) {
			return set;
		}

		set = new HashSet<ServletContextImpl>();

		_contextPathMap.putIfAbsent(path, set);

		return set;
	}

	private ComponentContext _componentContext;
	private ConcurrentMap<String, ServletContextImpl> _contextNameMap;
	private ConcurrentMap<String, Set<ServletContextImpl>> _contextPathMap;
	private ServletContextHelper _defaultServletContextHelper;
	private ServletContextHelperProperties _dschProperties;
	private HttpEngine _httpEngine;
	private HttpServiceProperties _httpServiceProperties;
	private ReentrantLock _lock;

}
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

package com.liferay.spa.senna;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypeController;
import com.liferay.portal.theme.ThemeDisplay;

import java.net.URI;

import java.util.EventListener;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"layout.type=senna-saa",
		"osgi.http.whiteboard.context.select=senna",
		"osgi.http.whiteboard.resource.prefix=/META-INF/resources",
		"osgi.http.whiteboard.servlet.pattern=/"
	},
	service = {EventListener.class, LayoutTypeController.class, Servlet.class}
)
public class SimpleSAAController extends HttpServlet
	implements LayoutTypeController, ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		_servletContext = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		_servletContext = servletContextEvent.getServletContext();
	}

	@Override
	public String[] getConfigurationActionDelete() {
		return _EMPTY_ARRAY;
	}

	@Override
	public String[] getConfigurationActionUpdate() {
		return _EMPTY_ARRAY;
	}

	/**
	 * You only need this to return a value if there is a configuration UI
	 * component to this controller.
	 *
	 * The portal has to be able to perform a dispatch to this url. In order to
	 * be able to provide such a UI, the implemetor would have to provide a
	 * servlet which can handle the dispatch.
	 *
	 * @return the path to a resource for the edit layout UI
	 */
	@Override
	public String getEditPage() {
		return null;
	}

	@Override
	public String getURL() {
		return null;
	}

	/**
	 * Generate the content of the type.
	 *
	 * @param request the {@link HttpServletRequest}
	 * @param response the {@link HttpServletRequest}
	 * @param themeDisplay the {@link ThemeDisplay}
	 * @param portletId the portlet id
	 * @return true if the result should be wrapped by the portal decorations
	 *         (only valid if the return type is text/html
	 */
	@Override
	public boolean includeLayoutContent(
			HttpServletRequest request, HttpServletResponse response,
			Layout layout)
		throws Exception {

		// Get the layoutPath.

		String currentLayoutPath = getLayoutPath(request, layout);

		String pathInfo = getPathInfo(request, currentLayoutPath);

		if (Validator.isNull(pathInfo)) {

			// In order to simplify the HTML href references, always make the
			// root URI of the page behave like a directory.

			response.sendRedirect(currentLayoutPath + StringPool.SLASH);

			return true;
		}

		if (pathInfo.endsWith(StringPool.SLASH)) {
			pathInfo = pathInfo + "index.html";
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(pathInfo);

		requestDispatcher.forward(request, response);

		return true;
	}

	/**
	 * @return true if the type implemented can be the first page of the site
	 */
	@Override
	public boolean isFirstPageable() {
		return true;
	}

	/**
	 * @return true if the type implemented can have child pages
	 */
	@Override
	public boolean isParentable() {
		return false;
	}

	/**
	 * @return if the type implemented can appear in the sitemap
	 */
	@Override
	public boolean isSitemapable() {
		return true;
	}

	/**
	 * @return true if the type implemented can have friendly urls
	 */
	@Override
	public boolean isURLFriendliable() {
		return true;
	}

	/**
	 * Used to determine if this type can take effect if it matches certain
	 * criteria. Any of the input data can be used as criteria.
	 */
	@Override
	public boolean matches(
		HttpServletRequest request, String friendlyURL, Layout layout) {

		return true;
	}

	@Activate
	protected void activate() {
		System.out.println(this + " activated!");
	}

	@Deactivate
	protected void deactivate() {
		System.out.println(this + " deactivated!");
	}

	protected String getLayoutPath(
			HttpServletRequest request, Layout layout)
		throws PortalException {

		URI uri = URI.create(layout.getRegularURL(request));

		return uri.getPath();
	}

	protected String getPathInfo(HttpServletRequest request, String layoutPath) {
		String requestURI = getRequestURI(request);

		return requestURI.substring(layoutPath.length());
	}

	protected String getRequestURI(HttpServletRequest request) {
		String requestURI = request.getRequestURI();

		// We'll always have been forwarded

		String forwardRequestURI = (String)request.getAttribute(
			RequestDispatcher.FORWARD_REQUEST_URI);

		if (forwardRequestURI != null) {
			requestURI = forwardRequestURI;
		}

		return requestURI;
	}

	private static final String[] _EMPTY_ARRAY = new String[0];

	protected ServletContext _servletContext;

}
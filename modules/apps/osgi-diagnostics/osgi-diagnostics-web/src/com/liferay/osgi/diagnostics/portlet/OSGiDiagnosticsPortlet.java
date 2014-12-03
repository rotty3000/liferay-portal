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

package com.liferay.osgi.diagnostics.portlet;

import com.liferay.osgi.diagnostics.model.IntegrationPoint;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ReflectionUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.service.component.annotations.Component;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-osgi-diagnostics",
		"com.liferay.portlet.control-panel-entry-category=configuration",
		"com.liferay.portlet.control-panel-entry-weight=11",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = {ListenerHook.class, Portlet.class}
)
public class OSGiDiagnosticsPortlet extends MVCPortlet
	implements ListenerHook {

	@Override
	public void added(Collection<ListenerInfo> listeners) {
		_listeners.addAll(listeners);
	}

	@Override
	public void removed(Collection<ListenerInfo> listeners) {
		_listeners.removeAll(listeners);
	}

	@Override
	public void doView(
		RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			"integrationPoints", collectIntegrationPointInfos());

		super.doView(renderRequest, renderResponse);
	}

	private void collectIntegrationPointInfo(
		Map<String, IntegrationPoint> integrationPoints, ListenerInfo listenerInfo) {

		try {
			String filterString = listenerInfo.getFilter();

			if (filterString == null) {
				return;
			}

			Matcher matcher = _pattern.matcher(filterString);

			while (matcher.find()) {
				String objectClass = matcher.group(1);

				Filter filter = FrameworkUtil.createFilter(filterString);

				if (!filter.matches(
					Collections.singletonMap("objectClass", objectClass))) {

					continue;
				}

				IntegrationPoint integrationPoint = integrationPoints.get(
					objectClass);

				if (integrationPoint == null) {
					integrationPoint = new IntegrationPoint(objectClass);

					integrationPoints.put(objectClass, integrationPoint);
				}

				Set<String> filters = integrationPoint.getFilters();

				filters.add(filterString);
			}
		}
		catch (Exception e) {
			ReflectionUtil.throwException(e);
		}
	}

	private List<IntegrationPoint> collectIntegrationPointInfos() {
		Map<String, IntegrationPoint> integrationPoints = new HashMap<>();

		for (ListenerInfo listenerInfo : _listeners) {
			collectIntegrationPointInfo(integrationPoints, listenerInfo);
		}

		List<IntegrationPoint> integrationPointList = new ArrayList<>(
			integrationPoints.values());

		Collections.sort(integrationPointList);

		return integrationPointList;
	}

	protected final List<ListenerInfo> _listeners =
		new CopyOnWriteArrayList<>();
	protected final Pattern _pattern = Pattern.compile("\\(objectClass=([^)]+)\\)");

}
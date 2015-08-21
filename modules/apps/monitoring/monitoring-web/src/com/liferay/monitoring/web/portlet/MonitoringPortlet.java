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

package com.liferay.monitoring.web.portlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

/**
 * @author Philip Jones
 */
@Component(
        immediate = true,
        property = {
                "com.liferay.portlet.icon=/icons/monitoring.png",
                "com.liferßay.portlet.control-panel-entry-category=users",
                "com.liferay.portlet.control-panel-entry-weight=5.0",
                "com.liferay.portlet.preferences-owned-by-group=true",
                "com.liferay.portlet.use-default-template=true",
                "com.liferay.portlet.private-request-attributes=false",
                "com.liferay.portlet.private-session-attributes=false",
                "com.liferay.portlet.render-weight=50",
                "com.liferay.portlet.header-portlet-css=/users_admin/css/main.css",
                "com.liferay.portlet.css-class-wrapper=portlet-users-admin",
                "javax.portlet.portlet-name=com_liferay_monitoring_web_portlet_MonitoringßPortlet",
                "javax.portlet.display-name=Monitoring",
                "javax.portlet.init-param.view-template=/monitoring/view.jsp",
                "javax.portlet.expiration-cache=0",
                "javax.portlet.supports.mime-type=text/html",
                "javax.portlet.resource-bundle=content.Language",
                "javax.portlet.security-role-ref=administrator",
       },
        service = Portlet.class
)
public class MonitoringPortlet extends MVCPortlet {
}
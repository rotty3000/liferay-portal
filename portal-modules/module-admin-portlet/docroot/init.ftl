<#--
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
-->

<#assign aui =                           PortletJspTagLibs["/WEB-INF/tld/aui.tld"] />
<#assign liferay_portlet =               PortletJspTagLibs["/WEB-INF/tld/liferay-portlet-ext.tld"] />
<#assign liferay_security =              PortletJspTagLibs["/WEB-INF/tld/liferay-security.tld"] />
<#assign liferay_theme =                 PortletJspTagLibs["/WEB-INF/tld/liferay-theme.tld"] />
<#assign liferay_ui =                    PortletJspTagLibs["/WEB-INF/tld/liferay-ui.tld"] />
<#assign liferay_util =                  PortletJspTagLibs["/WEB-INF/tld/liferay-util.tld"] />
<#assign portlet =                       PortletJspTagLibs["/WEB-INF/tld/liferay-portlet.tld"] />

<#assign OSGiHeader =                    staticUtil["aQute.libg.header.OSGiHeader"] />

<#assign LanguageUtil =                  staticUtil["com.liferay.portal.kernel.language.LanguageUtil"] />
<#assign UnicodeLanguageUtil =           staticUtil["com.liferay.portal.kernel.language.UnicodeLanguageUtil"] />
<#assign SessionErrors =                 staticUtil["com.liferay.portal.kernel.servlet.SessionErrors"] />
<#assign Constants =                     staticUtil["com.liferay.portal.kernel.util.Constants"] />
<#assign FastDateFormatFactoryUtil =     staticUtil["com.liferay.portal.kernel.util.FastDateFormatFactoryUtil"] />
<#assign ListUtil =                      staticUtil["com.liferay.portal.kernel.util.ListUtil"] />
<#assign ParamUtil =                     staticUtil["com.liferay.portal.kernel.util.ParamUtil"] />
<#assign Validator =                     staticUtil["com.liferay.portal.kernel.util.Validator"] />
<#assign ModuleFrameworkException =      staticUtil["com.liferay.portal.module.framework.ModuleFrameworkException"] />
<#assign ModuleFrameworkUtil =           staticUtil["com.liferay.portal.module.framework.ModuleFrameworkUtil"] />
<#assign PortalUtil =                    staticUtil["com.liferay.portal.util.PortalUtil"] />
<#assign PropsValues =                   staticUtil["com.liferay.portal.util.PropsValues"] />
<#assign WebKeys =                       staticUtil["com.liferay.portal.util.WebKeys"] />

<#assign Calendar =                      staticUtil["java.util.Calendar"] />

<#assign Bundle =                        staticUtil["org.osgi.framework.Bundle"] />
<#assign FrameworkConstants =            staticUtil["org.osgi.framework.Constants"] />
<#assign BundleStartLevel =              staticUtil["org.osgi.framework.startlevel.BundleStartLevel"] />

<@portlet["defineObjects"] />
<@liferay_theme["defineObjects"] />

<#assign currentURL = PortalUtil.getCurrentURL(request) />
<#assign pageContext = .vars["javax.servlet.jsp.jspPageContext"] />

<#assign dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone) />

<#assign framework = ModuleFrameworkUtil.getFramework() />

<#assign bundleContext = framework.getBundleContext() />
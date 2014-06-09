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

package com.liferay.portal.kernel.captcha;

import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class CaptchaUtil {

	public static void check(HttpServletRequest request)
		throws CaptchaException {

		getCaptcha().check(request);
	}

	public static void check(PortletRequest portletRequest)
		throws CaptchaException {

		getCaptcha().check(portletRequest);
	}

	public static Captcha getCaptcha() {	
		if(_captcha == null){
			_getRegistryCaptcha();
		}
		
		PortalRuntimePermission.checkGetBeanProperty(CaptchaUtil.class);

		return _captcha;
	}
	

	
	private static ServiceTracker<?, Captcha> _serviceTracker;
	
	/** loop through registry **/
	public static Captcha getCaptcha(String classname){		
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(objectClass=" 
					+ Captcha.class.getName() +
				")");

		_serviceTracker = registry.trackServices(filter);
		_serviceTracker.open();	
		
		/** figure out services **/
		Object[] services =  _serviceTracker.getServices();
		
		if(services != null){			
			for(Object service : services){
				if(classname.equals(service.getClass().getName())){
					_captcha = (Captcha) service;					
					return _captcha;
				}
			}			
		}
		/** cannot use reflection to instantiate new class due to dual classpath loader  **/
		return null;
	}
	
	/** dont care which, just grab the highest ranking one **/
	private static void _getRegistryCaptcha() {
		/** check with registry **/
		_captcha = (Captcha) RegistryUtil.getRegistry().getService(Captcha.class);
	}
	
	private static void _setRegistryCaptcha() {
		/** set into with registry **/
		RegistryUtil.getRegistry().registerService(Captcha.class.getName(), _captcha);
	}

	public static String getTaglibPath() {
		return getCaptcha().getTaglibPath();
	}

	public static boolean isEnabled(HttpServletRequest request)
		throws CaptchaException {

		return getCaptcha().isEnabled(request);
	}

	public static boolean isEnabled(PortletRequest portletRequest)
		throws CaptchaException {

		return getCaptcha().isEnabled(portletRequest);
	}

	public static void serveImage(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		getCaptcha().serveImage(request, response);
	}

	public static void serveImage(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException {

		getCaptcha().serveImage(resourceRequest, resourceResponse);
	}
	
	public static void injectCaptcha(Captcha captcha) {
		_setRegistryCaptcha();		
		_captcha = captcha;
	}
	
	
	/** member registry asign **/
	
	private Registry reg;
	
	private void add2Registry(Captcha captcha) {
		/** set into with registry **/
		if(reg == null){
			 reg = RegistryUtil.getRegistry();
		}		
		reg.registerService(Captcha.class.getName(), captcha);
	}
	
	
	/** spring injection section **/
	public void setCaptcha(Captcha captcha) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());
		
		_captcha = captcha;
		add2Registry(captcha);
	}
	
	private static Captcha _captcha;

}
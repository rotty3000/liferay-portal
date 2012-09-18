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

package com.liferay.web.extender.servlet.jsp;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.process.ClassPathUtil;
import com.liferay.portal.module.framework.ModuleFrameworkConstants;

import java.io.File;
import java.io.IOException;

import java.util.List;

import javax.servlet.ServletContext;

import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Jsr199JavaCompiler;

import org.osgi.framework.Bundle;

import org.phidias.compile.BundleJavaManager;

/**
 * @author Raymond Augé
 */
public class JspCompiler extends Jsr199JavaCompiler {

	@Override
	public void init(
		JspCompilationContext ctxt, ErrorDispatcher errDispatcher,
		boolean suppressLogging) {

		super.init(ctxt, errDispatcher, suppressLogging);

		ServletContext servletContext = ctxt.getServletContext();

		_bundle = (Bundle)servletContext.getAttribute(
			ModuleFrameworkConstants.OSGI_BUNDLE);

		_classpath = ClassPathUtil.getClassPathFiles(
			ClassPathUtil.getPortalClassPath());
	}

	@Override
	protected JavaFileManager getJavaFileManager(
		JavaFileManager javaFileManager) {

		if (javaFileManager instanceof StandardJavaFileManager) {
			StandardJavaFileManager standardJavaFileManager =
				(StandardJavaFileManager)javaFileManager;

			try {
				standardJavaFileManager.setLocation(
					StandardLocation.CLASS_PATH, _classpath);

				BundleJavaManager bundleJavaManager = new BundleJavaManager(
					_bundle, standardJavaFileManager, options);

				javaFileManager = bundleJavaManager;
			}
			catch (IOException e) {
				_log.error(e, e);
			}
		}

		return super.getJavaFileManager(javaFileManager);
	}

	public static Log _log = LogFactoryUtil.getLog(JspCompiler.class);

	private Bundle _bundle;
	private List<File> _classpath;

}
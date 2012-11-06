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

package com.liferay.web.extender.internal.compiler;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.osgi.framework.Bundle;

import org.phidias.compile.BundleJavaManager;
import org.phidias.compile.Constants;
import org.phidias.compile.StringJavaFileObject;

/**
 * @author Raymond Augé
 */
public class TestCompiler implements Constants {

	public TestCompiler() {
	}

	public void compile(
			String className, String source, List<String> options,
			Map<Location, Iterable<File>> locations, Bundle bundle)
		throws IOException {

		if (options != null) {
			_options.addAll(options);
		}

		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics =
			new DiagnosticCollector<JavaFileObject>();

		StandardJavaFileManager standardFileManager =
			javaCompiler.getStandardFileManager(diagnostics, null, null);

		JavaFileObject[] sourceFiles = {
			new StringJavaFileObject(className, source)};

		for (Map.Entry<Location, Iterable<File>> location :
				locations.entrySet()) {

			standardFileManager.setLocation(
				location.getKey(), location.getValue());
		}

		BundleJavaManager bundleJavaManager = new BundleJavaManager(
			bundle, standardFileManager, _options);

		JavaFileManager javaFileManager = bundleJavaManager;

		if (_options.contains("trace")) {
			_options.remove("trace");

			javaFileManager = (JavaFileManager)Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class<?>[] {StandardJavaFileManager.class},
				new TracingInvocationHandler(bundleJavaManager)
			);
		}

		CompilationTask compilationTask = javaCompiler.getTask(
			null, javaFileManager, diagnostics, _options, null,
			Arrays.asList(sourceFiles));

		javaFileManager.close();

		if (compilationTask.call()) {
			return;
		}

		for (Diagnostic dm : diagnostics.getDiagnostics()) {
			System.err.println(
				"COMPILE ERROR: " + className + Kind.SOURCE.extension + ":" +
					dm.getLineNumber());
			System.err.println(dm.getMessage(null));
		}
	}

	private ArrayList<String> _options = new ArrayList<String>();

	private class TracingInvocationHandler implements InvocationHandler {

		public TracingInvocationHandler(BundleJavaManager bundleJavaManager) {
			_bundleJavaManager = bundleJavaManager;

			_ignoreMethods = new ArrayList<String>();

			//_ignoreMethods.add("inferBinaryName");
		}

		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			if (_ignoreMethods.contains(method.getName())) {
				return method.invoke(_bundleJavaManager, args);
			}

			System.err.print("METHOD: " + method.getName() + "(");

			Class<?>[] parameterTypes = method.getParameterTypes();

			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameterType = parameterTypes[i];

				if (i > 0) {
					System.err.print(",");
				}

				System.err.print(parameterType.getSimpleName());
			}

			System.err.print(")");

			System.err.print(" INPUT: (");

			if ((args != null) && (args.length > 0)) {
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];

					if (i > 0) {
						System.err.print(",");
					}

					System.err.print(arg);
				}
			}

			System.err.println(")");

			Object returnValue = null;

			try {
				returnValue = method.invoke(_bundleJavaManager, args);
			}
			catch (Exception e) {
				e.printStackTrace();

				throw e;
			}

			System.err.println(" OUTPUT: " + returnValue);

			return returnValue;
		}

		private BundleJavaManager _bundleJavaManager;
		private List<String> _ignoreMethods;

	}

}
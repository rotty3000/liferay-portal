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

package com.liferay.ant.bnd.service;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 */
public class ServiceAnalyzerPluginTest {

	@Test
	public void testReadServiceXmlToProvideServiceCaps() throws Exception {
		InputStream inputStream =
			ServiceAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/service-test.xml");

		File serviceXml = tempFolder.newFile("service.xml");

		Files.copy(
			inputStream, serviceXml.toPath(),
			StandardCopyOption.REPLACE_EXISTING);

		ServiceAnalyzerPlugin serviceAnalyzerPlugin =
			new ServiceAnalyzerPlugin();

		Analyzer analyzer = new Analyzer();

		analyzer.setBase(tempFolder.getRoot());

		serviceAnalyzerPlugin.analyzeJar(analyzer);

		Parameters provideCapability = analyzer.getProvideCapability();

		Assert.assertNotNull(provideCapability);

		Assert.assertEquals(
			provideCapability.toString(), 104, provideCapability.size());
	}

	@Test
	public void testExistingProvideCapabilityParameters() throws Exception {
		//liferay.resource.bundle=bundle.symbolic.name="com.liferay.portal.impl";resource.bundle.base.name="content.Language"
		InputStream inputStream =
			ServiceAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/service-test.xml");

		File serviceXml = tempFolder.newFile("service.xml");

		Files.copy(
			inputStream, serviceXml.toPath(),
			StandardCopyOption.REPLACE_EXISTING);

		ServiceAnalyzerPlugin serviceAnalyzerPlugin =
			new ServiceAnalyzerPlugin();

		Analyzer analyzer = new Analyzer();

		analyzer.setBase(tempFolder.getRoot());

		analyzer.setProperty(Constants.PROVIDE_CAPABILITY, new Parameters("liferay.resource.bundle=bundle.symbolic.name=\"com.liferay.portal.impl\";resource.bundle.base.name=\"content.Language\"").toString());

		serviceAnalyzerPlugin.analyzeJar(analyzer);

		Parameters provideCapability = analyzer.getProvideCapability();

		Assert.assertNotNull(provideCapability);

		Assert.assertEquals(
			provideCapability.toString(), 105, provideCapability.size());
	}

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

}
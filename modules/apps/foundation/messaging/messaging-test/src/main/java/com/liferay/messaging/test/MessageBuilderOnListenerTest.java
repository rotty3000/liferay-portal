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

package com.liferay.messaging.test;

import static org.junit.Assert.assertEquals;

import com.liferay.messaging.MessageBuilder;

import org.junit.Test;

import org.osgi.framework.Bundle;

/**
 * @author Raymond Augé
 */
public class MessageBuilderOnListenerTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		test("tb10.jar", "builder/tb10");
	}

	@Test
	public void testSerial() throws Exception {
		test("tb11.jar", "builder/tb11");
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb12.jar", "builder/tb12");
	}

	public void test(String bundle, String destinationName) throws Exception {
		Bundle tbBundle = install(bundle);

		try {
			tbBundle.start();

			MessageBuilder builder = messageBuilderFactory.create(destinationName);

			Object response = builder.sendSynchronous();

			assertEquals(builder.build(), response);
		}
		finally {
			tbBundle.uninstall();
		}
	}

}
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

package com.liferay.petra.messaging.test;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.spi.MessageImpl;
import com.liferay.petra.messaging.test.tb7.TBParallelDestination;
import com.liferay.petra.messaging.test.tb8.TBSerialDestination;
import com.liferay.petra.messaging.test.tb9.TBSynchronousDestination;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.Bundle;

/**
 * @author Raymond Augé
 */
public class SynchronousMessageSenderTest extends TestUtil {

	@Test
	public void testParallel() throws Exception {
		test("tb7.jar", TBParallelDestination.DESTINATION_NAME);
	}

	@Test
	public void testSerial() throws Exception {
		test("tb8.jar", TBSerialDestination.DESTINATION_NAME);
	}

	@Test
	public void testSynchronous() throws Exception {
		test("tb9.jar", TBSynchronousDestination.DESTINATION_NAME);
	}

	protected void test(String bundle, String destinationName)
		throws Exception {

		Bundle tb = install(bundle);

		try {
			tb.start();

			Message message = new MessageImpl();

			Object result = messageBus.sendSynchronousMessage(
				destinationName, message);

			Assert.assertEquals(message, result);
		}
		finally {
			tb.uninstall();
		}
	}

}
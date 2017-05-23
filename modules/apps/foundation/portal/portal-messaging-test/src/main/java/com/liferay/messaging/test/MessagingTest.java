package com.liferay.messaging.test;

import com.liferay.messaging.Message;

import org.junit.Assert;
import org.junit.Test;

public class MessagingTest {

	@Test
	public void testOne() {
		Message message = new Message();

		Assert.assertNotNull(message);
	}

}
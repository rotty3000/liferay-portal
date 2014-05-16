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

package com.liferay.portal.kernel.settings;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.powermock.api.mockito.PowerMockito;

/**
 * @author Iván Zaera
 */
public class BaseSettingsTest extends PowerMockito {

	@Test
	public void testReset() {
		Settings settings = new MemorySettings();

		settings.setValue("key1", "value2");
		settings.setValue("key2", "value2");

		settings.reset();

		Collection<String> keys = settings.getKeys();

		Assert.assertEquals(0, keys.size());
	}

	@Test
	public void testSetValues() {
		Settings sourceSettings = new MemorySettings();

		sourceSettings.setValue("key1", "value1");
		sourceSettings.setValue("key2", "value2");

		Settings targetSettings = new MemorySettings();

		targetSettings.setValue("otherKey", "otherValue");

		sourceSettings.setValues(targetSettings);

		Collection<String> keys = sourceSettings.getKeys();

		Assert.assertEquals(3, keys.size());
		Assert.assertEquals(
			"otherValue", sourceSettings.getValue("otherKey", null));
		Assert.assertEquals("value1", sourceSettings.getValue("key1", null));
		Assert.assertEquals("value2", sourceSettings.getValue("key2", null));
	}

}
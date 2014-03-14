/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.osgi.util;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Constants;

/**
 * @author Raymond Augé
 */
public class TU {

	public static HandyMap serviceMap() {
		return serviceMap(null, null);
	}

	public static HandyMap serviceMap(String key, Object value) {
		HandyMap map = new HandyMap();

		map.put(Constants.SERVICE_ID,  integer.incrementAndGet());
		map.put(Constants.SERVICE_PID,  UUID.randomUUID().toString());
		map.put(Constants.SERVICE_RANKING,  0);
		map.put(Constants.SERVICE_VENDOR, "Liferay Inc.");

		if ((key != null) && (value != null)) {
			map.put(key,  value);
		}

		return map;
	}

	public static class HandyMap extends HashMap<String, Object> {

		public HandyMap add(String key, Object value) {
			put(key, value);

			return this;
		}

	}

	public static final AtomicInteger integer = new AtomicInteger();

}
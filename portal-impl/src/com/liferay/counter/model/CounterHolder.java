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

package com.liferay.counter.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Shuyang Zhou
 */
public class CounterHolder {

	public CounterHolder(long initValue, long rangeMax) {
		
		_counter = new AtomicLong(initValue);
		_rangeMax = rangeMax;
	}

	public long addAndGet(long delta) {
		return _counter.addAndGet(delta);
	}

	public long getCurrentValue() {
		return _counter.get();
	}

	public long getRangeMax() {
		return _rangeMax;
	}

	private final AtomicLong _counter;
	private final long _rangeMax;

}
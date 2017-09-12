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

package com.liferay.petra.io.nio.intraband.proxy;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.nio.intraband.Datagram;
import com.liferay.petra.io.nio.intraband.RegistrationReference;

import java.util.concurrent.ExecutorService;

/**
 * @author Shuyang Zhou
 */
public class AsyncIntrabandProxySkeleton implements IntrabandProxySkeleton {

	public static IntrabandProxySkeleton createAsyncIntrabandProxySkeleton(
		ExecutorService executorService,
		IntrabandProxySkeleton intrabandProxySkeleton) {

		if (executorService == null) {
			return intrabandProxySkeleton;
		}

		return new AsyncIntrabandProxySkeleton(
			executorService, intrabandProxySkeleton);
	}

	@Override
	public void dispatch(
		final RegistrationReference registrationReference,
		final Datagram datagram, final Deserializer deserializer) {

		_executorService.execute(
			new Runnable() {

				@Override
				public void run() {
					_intrabandProxySkeleton.dispatch(
						registrationReference, datagram, deserializer);
				}

			});
	}

	private AsyncIntrabandProxySkeleton(
		ExecutorService executorService,
		IntrabandProxySkeleton intrabandProxySkeleton) {

		_executorService = executorService;
		_intrabandProxySkeleton = intrabandProxySkeleton;
	}

	private final ExecutorService _executorService;
	private final IntrabandProxySkeleton _intrabandProxySkeleton;

}
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.permission;

import java.util.concurrent.Callable;

/**
 * @author László Csontos
 */
public abstract class DoAsUserCallable<P, R>
	extends AbstractDoAsUserTask<P, R> implements Callable<R> {

	public DoAsUserCallable(long userId, P parameter) {
		super(userId, parameter);
	}

	public DoAsUserCallable(P parameter) {
		super(parameter);
	}

	@Override
	public R call() throws Exception {
		P parameter = getParameter();

		return perform(parameter);
	}

}
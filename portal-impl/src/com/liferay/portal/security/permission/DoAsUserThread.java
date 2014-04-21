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

package com.liferay.portal.security.permission;

import com.liferay.portal.model.UserConstants;

/**
 * @author Brian Wing Shun Chan
 * @author László Csontos
 */
public abstract class DoAsUserThread extends Thread {

	public DoAsUserThread() {
		this(UserConstants.USER_ID_DEFAULT);
	}

	public DoAsUserThread(long userId) {
		_doAsUserTask = new AbstractDoAsUserTask<Void, Void>(userId, null) {

			@Override
			protected Void doPerform(Void parameter) throws Exception {
				doRun();

				return null;
			}

		};
	}

	public boolean isSuccess() {
		return _doAsUserTask.isSuccess();
	}

	@Override
	public void run() {
		_doAsUserTask.perform(null);
	}

	protected abstract void doRun() throws Exception;

	private AbstractDoAsUserTask<?, ?> _doAsUserTask;

}
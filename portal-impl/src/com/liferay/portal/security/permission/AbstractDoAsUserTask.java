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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserConstants;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * @author László Csontos
 */
public abstract class AbstractDoAsUserTask<P, R> implements DoAsUserTask<P, R> {

	public AbstractDoAsUserTask(long userId, P parameter) {
		if (userId == UserConstants.USER_ID_DEFAULT) {
			_userId = PrincipalThreadLocal.getUserId();
		}
		else {
			_userId = userId;
		}

		_parameter = parameter;
	}

	public AbstractDoAsUserTask(P parameter) {
		this(UserConstants.USER_ID_DEFAULT, parameter);
	}

	public P getParameter() {
		return _parameter;
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public boolean hasRun() {
		return _hasRun;
	}

	@Override
	public boolean isSuccess() {
		return _success;
	}

	@Override
	public final R perform(P parameter) {
		R result = null;

		try {
			PrincipalThreadLocal.setName(_userId);

			User user = UserLocalServiceUtil.getUserById(_userId);

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			result = doPerform(parameter);

			_success = true;
		}
		catch (Exception e) {
			_log.error(e, e);
		}
		finally {
			PrincipalThreadLocal.setName(null);
			PermissionThreadLocal.setPermissionChecker(null);

			_hasRun = true;
		}

		return result;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(10);

		sb.append(StringPool.OPEN_CURLY_BRACE);

		sb.append("hasRun=");
		sb.append(hasRun());
		sb.append(", parameter=");
		sb.append(getParameter());
		sb.append(", success=");
		sb.append(isSuccess());
		sb.append(", userId=");
		sb.append(getUserId());

		sb.append(StringPool.CLOSE_CURLY_BRACE);

		return sb.toString();
	}

	protected abstract R doPerform(P parameter) throws Exception;

	private static Log _log = LogFactoryUtil.getLog(AbstractDoAsUserTask.class);

	private boolean _hasRun;
	private P _parameter;
	private boolean _success;
	private long _userId;

}
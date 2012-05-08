/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.pwd;

import com.liferay.portal.UserPasswordException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.InstancePool;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.security.ldap.LDAPSettingsUtil;
import com.liferay.portal.util.PropsUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class PwdToolkitUtil {

	public static String generate(PasswordPolicy passwordPolicy) {
		return _instance._generate(passwordPolicy);
	}

	public static void validate(
			long companyId, long userId, String password1, String password2,
			PasswordPolicy passwordPolicy)
		throws PortalException, SystemException {

		if (!password1.equals(password2)) {
			throw new UserPasswordException(
				UserPasswordException.PASSWORDS_DO_NOT_MATCH);
		}

		if (!LDAPSettingsUtil.isPasswordPolicyEnabled(companyId)) {
			_instance._validate(userId, password1, password2, passwordPolicy);
		}
	}

	private PwdToolkitUtil() {
		_toolkit = (BasicToolkit)InstancePool.get(
			PropsUtil.get(PropsKeys.PASSWORDS_TOOLKIT));
	}

	private String _generate(PasswordPolicy passwordPolicy) {
		return _toolkit.generate(passwordPolicy);
	}

	private void _validate(
			long userId, String password1, String password2,
			PasswordPolicy passwordPolicy)
		throws PortalException, SystemException {

		if (!PwdToolkitUtilThreadLocal.isValidate()) {
			return;
		}

		_toolkit.validate(userId, password1, password2, passwordPolicy);
	}

	private static PwdToolkitUtil _instance = new PwdToolkitUtil();

	private BasicToolkit _toolkit;

}
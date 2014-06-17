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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.util.PropsValues;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Peter Fellwock
 */
public class EmailAddressValidatorFactory {

	public static EmailAddressValidator getInstance() {
		if (_emailAddressValidator == null) {
			_originalEmailAddressValidator =
					EmailAddressValidatorRegistryUtil
					.getEmailAddressValidator(
						PropsValues.USERS_EMAIL_ADDRESS_VALIDATOR);
			_emailAddressValidator = _originalEmailAddressValidator;
		}

		return _emailAddressValidator;
	}

	public static void setInstance(
		EmailAddressValidator emailAddressValidator) {

		if (_log.isDebugEnabled()) {
			_log.debug("Set " + ClassUtil.getClassName(emailAddressValidator));
		}

		if (emailAddressValidator == null) {
			_emailAddressValidator = _originalEmailAddressValidator;
		}
		else {
			_emailAddressValidator = emailAddressValidator;
		}
	}

	public void afterPropertiesSet() throws Exception {

		String classname = PropsValues.USERS_EMAIL_ADDRESS_VALIDATOR;

		if (_log.isDebugEnabled()) {
			_log.debug("Instantiate " + classname);
		}

		_originalEmailAddressValidator =
				EmailAddressValidatorRegistryUtil
				.getEmailAddressValidator(classname);

		_emailAddressValidator = _originalEmailAddressValidator;
	}

	private static Log _log = LogFactoryUtil.getLog(
		EmailAddressValidatorFactory.class);

	private static volatile EmailAddressValidator _emailAddressValidator;
	private static EmailAddressValidator _originalEmailAddressValidator;

}
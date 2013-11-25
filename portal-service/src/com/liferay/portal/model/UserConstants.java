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

package com.liferay.portal.model;

import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.webserver.WebServerServletTokenUtil;

/**
 * @author Amos Fong
 */
public class UserConstants {

	public static final int FULL_NAME_MAX_LENGTH = 75;

	public static final String LIST_VIEW_FLAT_ORGANIZATIONS =
		"flat-organizations";

	public static final String LIST_VIEW_FLAT_USER_GROUPS = "flat-user-groups";

	public static final String LIST_VIEW_FLAT_USERS = "flat-users";

	public static final String LIST_VIEW_TREE = "tree";

	public static final String USERS_EMAIL_ADDRESS_AUTO_SUFFIX = PropsUtil.get(
		PropsKeys.USERS_EMAIL_ADDRESS_AUTO_SUFFIX);

	public static String getDefaultPortraitURL(
		String imagePath, boolean male, long userId) {

		StringBundler sb = new StringBundler(7);

		sb.append(imagePath);

		if (_USERS_IMAGE_PORTRAIT_POLICY_ALL) {
			sb.append("/user");

			if (male) {
				sb.append("_male");
			}
			else {
				sb.append("_female");
			}

			sb.append("_portrait?");
		}
		else {
			sb.append("/user_portrait?");

			if (userId > 0) {
				sb.append("p_u_i_d=");
				sb.append(userId);
				sb.append(StringPool.AMPERSAND);
			}
		}

		sb.append("t=");
		sb.append(WebServerServletTokenUtil.getToken(userId));

		return sb.toString();
	}

	public static String getPortraitURL(
		String imagePath, boolean male, long portraitId) {

		StringBundler sb = new StringBundler(7);

		sb.append(imagePath);
		sb.append("/user");

		if (_USERS_IMAGE_PORTRAIT_POLICY_ALL) {
			if (male) {
				sb.append("_male");
			}
			else {
				sb.append("_female");
			}
		}

		sb.append("_portrait?img_id=");
		sb.append(portraitId);
		sb.append("&t=");
		sb.append(WebServerServletTokenUtil.getToken(portraitId));

		return sb.toString();
	}

	private static final boolean _USERS_IMAGE_PORTRAIT_POLICY_ALL =
		StringUtil.equalsIgnoreCase(
			PropsUtil.get(PropsKeys.USERS_IMAGE_PORTRAIT_POLICY), "all");

}
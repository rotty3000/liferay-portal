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

package com.liferay.portal.sso.ntlm.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Michael C. Han
 */
@Meta.OCD(
	id = "com.liferay.portal.sso.ntlm", localization = "content.Language"
)
public interface NTLMConfiguration {

	@Meta.AD(
		deflt = "EXAMPLE", id = "domain", required = false)
	public String domain();

	@Meta.AD(
		deflt = "127.0.0.1", id = "domain.controller", required = false)
	public String domainController();

	@Meta.AD(
		deflt = "EXAMPLE", id = "domain.controller.name", required = false)
	public String domainControllerName();

	@Meta.AD(
		deflt = "false", id = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "0x600FFFFF", id = "negotiateFlags", required = false)
	public String negotiateFlags();

	@Meta.AD(
		deflt = "LIFERAY$@EXAMPLE.COM", id = "service.account",
		required = false)
	public String serviceAccount();

	@Meta.AD(
		deflt = "test", id = "servicePassword", required = false)
	public String servicePassword();

}
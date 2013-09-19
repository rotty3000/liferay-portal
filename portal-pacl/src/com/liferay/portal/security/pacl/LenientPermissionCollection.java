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

package com.liferay.portal.security.pacl;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;

import java.util.Collections;
import java.util.Enumeration;

/**
 * @author Raymond Augé
 */
public class LenientPermissionCollection extends PermissionCollection {

	public LenientPermissionCollection() {
	}

	public LenientPermissionCollection(CodeSource codeSource) {
		_codeSource = codeSource;
	}

	public LenientPermissionCollection(ProtectionDomain protectionDomain) {
		_protectionDomain = protectionDomain;
	}

	@Override
	public void add(Permission permission) {
		_permissionCollection.add(permission);
	}

	@Override
	public Enumeration<Permission> elements() {
		return Collections.enumeration(Collections.<Permission>emptyList());
	}

	@Override
	public boolean implies(Permission permission) {
		if (_permissionCollection.implies(permission)) {
			return true;
		}

		if (_codeSource != null) {
//			System.out.println("PASSING: " + permission + "\t\t for " + _codeSource);
		}

		if (_protectionDomain != null) {
//			System.out.println("PASSING: " + permission + "\t\t for " + _protectionDomain);
		}

		return true;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	private CodeSource _codeSource;
	private PermissionCollection _permissionCollection = new Permissions();
	private ProtectionDomain _protectionDomain;

}
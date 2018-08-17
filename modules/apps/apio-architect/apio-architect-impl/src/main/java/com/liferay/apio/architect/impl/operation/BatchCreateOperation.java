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

package com.liferay.apio.architect.impl.operation;

import static com.liferay.apio.architect.operation.HTTPMethod.POST;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.operation.HTTPMethod;
import com.liferay.apio.architect.operation.Operation;

import java.util.Optional;

/**
 * Represents a batch resource create operation.
 *
 * @author Alejandro Hernández
 */
public class BatchCreateOperation implements Operation {

	public BatchCreateOperation(Form form, String resourceName) {
		this(form, resourceName, null);
	}

	public BatchCreateOperation(Form form, String resourceName, String uri) {
		_form = form;
		_resourceName = resourceName;
		_uri = uri;
	}

	@Override
	public Optional<Form> getFormOptional() {
		return Optional.ofNullable(_form);
	}

	@Override
	public HTTPMethod getHttpMethod() {
		return POST;
	}

	@Override
	public String getName() {
		return _resourceName + "/batch-create";
	}

	@Override
	public Optional<String> getURIOptional() {
		return Optional.ofNullable(_uri);
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	private final Form _form;
	private final String _resourceName;
	private final String _uri;

}
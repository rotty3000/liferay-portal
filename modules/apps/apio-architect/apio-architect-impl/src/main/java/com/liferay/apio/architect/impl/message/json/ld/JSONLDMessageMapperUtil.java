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

package com.liferay.apio.architect.impl.message.json.ld;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.liferay.apio.architect.impl.operation.BatchCreateOperation;
import com.liferay.apio.architect.impl.operation.CreateOperation;
import com.liferay.apio.architect.impl.operation.DeleteOperation;
import com.liferay.apio.architect.impl.operation.UpdateOperation;
import com.liferay.apio.architect.operation.Operation;

import java.util.List;

/**
 * Provides utility functions for JSON-LD message mappers.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class JSONLDMessageMapperUtil {

	/**
	 * Return the list of {@link Operation} types.
	 *
	 * @param  operation the operation
	 * @return the list
	 */
	public static List<String> getOperationTypes(Operation operation) {
		if (operation instanceof BatchCreateOperation) {
			return asList("BatchCreateAction", "Operation");
		}

		if (operation instanceof CreateOperation) {
			return asList("CreateAction", "Operation");
		}

		if (operation instanceof DeleteOperation) {
			return asList("DeleteAction", "Operation");
		}

		if (operation instanceof UpdateOperation) {
			return asList("ReplaceAction", "Operation");
		}

		return singletonList("Operation");
	}

	private JSONLDMessageMapperUtil() {
		throw new UnsupportedOperationException();
	}

}
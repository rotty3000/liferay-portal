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

package com.liferay.portal.osgi.web.wab.generator.internal.cdi;

import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Raymond Augé
 */
public class PackageDef extends ClassDataCollector {

	@Override
	public void annotation(Annotation annotation) throws Exception {
		Descriptors.TypeRef typeRef = annotation.getName();

		String fqn = typeRef.getFQN();

		if (fqn.equals("org.osgi.service.cdi.annotations.Beans")) {
			Object[] beanClasses = annotation.get("value");

			if ((beanClasses != null) && (beanClasses.length > 0)) {
				Stream<Object> stream = Arrays.stream(beanClasses);

				marked = new Instruction(
					stream.map(
						Object::toString
					).collect(
						Collectors.joining(",")
					)
				);
			}
			else {
				marked = new Instruction("*");
			}
		}
	}

	public Instruction marked;

}
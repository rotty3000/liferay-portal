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

package com.liferay.vulcan.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Alejandro Hernández
 */
@FunctionalInterface
public interface DecaFunction<A, B, C, D, E, F, G, H, I, J, R> {

	public default <V> DecaFunction<A, B, C, D, E, F, G, H, I, J, V> andThen(
		Function<? super R, ? extends V> afterFunction) {

		Objects.requireNonNull(afterFunction);

		return (A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) ->
			afterFunction.apply(apply(a, b, c, d, e, f, g, h, i, j));
	}

	public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

}
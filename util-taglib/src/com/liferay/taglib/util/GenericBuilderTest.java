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

package com.liferay.taglib.util;

/**
 * @author Carlos Sierra Andrés
 */
public class GenericBuilderTest<T, U, V> {


	public static class TB<T> {
		public <U> TU<T, U> setU(U u) {
			return new TU();
		}
	}

	public static class TU<T,U> {
		public <V> GenericBuilderTest<T, U, V> setV(V v) {
			return new GenericBuilderTest<>();
		}
	}
}

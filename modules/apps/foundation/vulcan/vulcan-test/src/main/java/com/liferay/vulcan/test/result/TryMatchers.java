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

package com.liferay.vulcan.test.result;

import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.test.internal.result.FailTry;
import com.liferay.vulcan.test.internal.result.SuccessTry;
import com.liferay.vulcan.test.internal.result.ValueTry;

import org.hamcrest.Matcher;

/**
 * This class provides {@code Hamcrest} {@link Matcher}s that can be used for
 * testing the {@link Try} monadic type.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public final class TryMatchers {

	/**
	 * Returns a matcher that checks that this try is a {@link Try.Failure}.
	 *
	 * @return a matcher that checks that this try is a failure
	 * @review
	 */
	public static <T> Matcher<Try<T>> aFailTry() {
		return new FailTry<>();
	}

	/**
	 * Returns a matcher that checks that this try is a {@link Try.Success}.
	 *
	 * @return a matcher that checks that this try is a success
	 * @review
	 */
	public static <T> Matcher<Try<T>> aSuccessTry() {
		return new SuccessTry<>();
	}

	/**
	 * Returns a matcher that checks that this try is a {@link Try.Success} with
	 * a certain value.
	 *
	 * @param  matcher the matcher used for checking the value
	 * @return a matcher that checks that this try is a success with a certain
	 *         value
	 * @review
	 */
	public static <T> Matcher<Try<T>> aTryWithValueThat(
		final Matcher<T> matcher) {

		return new ValueTry<>(matcher);
	}

	private TryMatchers() {
		throw new UnsupportedOperationException();
	}

}
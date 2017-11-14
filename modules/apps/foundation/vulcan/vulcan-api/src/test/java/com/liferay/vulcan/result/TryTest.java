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

package com.liferay.vulcan.result;

import static com.liferay.vulcan.test.result.TryMatchers.aFailTry;
import static com.liferay.vulcan.test.result.TryMatchers.aSuccessTry;
import static com.liferay.vulcan.test.result.TryMatchers.aTryWithValueThat;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.liferay.vulcan.exception.FalsePredicateException;
import com.liferay.vulcan.result.Try.Failure;
import com.liferay.vulcan.result.Try.Success;

import java.io.Closeable;
import java.io.IOException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

/**
 * @author Alejandro Hernández
 */
@RunWith(JUnitParamsRunner.class)
public class TryTest {

	public static final String FAIL = "fail";

	public static final String SUCCESS = "success";

	/**
	 * Returns a new list of failures to be used in tests using {@code
	 *
	 * @return a list of failures to be used in tests.
	 * @review
	 */
	public Try[] fail() {
		return new Try[] {Try.fail(new IllegalArgumentException())};
	}

	/**
	 * Returns a new list of successes to be used in tests using {@code
	 *
	 * @return a list of successes to be used in tests.
	 * @review
	 */
	public Try[] success() {
		return new Try[] {Try.success("Live long")};
	}

	@Test
	public void testInvokingFallibleWithResourcesClosesStream()
		throws Exception {

		Closeable closeable = Mockito.mock(Closeable.class);

		Try.fromFallibleWithResources(() -> closeable, __ -> "Live long");

		Mockito.verify(
			closeable
		).close();
	}

	@Test
	public void testInvokingFallibleWithResourcesWithExceptionCreatesFailure()
		throws Exception {

		Try<String> stringTry = Try.fromFallibleWithResources(
			() -> () -> {
			},
			__ -> {
				throw new IllegalArgumentException();
			});

		assertThat(stringTry, is(aFailTry()));
	}

	@Test
	public void testInvokingFallibleWithResourcesWithValueCreatesSuccess()
		throws Exception {

		Try<String> stringTry = Try.fromFallibleWithResources(
			() -> () -> {
			},
			__ -> "Live long");

		assertThat(stringTry, is(aSuccessTry()));
	}

	@Parameters(method = FAIL)
	@Test(expected = IllegalArgumentException.class)
	public void testInvokingFilterOnFailureThrowsException(
			Try<String> stringTry)
		throws Exception {

		stringTry.filter(
			__ -> true
		).get();
	}

	@Parameters(method = SUCCESS)
	@Test(expected = FalsePredicateException.class)
	public void testInvokingFilterOnSuccessWithFalsePredicateReturnsException(
			Try<String> stringTry)
		throws Exception {

		stringTry.filter(
			string -> string.startsWith("long")
		).get();
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingFilterOnSuccessWithValidPredicateReturnsValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.filter(string -> string.startsWith("Live")),
			is(aTryWithValueThat(equalTo("Live long"))));
	}

	@Parameters(method = FAIL)
	@Test
	public void testInvokingFoldOnFailureExecutesFailureFunction(
		Try<String> stringTry) {

		Integer integer = stringTry.fold(exception -> 3, string -> 5);

		assertThat(integer, is(equalTo(3)));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingFoldOnSuccessExecutesSuccessFunction(
		Try<String> stringTry) {

		Integer integer = stringTry.fold(exception -> 3, string -> 5);

		assertThat(integer, is(equalTo(5)));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingFoldOnSuccessWithFailureExecutesBothFunctions(
		Try<String> stringTry) {

		Integer integer = stringTry.fold(
			exception -> {
				assertThat(
					exception, is(instanceOf(IllegalArgumentException.class)));

				return 3;
			},
			string -> {
				throw new IllegalArgumentException();
			});

		assertThat(integer, is(equalTo(3)));
	}

	@Test
	public void testInvokingFromFallibleWithExceptionCreatesFailure() {
		Try<String> stringTry = Try.fromFallible(
			() -> {
				throw new IllegalArgumentException();
			});

		assertThat(stringTry, is(aFailTry()));
	}

	@Test
	public void testInvokingFromFallibleWithValueCreatesSuccess() {
		Try<String> stringTry = Try.fromFallible(() -> "Live long");

		assertThat(stringTry, is(aSuccessTry()));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingGetOnSuccessShouldReturnValue(Try<String> stringTry)
		throws Exception {

		assertThat(stringTry.get(), is(equalTo("Live long")));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingGetValueOnSuccessShouldReturnValue(
		Try<String> stringTry) {

		Success success = (Success)stringTry;

		assertThat(success.getValue(), is(equalTo("Live long")));
	}

	@Parameters(method = FAIL)
	@Test
	public void testInvokingIsFailureOnFailureReturnsTrue(
		Try<String> stringTry) {

		assertThat(stringTry.isFailure(), is(equalTo(true)));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingIsFailureOnSuccessReturnsFalse(
		Try<String> stringTry) {

		assertThat(stringTry.isFailure(), is(equalTo(false)));
	}

	@Parameters(method = FAIL)
	@Test
	public void testInvokingIsSuccessOnFailureReturnsFalse(
		Try<String> stringTry) {

		assertThat(stringTry.isSuccess(), is(equalTo(false)));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingIsSuccessOnSuccessReturnsFalse(
		Try<String> stringTry) {

		assertThat(stringTry.isSuccess(), is(equalTo(true)));
	}

	@Parameters(method = FAIL)
	@Test(expected = IllegalArgumentException.class)
	public void testInvokingMapFailMatchingOnFailureShouldThrowFirstException(
			Try<String> stringTry)
		throws Exception {

		stringTry.mapFailMatching(
			RuntimeException.class, IOException::new
		).get();
	}

	@Parameters(method = FAIL)
	@Test(expected = IOException.class)
	public void testInvokingMapFailMatchingOnFailureShouldThrowMatchedException(
			Try<String> stringTry)
		throws Exception {

		stringTry.mapFailMatching(
			IllegalArgumentException.class, IOException::new
		).get();
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingMapFailMatchingOnSuccessShouldReturnValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.mapFailMatching(RuntimeException.class, IOException::new),
			is(aTryWithValueThat(equalTo("Live long"))));
	}

	@Parameters(method = FAIL)
	@Test(expected = IOException.class)
	public void testInvokingMapFailOnFailureShouldThrowTransformedException(
			Try<String> stringTry)
		throws Exception {

		stringTry.mapFail(
			__ -> new IOException()
		).get();
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingMapFailOnSuccessShouldReturnValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.mapFail(__ -> new IOException()),
			is(aTryWithValueThat(equalTo("Live long"))));
	}

	@Parameters(method = FAIL)
	@Test
	public void testInvokingOrElseGetOnFailureShouldReturnValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.orElseGet(() -> "and prosper"),
			is(equalTo("and prosper")));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingOrElseGetOnSuccessShouldReturnPreviousValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.orElseGet(() -> "and prosper"), is(equalTo("Live long")));
	}

	@Parameters(method = FAIL)
	@Test
	public void testInvokingOrElseOnFailureShouldReturnValue(
		Try<String> stringTry) {

		assertThat(stringTry.orElse("and prosper"), is(equalTo("and prosper")));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingOrElseOnSuccessShouldReturnPreviousValue(
		Try<String> stringTry) {

		assertThat(stringTry.orElse("and prosper"), is(equalTo("Live long")));
	}

	@Parameters(method = FAIL)
	@Test(expected = UnsupportedOperationException.class)
	public void testInvokingOrElseThrowOnFailureShouldThrowNewException(
		Try<String> stringTry) {

		stringTry.orElseThrow(UnsupportedOperationException::new);
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testInvokingOrElseThrowOnSuccessShouldReturnValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.orElseThrow(UnsupportedOperationException::new),
			is(equalTo("Live long")));
	}

	@Parameters(method = FAIL)
	@Test
	public void testOnFailureShouldBeFailure(Try<String> stringTry) {
		assertThat(stringTry, is(aFailTry()));
	}

	@Test(expected = RuntimeException.class)
	public void testOnFailureWithUncheckedExceptionShouldThrowRuntimeException() {
		Try<String> stringTry = Try.fail(new Exception());

		stringTry.getUnchecked();
	}

	@Parameters(method = FAIL)
	@Test(expected = IllegalArgumentException.class)
	public void testOnGetShouldThrowTypedException(Try<String> stringTry)
		throws Exception {

		stringTry.get();
	}

	@Parameters(method = FAIL)
	@Test(expected = RuntimeException.class)
	public void testOnGetUncheckedShouldThrowRuntimeException(
		Try<String> stringTry) {

		stringTry.getUnchecked();
	}

	@Parameters(method = FAIL)
	@Test
	public void testOnInvokingFlatMapOnFailureShouldBeFailure(
		Try<String> stringTry) {

		Try<String> newTry = stringTry.flatMap(
			string -> Try.success(string + " and prosper"));

		assertThat(newTry, is(aFailTry()));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnInvokingFlatMapOnSuccessShouldReturnTransformedValue(
		Try<String> stringTry) {

		Try<String> newTry = stringTry.flatMap(
			string -> Try.success(string + " and prosper"));

		assertThat(
			newTry, is(aTryWithValueThat(equalTo("Live long and prosper"))));
	}

	@Parameters(method = FAIL)
	@Test
	public void testOnInvokingMapOnFailureShouldBeFailure(
		Try<String> stringTry) {

		Try<String> newTry = stringTry.map(string -> string + " and prosper");

		assertThat(newTry, is(aFailTry()));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnInvokingMapOnSuccessShouldReturnTransformedValue(
		Try<String> stringTry) {

		Try<String> newTry = stringTry.map(string -> string + " and prosper");

		assertThat(
			newTry, is(aTryWithValueThat(equalTo("Live long and prosper"))));
	}

	@Parameters(method = SUCCESS)
	@Test(expected = IOException.class)
	public void testOnInvokingThrowableFlatMapOnSuccessShouldReturnNewException(
			Try<String> stringTry)
		throws Exception {

		stringTry.flatMap(
			string -> {
				throw new IOException();
			}
		).get();
	}

	@Parameters(method = SUCCESS)
	@Test(expected = IOException.class)
	public void testOnInvokingThrowableMapOnSuccessShouldReturnNewException(
			Try<String> stringTry)
		throws Exception {

		stringTry.map(
			string -> {
				throw new IOException();
			}
		).get();
	}

	@Parameters(method = FAIL)
	@Test(expected = IOException.class)
	public void testOnRecoveringWithExceptionOnFailureShouldReturnNewException(
			Try<String> stringTry)
		throws Exception {

		Try<String> newTry = stringTry.recoverWith(
			__ -> {
				throw new IOException();
			});

		Failure failure = (Failure)newTry;

		throw failure.getException();
	}

	@Parameters(method = FAIL)
	@Test(expected = IOException.class)
	public void testOnRecoveringWithExceptionOnFailureShouldThrowNewException(
			Try<String> stringTry)
		throws Exception {

		stringTry.recoverWith(
			__ -> Try.fail(new IOException())
		).get();
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnRecoveringWithFailureOnSuccessShouldReturnFirstValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.recoverWith(__ -> Try.fail(new Exception())),
			is(aTryWithValueThat(equalTo("Live long"))));
	}

	@Parameters(method = FAIL)
	@Test
	public void testOnRecoveringWithShouldHavePreviousExceptionAsParameter(
			Try<String> stringTry)
		throws Exception {

		Failure failure = (Failure)stringTry;

		stringTry.recoverWith(
			exception -> {
				assertThat(failure.getException(), is(equalTo(exception)));

				return Try.success("and prosper");
			});
	}

	@Parameters(method = FAIL)
	@Test
	public void testOnRecoveringWithSuccessOnFailureShouldReturnNewValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.recoverWith(__ -> Try.fromFallible(() -> "and prosper")),
			is(aTryWithValueThat(equalTo("and prosper"))));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnRecoveringWithSuccessOnSuccessShouldReturnFirstValue(
		Try<String> stringTry) {

		assertThat(
			stringTry.recoverWith(__ -> Try.fromFallible(() -> "and prosper")),
			is(aTryWithValueThat(equalTo("Live long"))));
	}

	@Parameters(method = FAIL)
	@Test
	public void testOnRecoverWithValueOnFailureShouldReturnValue(
		Try<String> stringTry) {

		String string = stringTry.recover(__ -> "and prosper");

		assertThat(string, is(equalTo("and prosper")));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnRecoverWithValueOnSuccessShouldReturnPreviousValue(
		Try<String> stringTry) {

		String string = stringTry.recover(__ -> "and prosper");

		assertThat(string, is(equalTo("Live long")));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnSuccessShouldBeSuccess(Try<String> stringTry) {
		assertThat(stringTry, is(aSuccessTry()));
	}

	@Parameters(method = SUCCESS)
	@Test
	public void testOnSuccessShouldGetValue(Try<String> stringTry) {
		assertThat(stringTry, is(aTryWithValueThat(equalTo("Live long"))));
	}

}
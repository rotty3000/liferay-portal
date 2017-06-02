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

package com.liferay.petra.io.unsync;

import java.io.Writer;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-6648.
 * </p>
 *
 * @author Shuyang Zhou
 */
public class UnsyncStringWriter extends Writer {

	public UnsyncStringWriter() {
		stringBuilder = new StringBuilder();
	}

	public UnsyncStringWriter(int initialCapacity) {
		stringBuilder = new StringBuilder(initialCapacity);
	}

	@Override
	public UnsyncStringWriter append(char c) {
		write(c);

		return this;
	}

	@Override
	public UnsyncStringWriter append(CharSequence charSequence) {
		if (charSequence == null) {
			write("null");
		}
		else {
			write(charSequence.toString());
		}

		return this;
	}

	@Override
	public UnsyncStringWriter append(
		CharSequence charSequence, int start, int end) {

		if (charSequence == null) {
			charSequence = "null";
		}

		write(charSequence.subSequence(start, end).toString());

		return this;
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}

	public void reset() {
		stringBuilder.setLength(0);
	}

	@Override
	public String toString() {
		return stringBuilder.toString();
	}

	@Override
	public void write(char[] chars) {
		write(chars, 0, chars.length);
	}

	@Override
	public void write(char[] chars, int offset, int length) {
		if (length <= 0) {
			return;
		}

		stringBuilder.append(chars, offset, length);
	}

	@Override
	public void write(int c) {
		stringBuilder.append((char)c);
	}

	@Override
	public void write(String string) {
		stringBuilder.append(string);
	}

	@Override
	public void write(String string, int offset, int length) {
		if ((string == null) ||
			((offset == 0) && (length == string.length()))) {

			write(string);
		}

		stringBuilder.append(string.substring(offset, offset + length));
	}

	protected StringBuilder stringBuilder;

}
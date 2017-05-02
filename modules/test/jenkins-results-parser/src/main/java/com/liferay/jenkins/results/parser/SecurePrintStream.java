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

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Peter Yoo
 */
public class SecurePrintStream extends PrintStream {

	public static SecurePrintStream getInstance() {
		if (_securePrintStream == null) {
			try {
				_securePrintStream = new SecurePrintStream(
					new SecurePrintStreamByteArrayOutputStream());
			}
			catch (UnsupportedEncodingException uee) {
				throw new RuntimeException(uee);
			}
		}

		return _securePrintStream;
	}

	@Override
	public PrintStream append(char c) {
		if (_suspendFlush) {
			return _tempPrintStream.append(c);
		}

		return super.append(c);
	}

	@Override
	public PrintStream append(CharSequence charSequence) {
		if (_suspendFlush) {
			return _tempPrintStream.append(charSequence);
		}

		return super.append(charSequence);
	}

	@Override
	public PrintStream append(CharSequence charSequence, int start, int end) {
		if (_suspendFlush) {
			return _tempPrintStream.append(charSequence, start, end);
		}

		return super.append(charSequence, start, end);
	}

	@Override
	public void flush() {
		if (_suspendFlush) {
			return;
		}

		synchronized (this) {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			try {
				_tempPrintStream = new PrintStream(byteArrayOutputStream);

				_suspendFlush = true;

				String content =
					_securePrintStreamByteArrayOutputStream.toString();

				content = JenkinsResultsParserUtil.redact(content);

				_systemOutPrintStream.print(content);
			}
			finally {
				_securePrintStreamByteArrayOutputStream.reset();

				_suspendFlush = false;

				try {
					_securePrintStreamByteArrayOutputStream.write(
						byteArrayOutputStream.toByteArray());
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				finally {
					byteArrayOutputStream.reset();
				}

				_tempPrintStream.close();
			}
		}
	}

	@Override
	public void print(boolean b) {
		if (_suspendFlush) {
			_tempPrintStream.print(b);

			return;
		}

		super.print(b);
	}

	@Override
	public void print(char c) {
		if (_suspendFlush) {
			_tempPrintStream.print(c);

			return;
		}

		super.print(c);
	}

	@Override
	public void print(char[] chars) {
		if (_suspendFlush) {
			_tempPrintStream.print(chars);

			return;
		}

		super.print(chars);
	}

	@Override
	public void print(double d) {
		if (_suspendFlush) {
			_tempPrintStream.print(d);

			return;
		}

		super.print(d);
	}

	@Override
	public void print(float f) {
		if (_suspendFlush) {
			_tempPrintStream.print(f);

			return;
		}

		super.print(f);
	}

	@Override
	public void print(int i) {
		if (_suspendFlush) {
			_tempPrintStream.print(i);

			return;
		}

		super.print(i);
	}

	@Override
	public void print(long l) {
		if (_suspendFlush) {
			_tempPrintStream.print(l);

			return;
		}

		super.print(l);
	}

	@Override
	public void print(Object object) {
		if (_suspendFlush) {
			_tempPrintStream.print(object);

			return;
		}

		super.print(object);
	}

	@Override
	public void print(String string) {
		if (_suspendFlush) {
			_tempPrintStream.print(string);

			return;
		}

		super.print(string);
	}

	@Override
	public void println() {
		if (_suspendFlush) {
			_tempPrintStream.println();

			return;
		}

		super.println();
	}

	@Override
	public void println(boolean b) {
		if (_suspendFlush) {
			_tempPrintStream.println(b);

			return;
		}

		super.println(b);
	}

	@Override
	public void println(char c) {
		if (_suspendFlush) {
			_tempPrintStream.println(c);

			return;
		}

		super.println(c);
	}

	@Override
	public void println(char[] chars) {
		if (_suspendFlush) {
			_tempPrintStream.println(chars);

			return;
		}

		super.println(chars);
	}

	@Override
	public void println(double d) {
		if (_suspendFlush) {
			_tempPrintStream.println(d);

			return;
		}

		super.println(d);
	}

	@Override
	public void println(float f) {
		if (_suspendFlush) {
			_tempPrintStream.println(f);

			return;
		}

		super.println(f);
	}

	@Override
	public void println(int i) {
		if (_suspendFlush) {
			_tempPrintStream.println(i);

			return;
		}

		super.println(i);
	}

	@Override
	public void println(long l) {
		if (_suspendFlush) {
			_tempPrintStream.println(l);

			return;
		}

		super.println(l);
	}

	@Override
	public void println(Object object) {
		if (_suspendFlush) {
			_tempPrintStream.println(object);

			return;
		}

		super.println(object);
	}

	@Override
	public void println(String string) {
		if (_suspendFlush) {
			_tempPrintStream.println(string);

			return;
		}

		super.println(string);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		if (_suspendFlush) {
			_tempPrintStream.write(bytes);

			return;
		}

		super.write(bytes);
	}

	@Override
	public void write(byte[] buffer, int offset, int length) {
		if (_suspendFlush) {
			_tempPrintStream.write(buffer, offset, length);

			return;
		}

		super.write(buffer, offset, length);
	}

	@Override
	public void write(int b) {
		if (_suspendFlush) {
			_tempPrintStream.write(b);

			return;
		}

		super.write(b);
	}

	private SecurePrintStream(
			SecurePrintStreamByteArrayOutputStream
				securePrintStreamByteArrayOutputStream)
		throws UnsupportedEncodingException {

		super(securePrintStreamByteArrayOutputStream, true);

		_securePrintStreamByteArrayOutputStream =
			securePrintStreamByteArrayOutputStream;

		_securePrintStreamByteArrayOutputStream.setSecurePrintStream(this);

		_systemOutPrintStream = System.out;
	}

	private static SecurePrintStream _securePrintStream;

	private final SecurePrintStreamByteArrayOutputStream
		_securePrintStreamByteArrayOutputStream;
	private boolean _suspendFlush;
	private final PrintStream _systemOutPrintStream;
	private PrintStream _tempPrintStream;

	private static class SecurePrintStreamByteArrayOutputStream
		extends ByteArrayOutputStream {

		@Override
		public void flush() throws IOException {
			super.flush();

			if (_securePrintStream != null) {
				_securePrintStream.flush();
			}
		}

		public void setSecurePrintStream(SecurePrintStream securePrintStream) {
			_securePrintStream = securePrintStream;
		}

		private SecurePrintStream _securePrintStream;

	}

}
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

package com.liferay.petra.io;

import com.liferay.petra.io.unsync.UnsyncBufferedOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raymond Augé
 */
public class FileCacheOutputStream extends OutputStream {

	public FileCacheOutputStream() throws IOException {
		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

		UUID uuid = new UUID(
			threadLocalRandom.nextLong(), threadLocalRandom.nextLong());

		_tempFile = File.createTempFile(
			uuid.toString() + StringPool.DASH, _EXTENSION);

		_ubos = new UnsyncBufferedOutputStream(
			new FileOutputStream(_tempFile), _BUFFER);
	}

	public void cleanUp() {
		try {
			flush();
			close();

			if (_fis != null) {
				_fis.close();
			}

			try (Stream<Path> stream = Files.walk(
					_tempFile.toPath(), FileVisitOption.FOLLOW_LINKS)) {

				Stream<Path> sortedStream = stream.sorted(
					Comparator.reverseOrder());

				sortedStream.map(Path::toFile).forEach(File::delete);
			}
		}
		catch (IOException ioe) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioe.getMessage());
			}
		}
	}

	@Override
	public void close() throws IOException {
		_ubos.close();
	}

	@Override
	public void flush() throws IOException {
		_ubos.flush();
	}

	public byte[] getBytes() throws IOException {
		flush();
		close();

		return Files.readAllBytes(_tempFile.toPath());
	}

	public File getFile() throws IOException {
		flush();
		close();

		return _tempFile;
	}

	public FileInputStream getFileInputStream() throws IOException {
		if (_fis == null) {
			flush();
			close();

			_fis = new FileInputStream(_tempFile);
		}

		return _fis;
	}

	public long getSize() {
		return _tempFile.length();
	}

	@Override
	public void write(byte[] b) throws IOException {
		_ubos.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		_ubos.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		_ubos.write(b);
	}

	private static final int _BUFFER = 2048;

	private static final String _EXTENSION = ".fcos";

	private static final Logger _log = LoggerFactory.getLogger(
		FileCacheOutputStream.class);

	private FileInputStream _fis;
	private final File _tempFile;
	private final UnsyncBufferedOutputStream _ubos;

}
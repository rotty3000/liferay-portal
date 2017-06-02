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

package com.liferay.petra.io.nio.intraband.welder.socket;

import com.liferay.petra.io.SocketUtil;
import com.liferay.petra.io.SocketUtil.ServerSocketConfigurator;
import com.liferay.petra.io.convert.Conversions;
import com.liferay.petra.io.internal.util.InetAddressUtil;
import com.liferay.petra.io.nio.intraband.Intraband;
import com.liferay.petra.io.nio.intraband.RegistrationReference;
import com.liferay.petra.io.nio.intraband.welder.BaseWelder;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Shuyang Zhou
 */
public class SocketWelder extends BaseWelder {

	public SocketWelder() throws IOException {

		// Assignments have to stay in the constructor because we need to
		// differentiate between a constructor created object and a
		// deserialization created object. Only the constructor created object
		// needs to assign values. The deserialization created object gets its
		// values from the original object.

		bufferSize = Configuration.bufferSize;
		keepAlive = Configuration.keepAlive;
		reuseAddress = Configuration.reuseAddress;
		soLinger = Configuration.soLinger;
		soTimeout = Configuration.soTimeout;
		tcpNoDelay = Configuration.tcpNoDelay;

		serverSocketChannel = SocketUtil.createServerSocketChannel(
			InetAddressUtil.getLoopbackInetAddress(),
			Configuration.serverStartPort,
			new SocketWelderServerSocketConfigurator());

		ServerSocket serverSocket = serverSocketChannel.socket();

		serverPort = serverSocket.getLocalPort();
	}

	@Override
	protected void doDestroy() throws IOException {
		socketChannel.close();
	}

	@Override
	protected RegistrationReference weldClient(Intraband intraband)
		throws IOException {

		socketChannel = SocketChannel.open();

		_configureSocket(socketChannel.socket());

		socketChannel.connect(
			new InetSocketAddress(
				InetAddressUtil.getLoopbackInetAddress(), serverPort));

		return intraband.registerChannel(socketChannel);
	}

	@Override
	protected RegistrationReference weldServer(Intraband intraband)
		throws IOException {

		socketChannel = serverSocketChannel.accept();

		serverSocketChannel.close();

		_configureSocket(socketChannel.socket());

		return intraband.registerChannel(socketChannel);
	}

	protected final int bufferSize;
	protected final boolean keepAlive;
	protected final boolean reuseAddress;
	protected final int serverPort;
	protected final transient ServerSocketChannel serverSocketChannel;
	protected transient SocketChannel socketChannel;
	protected final int soLinger;
	protected final int soTimeout;
	protected final boolean tcpNoDelay;

	protected static class Configuration {

		protected static final int bufferSize = Conversions.getInteger(
			System.getProperty("intraband.welder.socket.buffer.size"));
		protected static final boolean keepAlive = Conversions.getBoolean(
			System.getProperty("intraband.welder.socket.keep.alive"));
		protected static final boolean reuseAddress = Conversions.getBoolean(
			System.getProperty("intraband.welder.socket.reuse.address"));
		protected static final int serverStartPort = Conversions.getInteger(
			System.getProperty("intraband.welder.socket.server.start.port"));
		protected static final int soLinger = Conversions.getInteger(
			System.getProperty("intraband.welder.socket.so.linger"));
		protected static final int soTimeout = Conversions.getInteger(
			System.getProperty("intraband.welder.socket.so.timeout"));
		protected static final boolean tcpNoDelay = Conversions.getBoolean(
			System.getProperty("intraband.welder.socket.tcp.no.delay"));

	}

	class SocketWelderServerSocketConfigurator
		implements ServerSocketConfigurator {

		@Override
		public void configure(ServerSocket serverSocket)
			throws SocketException {

			serverSocket.setReceiveBufferSize(bufferSize);
			serverSocket.setReuseAddress(reuseAddress);
			serverSocket.setSoTimeout(soTimeout);
		}

	}

	private void _configureSocket(Socket socket) throws SocketException {
		socket.setKeepAlive(keepAlive);
		socket.setReceiveBufferSize(bufferSize);
		socket.setReuseAddress(reuseAddress);
		socket.setSendBufferSize(bufferSize);

		if (soLinger <= 0) {
			socket.setSoLinger(false, soLinger);
		}
		else {
			socket.setSoLinger(true, soLinger);
		}

		socket.setSoTimeout(soTimeout);
		socket.setTcpNoDelay(tcpNoDelay);
	}

}
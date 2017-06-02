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

package com.liferay.petra.io.nio.intraband.rpc;

import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.io.nio.intraband.BaseAsyncDatagramReceiveHandler;
import com.liferay.petra.io.nio.intraband.Datagram;
import com.liferay.petra.io.nio.intraband.Intraband;
import com.liferay.petra.io.nio.intraband.RegistrationReference;
import com.liferay.petra.process.ProcessCallable;

import java.io.Serializable;

import java.util.concurrent.Executor;

/**
 * @author Shuyang Zhou
 */
public class RPCDatagramReceiveHandler extends BaseAsyncDatagramReceiveHandler {

	public RPCDatagramReceiveHandler(Executor executor) {
		super(executor);
	}

	@Override
	protected void doReceive(
			RegistrationReference registrationReference, Datagram datagram)
		throws Exception {

		Deserializer deserializer = new Deserializer(
			datagram.getDataByteBuffer());

		Serializer serializer = new Serializer();

		try {
			ProcessCallable<? extends Serializable> processCallable =
				deserializer.readObject();

			serializer.writeObject(new RPCResponse(processCallable.call()));
		}
		catch (Exception e) {
			serializer.writeObject(new RPCResponse(e));
		}

		Intraband intraband = registrationReference.getIntraband();

		intraband.sendDatagram(
			registrationReference,
			Datagram.createResponseDatagram(
				datagram, serializer.toByteBuffer()));
	}

}
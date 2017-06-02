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

package com.liferay.petra.io.nio.intraband.mailbox;

import com.liferay.petra.io.BigEndianCodec;
import com.liferay.petra.io.nio.intraband.BaseAsyncDatagramReceiveHandler;
import com.liferay.petra.io.nio.intraband.Datagram;
import com.liferay.petra.io.nio.intraband.Intraband;
import com.liferay.petra.io.nio.intraband.RegistrationReference;

import java.nio.ByteBuffer;

import java.util.concurrent.Executor;

/**
 * @author Shuyang Zhou
 */
public class MailboxDatagramReceiveHandler
	extends BaseAsyncDatagramReceiveHandler {

	public MailboxDatagramReceiveHandler(Executor executor) {
		super(executor);
	}

	@Override
	protected void doReceive(
		RegistrationReference registrationReference, Datagram datagram) {

		long receipt = MailboxUtil.depositMail(datagram.getDataByteBuffer());

		byte[] data = new byte[8];

		BigEndianCodec.putLong(data, 0, receipt);

		Intraband intraband = registrationReference.getIntraband();

		intraband.sendDatagram(
			registrationReference,
			Datagram.createResponseDatagram(datagram, ByteBuffer.wrap(data)));
	}

}
package com.liferay.petra.messaging.test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.api.MessageListenerException;

public class CallableMessageListener implements Callable<Message>, MessageListener {

	@Override
	public Message call() throws Exception {
		return _message.get();
	}

	@Override
	public void receive(Message message) throws MessageListenerException {
		_message.set(message);
	}

	private final AtomicReference<Message> _message = new AtomicReference<>();

}

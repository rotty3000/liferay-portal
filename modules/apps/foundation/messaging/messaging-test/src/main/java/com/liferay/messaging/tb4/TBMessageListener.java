package com.liferay.messaging.tb4;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"destination.name=configuration/tb4"
	},
	scope = ServiceScope.SINGLETON,
	service = {Callable.class, MessageListener.class}
)
public class TBMessageListener implements Callable<Message>, MessageListener{

	@Override
	public void receive(Message message) throws MessageListenerException {
		_message = message;

		_latch.countDown();
	}

	@Override
	public Message call() throws Exception {
		_latch.await();

		return _message;
	}

	private final CountDownLatch _latch = new CountDownLatch(1);
	private volatile Message _message;

}
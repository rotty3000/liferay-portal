package com.liferay.messaging.tb2;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
	property = {
		"destination.name=parallel/test"
	},
	scope = ServiceScope.SINGLETON,
	service = {Callable.class, MessageListener.class}
)
public class TBMessageListener implements Callable<Message>, MessageListener{

	@Override
	public void receive(Message message) throws MessageListenerException {
		_message = message;

		_latch.countDown();

		if (_log.isDebugEnabled()) {
			_log.debug("Received message {} from {}", message, this);
		}
	}

	@Override
	public Message call() throws Exception {
		_latch.await(10, TimeUnit.SECONDS);

		if (_log.isDebugEnabled()) {
			_log.debug("Checking message {} from {}", _message, this);
		}

		return _message;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		TBMessageListener.class);

	private final CountDownLatch _latch = new CountDownLatch(1);

	private volatile Message _message;

}
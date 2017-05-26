package com.liferay.messaging.tb1;

import com.liferay.messaging.Message;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;

import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	property = {
		"destination.name=synchronous/test"
	},
	scope = ServiceScope.SINGLETON,
	service = {Callable.class, MessageListener.class}
)
public class TBMessageListener implements Callable<Message>, MessageListener{

	@Override
	public void receive(Message message) throws MessageListenerException {
		_message = message;
	}

	@Override
	public Message call() throws Exception {
		return _message;
	}

	private volatile Message _message;

}
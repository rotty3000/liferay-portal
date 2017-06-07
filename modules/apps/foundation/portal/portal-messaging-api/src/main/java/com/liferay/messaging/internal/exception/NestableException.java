package com.liferay.messaging.internal.exception;

/**
 * @author Brian Wing Shun Chan
 */
public class NestableException extends Exception {

	public NestableException() {
	}

	public NestableException(String msg) {
		super(msg);
	}

	public NestableException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NestableException(Throwable cause) {
		super(cause);
	}

}

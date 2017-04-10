package com.liferay.messaging.internal.util;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 * @see    com.liferay.portal.kernel.servlet.NonSerializableObjectHandler
 */
public class TransientValue<V> implements Serializable {

	public TransientValue() {
	}

	public TransientValue(V value) {
		_value = value;
	}

	public V getValue() {
		return _value;
	}

	public boolean isNotNull() {
		return !isNull();
	}

	public boolean isNull() {
		if (_value == null) {
			return true;
		}
		else {
			return false;
		}
	}

	public void setValue(V value) {
		_value = value;
	}

	private transient V _value;

}

package com.liferay.messaging.internal.util;

import java.util.function.Supplier;

/**
 * @author Shuyang Zhou
 */
public class AutoResetThreadLocal<T> extends InitialThreadLocal<T> {

	public AutoResetThreadLocal(String name) {
		super(name, () -> null, true);
	}

	public AutoResetThreadLocal(String name, Supplier<T> supplier) {
		super(name, supplier, true);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #AutoResetThreadLocal(
	 *             String, Supplier)}
	 */
	@Deprecated
	@SuppressWarnings("deprecation")
	public AutoResetThreadLocal(String name, T initialValue) {
		super(name, initialValue, true);
	}

}

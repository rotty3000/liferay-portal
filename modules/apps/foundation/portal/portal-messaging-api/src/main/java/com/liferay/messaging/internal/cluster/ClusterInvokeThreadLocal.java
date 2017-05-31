package com.liferay.messaging.internal.cluster;

import com.liferay.messaging.internal.util.AutoResetThreadLocal;

/**
 * @author Shuyang Zhou
 */
public class ClusterInvokeThreadLocal {

	public static boolean isEnabled() {
		return _enabled.get();
	}

	public static void setEnabled(boolean enabled) {
		_enabled.set(enabled);
	}

	private static final ThreadLocal<Boolean> _enabled =
		new AutoResetThreadLocal<>(
			ClusterInvokeThreadLocal.class + "._enabled", () -> Boolean.TRUE);

}

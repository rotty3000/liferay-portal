package com.liferay.messaging.internal.util;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class InitialThreadLocal<T> extends CentralizedThreadLocal<T> {

	public InitialThreadLocal(String name, Supplier<T> supplier) {
		this(name, supplier, false);
	}

	public InitialThreadLocal(
		String name, Supplier<T> supplier, boolean shortLived) {

		super(shortLived);

		_name = name;

		if (supplier == null) {
			_supplier = () -> null;
		}
		else {
			_supplier = supplier;
		}
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #InitialThreadLocal(
	 *             String, Supplier, boolean)}
	 */
	@Deprecated
	public InitialThreadLocal(String name, T initialValue) {
		this(name, new CloneableSupplier<>(initialValue), false);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #InitialThreadLocal(
	 *             String, Supplier, boolean)}
	 */
	@Deprecated
	public InitialThreadLocal(String name, T initialValue, boolean shortLived) {
		this(name, new CloneableSupplier<>(initialValue), shortLived);
	}

	@Override
	public String toString() {
		if (_name == null) {
			return super.toString();
		}

		return _name;
	}

	@Override
	protected T initialValue() {
		return _supplier.get();
	}

	private static final String _METHOD_CLONE = "clone";

	private static final Logger _logger = LoggerFactory.getLogger(
		InitialThreadLocal.class);

	private final String _name;
	private final Supplier<T> _supplier;

	private static class CloneableSupplier<T> implements Supplier<T> {

		@Override
		@SuppressWarnings("unchecked")
		public T get() {
			if (_cloneMethod != null) {
				try {
					return (T)_cloneMethod.invoke(_initialValue);
				}
				catch (Exception e) {
					_logger.error(e.toString(), e);
				}
			}

			return _initialValue;
		}

		private CloneableSupplier(T initialValue) {
			Method cloneMethod = null;

			if (initialValue instanceof Cloneable) {
				try {
					Class<?> clazz = initialValue.getClass();

					cloneMethod = clazz.getMethod(_METHOD_CLONE);
				}
				catch (Exception e) {
					_logger.error(e.toString(), e);
				}
			}

			_cloneMethod = cloneMethod;
			_initialValue = initialValue;
		}

		private final Method _cloneMethod;
		private final T _initialValue;

	}

}

package com.liferay.messaging.internal.concurrent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.liferay.messaging.internal.util.MapBackedSet;

/**
 * @author Brian Wing Shun Chan
 */
public class ConcurrentHashSet<E> extends MapBackedSet<E> {

	public ConcurrentHashSet() {
		super(new ConcurrentHashMap<E, Boolean>());
	}

	public ConcurrentHashSet(int capacity) {
		super(new ConcurrentHashMap<E, Boolean>(capacity));
	}

	public ConcurrentHashSet(Set<E> set) {
		super(new ConcurrentHashMap<E, Boolean>());

		addAll(set);
	}

}

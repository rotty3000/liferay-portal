/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.cache.key;

import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Brian Wing Shun Chan
 */
public class ThreadSafeCacheKeyGenerator extends CacheKeyGeneratorWrapper {

	public ThreadSafeCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
		super(cacheKeyGenerator);
	}

	@Override
	public CacheKeyGenerator append(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheKeyGenerator append(String[] keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheKeyGenerator append(StringBundler sb) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheKeyGenerator clone() {
		return this;
	}

}
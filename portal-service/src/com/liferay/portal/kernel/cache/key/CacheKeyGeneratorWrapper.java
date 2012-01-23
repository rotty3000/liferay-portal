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

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 */
public class CacheKeyGeneratorWrapper implements CacheKeyGenerator {

	public CacheKeyGeneratorWrapper(CacheKeyGenerator cacheKeyGenerator) {
		_cacheKeyGenerator = cacheKeyGenerator;
	}

	public CacheKeyGenerator append(String key) {
		return _cacheKeyGenerator.append(key);
	}

	public CacheKeyGenerator append(String[] keys) {
		return _cacheKeyGenerator.append(keys);
	}

	public CacheKeyGenerator append(StringBundler sb) {
		return _cacheKeyGenerator.append(sb);
	}

	@Override
	public CacheKeyGenerator clone() {
		return new CacheKeyGeneratorWrapper(_cacheKeyGenerator.clone());
	}

	public Serializable finish() {
		return _cacheKeyGenerator.finish();
	}

	public Serializable getCacheKey(String key) {
		return _cacheKeyGenerator.getCacheKey(key);
	}

	public Serializable getCacheKey(String[] keys) {
		return _cacheKeyGenerator.getCacheKey(keys);
	}

	public Serializable getCacheKey(StringBundler sb) {
		return _cacheKeyGenerator.getCacheKey(sb);
	}

	public boolean isCallingGetCacheKeyThreadSafe() {
		return _cacheKeyGenerator.isCallingGetCacheKeyThreadSafe();
	}

	private CacheKeyGenerator _cacheKeyGenerator;

}
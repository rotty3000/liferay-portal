/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.servlet.filters.sso.cas;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;

import java.io.Serializable;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;

public class CacheProxyGrantingTicketStorage
	implements ProxyGrantingTicketStorage {

	public CacheProxyGrantingTicketStorage() {
		_cache = MultiVMPoolUtil.getCache(
			CacheProxyGrantingTicketStorage.class.getName());
	}

	@Override
	public void cleanUp() {
		//No need to implement this because cache will evict entries
	}

	@Override
	public String retrieve(String key) {
		return (String)_cache.get(key);
	}

	@Override
	public void save(String key, String value) {
		_cache.put(key, value);
	}

	private PortalCache<Serializable, Serializable> _cache;

}
/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.osgi.http.internal.holder;


import javax.servlet.Filter;

import org.osgi.service.http.runtime.FilterDTO;

/**
 * @author Raymond Augé
 */
public class FilterHolder extends Holder<Filter, FilterDTO>
	implements Comparable<FilterHolder> {

	public FilterHolder(
		Filter filter, FilterDTO filterDTO, long serviceRanking) {

		super(filter, filterDTO);

		this.serviceRanking = serviceRanking;
	}

	@Override
	public int compareTo(FilterHolder filterHolder) {
		return new FilterHolderComparator().compare(this, filterHolder);
	}

	@Override
	public void destroy() {
		t.destroy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object object) {
		FilterHolder filterHolder = (FilterHolder)object;

		if (d.serviceId == filterHolder.d.serviceId) {
			return true;
		}

		return false;
	}

	@Override
	public Filter match(String requestURI, String name) {
		if (name != null) {
			if (d.name.equals(name)) {
				return t;
			}

			return null;
		}

		for (String pattern : d.patterns) {
			if ((pattern != null) && match(pattern, requestURI, true)) {
				return t;
			}
		}

		return null;
	}

	public final long serviceRanking;

}
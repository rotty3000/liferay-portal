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


import java.util.Comparator;

/**
 * @author Raymond Augé
 */
public class FilterHolderComparator implements Comparator<FilterHolder> {

	@Override
	public int compare(
		FilterHolder serviceComparable1,
		FilterHolder serviceComparable2) {

		if (serviceComparable1.serviceRanking <
				serviceComparable2.serviceRanking) {

			return -1;
		}
		else if (serviceComparable1.serviceRanking >
					serviceComparable2.serviceRanking) {

			return 1;
		}

		if (serviceComparable1.d.serviceId <
				serviceComparable2.d.serviceId) {

			return -1;
		}
		else if (serviceComparable1.d.serviceId >
					serviceComparable2.d.serviceId) {

			return 1;
		}

		return 0;
	}

}
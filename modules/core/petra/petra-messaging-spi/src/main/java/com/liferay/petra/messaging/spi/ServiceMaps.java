/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.petra.messaging.spi;

import com.liferay.petra.reflect.ReflectionUtil;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @author Raymond Augé
 */
public class ServiceMaps {

	public static final Comparator<Map<String, Object>> comparator() {
		return _INSTANCE;
	}

	public static final <T> Map.Entry<Map<String, Object>, T> findMatch(
		String finterString, Map<Map<String, Object>, T> map) {

		try {
			Filter filter = FrameworkUtil.createFilter(finterString);

			Set<Entry<Map<String, Object>, T>> entrySet = map.entrySet();

			Stream<Entry<Map<String, Object>, T>> stream = entrySet.stream();

			return stream.filter(
				entry -> filter.matches(entry.getKey())
			).findFirst(
			).orElse(
				null
			);
		}
		catch (InvalidSyntaxException ise) {
			return ReflectionUtil.throwException(ise);
		}
	}

	public static final Filter formatFilter(
		String format, String... arguments) {

		try {
			return FrameworkUtil.createFilter(String.format(format, arguments));
		}
		catch (InvalidSyntaxException ise) {
			return ReflectionUtil.throwException(ise);
		}
	}

	public static final <T> T remove(
		String finterString, Map<Map<String, Object>, T> map) {

		Entry<Map<String, Object>, T> match = findMatch(finterString, map);

		return map.remove(match.getKey());
	}

	public static class ServiceMapComparator
		implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> map, Map<String, Object> other) {
			Long id = (Long)map.get(Constants.SERVICE_ID);
			Long otherId = (Long)other.get(Constants.SERVICE_ID);

			if (id.equals(otherId)) {

				// same service

				return 0;
			}

			Object rankingObj = map.get(Constants.SERVICE_RANKING);
			Object otherRankingObj = other.get(Constants.SERVICE_RANKING);

			// If no rank, then spec says it defaults to zero.

			if (rankingObj == null) {
				rankingObj = _ZERO;
			}

			if (otherRankingObj == null) {
				otherRankingObj = _ZERO;
			}

			// If rank is not Integer, then spec says it defaults to zero.

			Integer ranking = _ZERO;

			if (rankingObj instanceof Integer) {
				ranking = (Integer)rankingObj;
			}

			Integer otherRanking = _ZERO;

			if (otherRankingObj instanceof Integer) {
				otherRanking = (Integer)otherRankingObj;
			}

			// Sort by rank in ascending order.

			if (ranking.compareTo(otherRanking) < 0) {

				// lower rank

				return -1;
			}
			else if (ranking.compareTo(otherRanking) > 0) {

				// higher rank

				return 1;
			}

			// If ranks are equal, then sort by service id in descending order.

			if (id.compareTo(otherId) < 0) {
				return 1;
			}

			return -1;
		}

	}

	private ServiceMaps() {

		// Do not instantiate!

	}

	private static final ServiceMapComparator _INSTANCE =
		new ServiceMapComparator();

	private static final Integer _ZERO = Integer.valueOf(0);

}
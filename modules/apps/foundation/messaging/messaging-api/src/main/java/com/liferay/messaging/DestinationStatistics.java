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

package com.liferay.messaging;

/**
 * DestinationStatistics is meant for informational purposes only. The datum
 * contained may not add up. They are assembled as a best effort and may
 * contain slight discrepancies. However, after forced
 * {@link Destination#close(boolean)} operation, the final results must add
 * up.
 *
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public interface DestinationStatistics {

	public int getActiveThreadCount();

	public int getCurrentThreadCount();

	public int getLargestThreadCount();

	public int getMaxThreadPoolSize();

	public int getMinThreadPoolSize();

	public long getPendingMessageCount();

	public long getSentMessageCount();

}
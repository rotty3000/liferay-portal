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

package com.liferay.petra.messaging.api;

/**
 * DestinationStatistics is meant for informational purposes only. The datum
 * contained may not add up. They are assembled as a best effort and may contain
 * slight discrepancies. However, after forced {@link
 * Destination#close(boolean)} operation, the final results must add up.
 *
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public interface DestinationStatistics {

	/**
	 * Returns the approximate number of threads that are currently executing tasks.
	 *
	 * @return the approximate number of threads that are currently executing tasks
	 */
	public int getActiveThreadCount();

	/**
	 * Returns the current number of threads.
	 *
	 * @return the current number of threads
	 */
	public int getCurrentThreadCount();

	/**
	 * Returns the largest number of threads that have ever simultaneously been in the pool.
	 *
	 * @return the largest number of threads that have ever simultaneously been in the pool
	 */
	public int getLargestThreadCount();

	/**
	 * Returns the maximum allowed number of threads.
	 *
	 * @return the maximum allowed number of threads
	 */
	public int getMaxThreadPoolSize();

	/**
	 * Returns the core number of threads.
	 *
	 * @return the core number of threads
	 */
	public int getMinThreadPoolSize();

	/**
	 * Returns the number of messages queued up waiting to be dispatched.
	 *
	 * @return the number of messages queued up waiting to be dispatched
	 */
	public long getPendingMessageCount();

	/**
	 * Returns the approximate number of messages that have been sent.
	 *
	 * @return the approximate number of messages that have been sent
	 */
	public long getSentMessageCount();

}
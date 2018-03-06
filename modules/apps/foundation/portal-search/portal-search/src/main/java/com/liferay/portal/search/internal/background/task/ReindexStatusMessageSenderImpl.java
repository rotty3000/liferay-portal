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

package com.liferay.portal.search.internal.background.task;

import com.liferay.petra.messaging.api.DestinationNames;
import com.liferay.petra.messaging.api.Message;
import com.liferay.petra.messaging.api.MessageBuilder;
import com.liferay.petra.messaging.api.MessageBuilderFactory;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusMessageSender;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrew Betts
 */
@Component(immediate = true, service = ReindexStatusMessageSender.class)
public class ReindexStatusMessageSenderImpl
	implements ReindexStatusMessageSender {

	@Override
	public void sendStatusMessage(String className, long count, long total) {
		MessageBuilder messageBuilder = _messageBuilderFactory.create(
			DestinationNames.BACKGROUND_TASK_STATUS);

		messageBuilder.put(
			BackgroundTaskConstants.BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		messageBuilder.put(ReindexBackgroundTaskConstants.CLASS_NAME, className);
		messageBuilder.put(ReindexBackgroundTaskConstants.COUNT, count);
		messageBuilder.put(ReindexBackgroundTaskConstants.TOTAL, total);
		messageBuilder.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		Message message = messageBuilder.build();

		sendBackgroundTaskStatusMessage(message);
	}

	@Override
	public void sendStatusMessage(
		String phase, long companyId, long[] companyIds) {

		MessageBuilder messageBuilder = _messageBuilderFactory.create(
			DestinationNames.BACKGROUND_TASK_STATUS);

		messageBuilder.put(
			BackgroundTaskConstants.BACKGROUND_TASK_ID,
			BackgroundTaskThreadLocal.getBackgroundTaskId());
		messageBuilder.put(ReindexBackgroundTaskConstants.COMPANY_ID, companyId);
		messageBuilder.put(ReindexBackgroundTaskConstants.COMPANY_IDS, companyIds);
		messageBuilder.put(ReindexBackgroundTaskConstants.PHASE, phase);
		messageBuilder.put("status", BackgroundTaskConstants.STATUS_IN_PROGRESS);

		Message message = messageBuilder.build();

		sendBackgroundTaskStatusMessage(message);
	}

	protected void sendBackgroundTaskStatusMessage(Message message) {
		_backgroundTaskStatusMessageSender.sendBackgroundTaskStatusMessage(
			message);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Sent reindex background task status message: " + message);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReindexStatusMessageSenderImpl.class);

	private static final int _timeout = 1000;

	@Reference
	private MessageBuilderFactory _messageBuilderFactory;

	@Reference
	private BackgroundTaskStatusMessageSender
		_backgroundTaskStatusMessageSender;

}
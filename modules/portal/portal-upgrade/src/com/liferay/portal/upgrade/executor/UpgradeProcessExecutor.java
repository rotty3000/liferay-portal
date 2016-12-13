package com.liferay.portal.upgrade.executor;

import com.liferay.portal.upgrade.task.UpgradeTask;

import java.util.Map;
import java.util.concurrent.Future;

public interface UpgradeProcessExecutor {

	public Future<Void> schedule(
		UpgradeTask upgradeTask, UpgradeTask... predecessors);

	public Map<String, Throwable> errors();

}
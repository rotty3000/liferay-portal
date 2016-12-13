package com.liferay.portal.upgrade.task;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.concurrent.Callable;
public class UpgradeTask implements Callable<Void> {

	public static UpgradeTask create(UpgradeProcess upgradeProcess) {
		return new UpgradeTask(upgradeProcess.getClass().getName())
	}
	public UpgradeTask(String id, UpgradeProcess upgradeProcess) {
		_id = id;
		_upgradeProcess = upgradeProcess;
	}

	@Override
	public Void call() throws Exception {
		System.out.println("Starting upgrade process " + getId());

		_upgradeProcess.upgrade();

		System.out.println("Process " + getId() + " has finished!");
		return null;
	}

	public String getId() {
		return _id;
	}

	private final String _id;
	private final UpgradeProcess _upgradeProcess;

}
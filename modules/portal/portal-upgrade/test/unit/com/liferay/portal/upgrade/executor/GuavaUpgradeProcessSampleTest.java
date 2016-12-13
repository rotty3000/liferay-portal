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

package com.liferay.portal.upgrade.executor;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.upgrade.executor.impl.GuavaFutureExecutor;
import com.liferay.portal.upgrade.task.UpgradeTask;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * @author Miguel Pastor
 */
public class GuavaUpgradeProcessSampleTest {

	public static void main(String[] args) throws Exception {
		new GuavaUpgradeProcessSampleTest().container();
	}

	@Test
	public void container() {
		final UpgradeProcessExecutor upgradeProcessExecutor =
			new GuavaFutureExecutor(Executors.newCachedThreadPool());

		final UpgradeTask a = new UpgradeTask("a", new A());
		final UpgradeTask b = new UpgradeTask("b", new B());
		final UpgradeTask c = new UpgradeTask("c", new C());
		final UpgradeTask d = new UpgradeTask("d", new D());
		final UpgradeTask e = new UpgradeTask("e", new E());
		final UpgradeTask f = new UpgradeTask("f", new F());
		final UpgradeTask g = new UpgradeTask("g", new A());
		final UpgradeTask h = new UpgradeTask("h", new A());
		final UpgradeTask failed = new UpgradeTask("failed", new Failed());

		upgradeProcessExecutor.schedule(a);

		upgradeProcessExecutor.schedule(b, a);
		upgradeProcessExecutor.schedule(c, a);
		upgradeProcessExecutor.schedule(d, a);
		//upgradeProcessExecutor.schedule(failed, a);

		// upgradeProcessExecutor.schedule(g, b, c, d, failed);
		upgradeProcessExecutor.schedule(g, b, c, d);

		// Future<Void> voidFuture = upgradeProcessExecutor.schedule(f, failed);
		Future<Void> voidFuture = upgradeProcessExecutor.schedule(f, g);

		try {
			Void aVoid = voidFuture.get();
		} catch (InterruptedException ie) {
		} catch (ExecutionException ee) {
		}

		System.out.println("Finish!");

		Map<String, Throwable> errors = upgradeProcessExecutor.errors();

		System.out.println("-------------- ERRORS -------------------");
		for (Map.Entry<String, Throwable> entry : errors.entrySet()) {
			System.out.println(
				"Upgrade process " + entry.getKey() + " has failed with error");
		}
		System.out.println("--------------------------------------");
	}

	private static class A extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(3000);
		}

	}

	private static class B extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(6000);
		}

	}

	private static class C extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(8000);
		}

	}

	private static class D extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(10000);
		}

	}

	private static class E extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(9000);
		}

	}

	private static class F extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(200000);
		}

	}

	private static class Failed extends UpgradeProcess {

		@Override
		public void doUpgrade() throws Exception {
			Thread.sleep(5000);

			throw new Exception("Failed upgrade!!");
		}

	}

}
package com.liferay.portal.upgrade.executor.impl;

import com.google.common.util.concurrent.*;

import com.liferay.portal.upgrade.executor.UpgradeProcessExecutor;
import com.liferay.portal.upgrade.task.UpgradeTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class GuavaFutureExecutor implements UpgradeProcessExecutor {

	public GuavaFutureExecutor(ExecutorService executorService) {
		_listeningExecutorService = MoreExecutors.listeningDecorator(
			executorService);
	}

	@Override
	public Map<String, Throwable> errors() {
		return Collections.unmodifiableMap(_failedTasks);
	}

	@Override
	public Future<Void> schedule(
		final UpgradeTask upgradeTask, UpgradeTask... predecessors) {

		String taskId = upgradeTask.getId();

		if (_scheduledTasks.containsKey(taskId)) {
			throw new IllegalArgumentException(
				"Task " + taskId + " already registered!");
		}

		List<ListenableFuture<Void>> predecessorFutures = new ArrayList<>();

		for (UpgradeTask predecessorTask : predecessors) {
			ListenableFuture<Void> predecessorTaskFuture = _scheduledTasks.get(
				predecessorTask.getId());

			if (predecessorTaskFuture == null) {
				throw new IllegalArgumentException(
					"Predecessor task " + predecessorTaskFuture +
						" doesn't exist.");
			}

			predecessorFutures.add(predecessorTaskFuture);
		}

		ListenableFuture<Void> scheduledTaskFuture;

		if (predecessorFutures.isEmpty()) {
			scheduledTaskFuture = _schedule(upgradeTask);
		}
		else {
			scheduledTaskFuture = Futures.transform(
				Futures.allAsList(predecessorFutures),
				new AsyncFunction<List<Void>, Void>() {

					@Override
					public ListenableFuture<Void> apply(List<Void> input)
						throws Exception {

						return _schedule(upgradeTask);
					}

			}, _listeningExecutorService);
		}

		_scheduledTasks.put(taskId, scheduledTaskFuture);

		return scheduledTaskFuture;
	}

	private ListenableFuture<Void> _schedule(final UpgradeTask upgradeTask) {
		final ListenableFuture<Void> listenableFuture =
			_listeningExecutorService.submit(upgradeTask);

		Futures.addCallback(listenableFuture, new FutureCallback<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
			}

			@Override
			public void onFailure(Throwable throwable) {
				_failedTasks.put(upgradeTask.getId(), throwable);
			}
		});

		return listenableFuture;
	}

	private final ListeningExecutorService _listeningExecutorService;
	private volatile ConcurrentMap<String, Throwable> _failedTasks =
		new ConcurrentHashMap<>();
	private final Map<String, ListenableFuture<Void>> _scheduledTasks =
		new HashMap<>();

}
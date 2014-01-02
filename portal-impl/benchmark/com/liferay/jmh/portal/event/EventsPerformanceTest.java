/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.jmh.portal.event;

import com.liferay.jmh.portal.event.dependency.BeanchmarkLifecycleEvent;
import com.liferay.jmh.portal.event.dependency.BenchmarkLifecycleAction;
import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.module.framework.ModuleFrameworkUtilAdapter;
import com.liferay.portal.spring.util.SpringUtil;
import com.liferay.portal.util.InitUtil;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * @author Raymond Augé
 */
@State(Scope.Benchmark)
public class EventsPerformanceTest {

	@GenerateMicroBenchmark
	public LifecycleEvent array() throws ActionException {
		BeanchmarkLifecycleEvent lifecycleEvent =
			new BeanchmarkLifecycleEvent();

		for (LifecycleAction lifecycleAction : _array) {
			lifecycleAction.processLifecycleEvent(lifecycleEvent);
		}

		assert lifecycleEvent.counter.get() == 4;

		return lifecycleEvent;
	}

	@GenerateMicroBenchmark
	public LifecycleEvent eventProcessorUtil_process_classNames()
		throws ActionException {

		BeanchmarkLifecycleEvent lifecycleEvent =
			new BeanchmarkLifecycleEvent();

		EventsProcessorUtil.process(null, _EVENT_CLASSNAMES_B, lifecycleEvent);

		assert lifecycleEvent.counter.get() == 4;

		return lifecycleEvent;
	}

	@GenerateMicroBenchmark
	public LifecycleEvent eventProcessorUtil_process_registered()
		throws ActionException {

		BeanchmarkLifecycleEvent lifecycleEvent =
			new BeanchmarkLifecycleEvent();

		EventsProcessorUtil.process(
			"a.key", _EVENT_CLASSNAMES_A, lifecycleEvent);

		assert lifecycleEvent.counter.get() == 4;

		return lifecycleEvent;
	}

	@GenerateMicroBenchmark
	public LifecycleEvent list() throws ActionException {
		BeanchmarkLifecycleEvent lifecycleEvent =
			new BeanchmarkLifecycleEvent();

		for (LifecycleAction lifecycleAction : _list) {
			lifecycleAction.processLifecycleEvent(lifecycleEvent);
		}

		assert lifecycleEvent.counter.get() == 4;

		return lifecycleEvent;
	}

	@SuppressWarnings("deprecation")
	@Setup(Level.Trial)
	public void setup() throws Exception {
		System.setProperty("catalina.base", ".");
		System.setProperty("external-properties", "portal-test.properties");

		InitUtil.init();

		PropsValues.LIFERAY_WEB_PORTAL_CONTEXT_TEMPDIR =
			System.getProperty(SystemProperties.TMP_DIR);

		ModuleFrameworkUtilAdapter.startFramework();

		SpringUtil.loadContext(null);

		BeanLocator beanLocator = PortalBeanLocatorUtil.getBeanLocator();

		ModuleFrameworkUtilAdapter.registerContext(
			((BeanLocatorImpl)beanLocator).getApplicationContext());

		ModuleFrameworkUtilAdapter.startRuntime();

		_array = new LifecycleAction[4];

		_array[0] = new BenchmarkLifecycleAction();
		_array[1] = new BenchmarkLifecycleAction();
		_array[2] = new BenchmarkLifecycleAction();
		_array[3] = new BenchmarkLifecycleAction();

		_list = new ArrayList<LifecycleAction>();
		_list.add(new BenchmarkLifecycleAction());
		_list.add(new BenchmarkLifecycleAction());
		_list.add(new BenchmarkLifecycleAction());
		_list.add(new BenchmarkLifecycleAction());

		EventsProcessorUtil.registerEvent(
			"a.key", new BenchmarkLifecycleAction());
		EventsProcessorUtil.registerEvent(
			"a.key", new BenchmarkLifecycleAction());
		EventsProcessorUtil.registerEvent(
			"a.key", new BenchmarkLifecycleAction());
		EventsProcessorUtil.registerEvent(
			"a.key", new BenchmarkLifecycleAction());
	}

	@TearDown(Level.Trial)
	public void tearDown() throws Exception {
		BeanLocator beanLocator = PortalBeanLocatorUtil.getBeanLocator();

		beanLocator.destroy();

		ModuleFrameworkUtilAdapter.stopFramework();
	}

	private static final String[] _EVENT_CLASSNAMES_A = new String[0];

	private static final String[] _EVENT_CLASSNAMES_B = new String[] {
		BenchmarkLifecycleAction.class.getName(),
		BenchmarkLifecycleAction.class.getName(),
		BenchmarkLifecycleAction.class.getName(),
		BenchmarkLifecycleAction.class.getName()
	};

	LifecycleAction[] _array;
	List<LifecycleAction> _list;

}
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

package com.liferay.portal.kernel.search;

import com.liferay.petra.messaging.api.Destination;
import com.liferay.petra.messaging.api.DestinationConfiguration;
import com.liferay.petra.messaging.api.DestinationType;
import com.liferay.petra.messaging.api.MessageBus;
import com.liferay.petra.messaging.api.MessageListener;
import com.liferay.petra.messaging.spi.DestinationFactory;
import com.liferay.petra.messaging.spi.InvokerMessageListener;
import com.liferay.portal.kernel.concurrent.CallerRunsPolicy;
import com.liferay.portal.kernel.concurrent.RejectedExecutionHandler;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.messaging.BaseSearchEngineMessageListener;
import com.liferay.portal.kernel.search.messaging.SearchReaderMessageListener;
import com.liferay.portal.kernel.search.messaging.SearchWriterMessageListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceRegistration;
import com.liferay.registry.dependency.ServiceDependencyListener;
import com.liferay.registry.dependency.ServiceDependencyManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael C. Han
 */
public abstract class AbstractSearchEngineConfigurator
	implements SearchEngineConfigurator {

	@Override
	public void afterPropertiesSet() {
		final ServiceDependencyManager serviceDependencyManager =
			new ServiceDependencyManager();

		serviceDependencyManager.addServiceDependencyListener(
			new ServiceDependencyListener() {

				@Override
				public void dependenciesFulfilled() {
					Registry registry = RegistryUtil.getRegistry();

					_messageBusServiceReference = registry.getServiceReference(
						MessageBus.class);

					_messageBus = registry.getService(
						_messageBusServiceReference);

					initialize();
				}

				@Override
				public void destroy() {
				}

			});

		serviceDependencyManager.registerDependencies(
			DestinationFactory.class, MessageBus.class);
	}

	@Override
	public void destroy() {
		for (SearchEngineRegistration searchEngineRegistration :
				_searchEngineRegistrations) {

			destroySearchEngine(searchEngineRegistration);
		}

		_searchEngineRegistrations.clear();

		if (Validator.isNotNull(_originalSearchEngineId)) {
			SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

			searchEngineHelper.setDefaultSearchEngineId(
				_originalSearchEngineId);

			_originalSearchEngineId = null;
		}

		if (_messageBusServiceReference != null) {
			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(_messageBusServiceReference);
		}
	}

	@Override
	public void setSearchEngines(Map<String, SearchEngine> searchEngines) {
		_searchEngines = searchEngines;
	}

	protected void createSearchEngineListeners(
		String searchEngineId, SearchEngine searchEngine,
		Destination searchReaderDestination,
		Destination searchWriterDestination) {

		registerSearchEngineMessageListener(
			searchEngineId, searchEngine, searchReaderDestination,
			new SearchReaderMessageListener(), searchEngine.getIndexSearcher());

		registerSearchEngineMessageListener(
			searchEngineId, searchEngine, searchWriterDestination,
			new SearchWriterMessageListener(), searchEngine.getIndexWriter());
	}

	protected void destroySearchEngine(
		SearchEngineRegistration searchEngineRegistration) {

		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.get(
				searchEngineRegistration.getSearchReaderDestinationName());

		serviceRegistration.unregister();

		serviceRegistration = _serviceRegistrations.get(
			searchEngineRegistration.getSearchReaderDestinationName());

		serviceRegistration.unregister();

		SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

		searchEngineHelper.removeSearchEngine(
			searchEngineRegistration.getSearchEngineId());

		if (!searchEngineRegistration.isOverride()) {
			return;
		}

		SearchEngineProxyWrapper originalSearchEngineProxy =
			searchEngineRegistration.getOriginalSearchEngineProxyWrapper();

		Destination searchReaderDestination = getSearchReaderDestination(
			_messageBus, searchEngineRegistration.getSearchEngineId());

		registerInvokerMessageListener(
			searchReaderDestination,
			searchEngineRegistration.getOriginalSearchReaderMessageListeners());

		Destination searchWriterDestination = getSearchWriterDestination(
			_messageBus, searchEngineRegistration.getSearchEngineId());

		registerInvokerMessageListener(
			searchWriterDestination,
			searchEngineRegistration.getOriginalSearchWriterMessageListeners());

		setSearchEngine(
			searchEngineRegistration.getSearchEngineId(),
			originalSearchEngineProxy);
	}

	protected abstract String getDefaultSearchEngineId();

	protected abstract IndexSearcher getIndexSearcher();

	protected abstract IndexWriter getIndexWriter();

	protected abstract ClassLoader getOperatingClassloader();

	protected abstract SearchEngineHelper getSearchEngineHelper();

	protected Destination getSearchReaderDestination(
		MessageBus messageBus, String searchEngineId) {

		SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

		String searchReaderDestinationName =
			searchEngineHelper.getSearchReaderDestinationName(searchEngineId);

		Destination searchReaderDestination = messageBus.getDestination(
			searchReaderDestinationName);

		if (searchReaderDestination == null) {
			DestinationConfiguration destinationConfiguration =
				new DestinationConfiguration(
					DestinationType.SYNCHRONOUS, searchReaderDestinationName);

			Registry registry = RegistryUtil.getRegistry();

			String className = DestinationConfiguration.class.getName();

			ServiceRegistration<?> serviceRegistration =
				registry.registerService(className, destinationConfiguration);

			_serviceRegistrations.put(className, serviceRegistration);
		}

		searchReaderDestination = messageBus.getDestination(
			searchReaderDestinationName);

		return searchReaderDestination;
	}

	protected Destination getSearchWriterDestination(
		MessageBus messageBus, String searchEngineId) {

		SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

		String searchWriterDestinationName =
			searchEngineHelper.getSearchWriterDestinationName(searchEngineId);

		Destination searchWriterDestination = messageBus.getDestination(
			searchWriterDestinationName);

		if (searchWriterDestination == null) {
			DestinationConfiguration destinationConfiguration = null;

			if (PortalRunMode.isTestMode()) {
				destinationConfiguration = new DestinationConfiguration(
					DestinationType.SYNCHRONOUS, searchWriterDestinationName);
			}
			else {
				destinationConfiguration = new DestinationConfiguration(
					DestinationType.PARALLEL, searchWriterDestinationName);
			}

			if (_INDEX_SEARCH_WRITER_MAX_QUEUE_SIZE > 0) {
				destinationConfiguration.setMaximumQueueSize(
					_INDEX_SEARCH_WRITER_MAX_QUEUE_SIZE);

				RejectedExecutionHandler rejectedExecutionHandler =
					new CallerRunsPolicy() {

						@Override
						public void rejectedExecution(
							Runnable runnable,
							ThreadPoolExecutor threadPoolExecutor) {

							if (_log.isWarnEnabled()) {
								StringBundler sb = new StringBundler(4);

								sb.append("The search index writer's task queue ");
								sb.append("is at its maximum capacity. The ");
								sb.append("current thread will handle the ");
								sb.append("request.");

								_log.warn(sb.toString());
							}

							super.rejectedExecution(runnable, threadPoolExecutor);
						}

					};

				Registry registry = RegistryUtil.getRegistry();

				String className = DestinationConfiguration.class.getName();

				ServiceRegistration<?> serviceRegistration =
					registry.registerService(className, destinationConfiguration);

				_serviceRegistrations.put(className, serviceRegistration);

				className = RejectedExecutionHandler.class.getName();

				Map<String, Object> properties = new HashMap<String, Object>();

				properties.put("destination.name", searchWriterDestinationName);

				ServiceRegistration<?> rehServiceRegistration =
					registry.registerService(
						className, rejectedExecutionHandler, properties);

				_serviceRegistrations.put(className, rehServiceRegistration);
			}
		}

		searchWriterDestination = messageBus.getDestination(
			searchWriterDestinationName);

		return searchWriterDestination;
	}

	protected void initialize() {
		Set<Entry<String, SearchEngine>> entrySet = _searchEngines.entrySet();

		for (Entry<String, SearchEngine> entry : entrySet) {
			initSearchEngine(entry.getKey(), entry.getValue());
		}

		String defaultSearchEngineId = getDefaultSearchEngineId();

		if (Validator.isNotNull(defaultSearchEngineId)) {
			SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

			_originalSearchEngineId =
				searchEngineHelper.getDefaultSearchEngineId();

			searchEngineHelper.setDefaultSearchEngineId(defaultSearchEngineId);
		}

		_searchEngines.clear();
	}

	protected void initSearchEngine(
		String searchEngineId, SearchEngine searchEngine) {

		SearchEngineRegistration searchEngineRegistration =
			new SearchEngineRegistration(searchEngineId);

		_searchEngineRegistrations.add(searchEngineRegistration);

		Destination searchReaderDestination = getSearchReaderDestination(
			_messageBus, searchEngineId);

		searchEngineRegistration.setSearchReaderDestinationName(
			searchReaderDestination.getName());

		Destination searchWriterDestination = getSearchWriterDestination(
			_messageBus, searchEngineId);

		searchEngineRegistration.setSearchWriterDestinationName(
			searchWriterDestination.getName());

		SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

		SearchEngine originalSearchEngine =
			searchEngineHelper.getSearchEngineSilent(searchEngineId);

		if (originalSearchEngine != null) {
			searchEngineRegistration.setOverride(true);

			searchEngineRegistration.setOriginalSearchEngineProxyWrapper(
				(SearchEngineProxyWrapper)originalSearchEngine);

			savePreviousSearchEngineListeners(
				searchReaderDestination, searchWriterDestination,
				searchEngineRegistration);

			ServiceRegistration<?> serviceRegistration =
				_serviceRegistrations.get((searchReaderDestination.getName()));

			serviceRegistration.unregister();

			serviceRegistration = _serviceRegistrations.get(
				(searchWriterDestination.getName()));

			serviceRegistration.unregister();

			searchWriterDestination = getSearchWriterDestination(
				_messageBus, searchEngineId);
		}

		createSearchEngineListeners(
			searchEngineId, searchEngine, searchReaderDestination,
			searchWriterDestination);

		SearchEngineProxyWrapper searchEngineProxyWrapper =
			new SearchEngineProxyWrapper(
				searchEngine, getIndexSearcher(), getIndexWriter());

		setSearchEngine(searchEngineId, searchEngineProxyWrapper);
	}

	protected void registerInvokerMessageListener(
		Destination destination,
		List<InvokerMessageListener> invokerMessageListeners) {

		for (InvokerMessageListener invokerMessageListener :
				invokerMessageListeners) {

			Registry registry = RegistryUtil.getRegistry();

			Map<String, Object> properties = new HashMap<String, Object>();

			properties.put("destination.name", destination.getName());

			registry.registerService(
				MessageListener.class.getName(),
				invokerMessageListener.getMessageListener(), properties);
		}
	}

	protected void registerSearchEngineMessageListener(
		String searchEngineId, SearchEngine searchEngine,
		Destination destination,
		BaseSearchEngineMessageListener baseSearchEngineMessageListener,
		Object manager) {

		baseSearchEngineMessageListener.setManager(manager);
		baseSearchEngineMessageListener.setMessageBus(_messageBus);
		baseSearchEngineMessageListener.setSearchEngine(searchEngine);
		baseSearchEngineMessageListener.setSearchEngineId(searchEngineId);

		Registry registry = RegistryUtil.getRegistry();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("destination.name", destination.getName());

		registry.registerService(
			MessageListener.class.getName(), baseSearchEngineMessageListener,
			properties);
	}

	protected void savePreviousSearchEngineListeners(
		Destination searchReaderDestination,
		Destination searchWriterDestination,
		SearchEngineRegistration searchEngineRegistration) {

		com.liferay.petra.messaging.spi.Destination spiSearchReaderDestination =
			(com.liferay.petra.messaging.spi.Destination)
				searchReaderDestination;

		Collection<MessageListener> searchReaderMessageListeners =
			spiSearchReaderDestination.getMessageListeners();

		for (MessageListener searchReaderMessageListener :
				searchReaderMessageListeners) {

			InvokerMessageListener invokerMessageListener =
				(InvokerMessageListener)searchReaderMessageListener;

			searchEngineRegistration.addOriginalSearchReaderMessageListener(
				invokerMessageListener);
		}

		com.liferay.petra.messaging.spi.Destination spiSearchWriterDestination =
			(com.liferay.petra.messaging.spi.Destination)
				searchWriterDestination;

		Collection<MessageListener> searchWriterMessageListeners =
			spiSearchWriterDestination.getMessageListeners();

		for (MessageListener searchWriterMessageListener :
				searchWriterMessageListeners) {

			InvokerMessageListener invokerMessageListener =
				(InvokerMessageListener)searchWriterMessageListener;

			searchEngineRegistration.addOriginalSearchWriterMessageListener(
				invokerMessageListener);
		}
	}

	protected void setSearchEngine(
		String searchEngineId, SearchEngine searchEngine) {

		SearchEngineHelper searchEngineHelper = getSearchEngineHelper();

		searchEngineHelper.setSearchEngine(searchEngineId, searchEngine);

		searchEngine.initialize(CompanyConstants.SYSTEM);
	}

	private static final int _INDEX_SEARCH_WRITER_MAX_QUEUE_SIZE =
		GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.INDEX_SEARCH_WRITER_MAX_QUEUE_SIZE));

	private static final Log _log = LogFactoryUtil.getLog(
		AbstractSearchEngineConfigurator.class);

	private volatile MessageBus _messageBus;
	private volatile ServiceReference<MessageBus> _messageBusServiceReference;
	private String _originalSearchEngineId;
	private final List<SearchEngineRegistration> _searchEngineRegistrations =
		new ArrayList<>();
	private Map<String, SearchEngine> _searchEngines;
	private final Map<String, ServiceRegistration<?>>
		_serviceRegistrations = new ConcurrentHashMap<>();

	private static class SearchEngineRegistration {

		public void addOriginalSearchReaderMessageListener(
			InvokerMessageListener messageListener) {

			_originalSearchReaderMessageListeners.add(messageListener);
		}

		public void addOriginalSearchWriterMessageListener(
			InvokerMessageListener messageListener) {

			_originalSearchWriterMessageListeners.add(messageListener);
		}

		public SearchEngineProxyWrapper getOriginalSearchEngineProxyWrapper() {
			return _originalSearchEngineProxyWrapper;
		}

		public List<InvokerMessageListener>
			getOriginalSearchReaderMessageListeners() {

			return _originalSearchReaderMessageListeners;
		}

		public List<InvokerMessageListener>
			getOriginalSearchWriterMessageListeners() {

			return _originalSearchWriterMessageListeners;
		}

		public String getSearchEngineId() {
			return _searchEngineId;
		}

		public String getSearchReaderDestinationName() {
			return _searchReaderDestinationName;
		}

		public String getSearchWriterDestinationName() {
			return _searchWriterDestinationName;
		}

		public boolean isOverride() {
			return _override;
		}

		public void setOriginalSearchEngineProxyWrapper(
			SearchEngineProxyWrapper searchEngineProxyWrapper) {

			_originalSearchEngineProxyWrapper = searchEngineProxyWrapper;
		}

		public void setOverride(boolean override) {
			_override = override;
		}

		public void setSearchReaderDestinationName(
			String searchReaderDestinationName) {

			_searchReaderDestinationName = searchReaderDestinationName;
		}

		public void setSearchWriterDestinationName(
			String searchWriterDestinationName) {

			_searchWriterDestinationName = searchWriterDestinationName;
		}

		private SearchEngineRegistration(String searchEngineId) {
			_searchEngineId = searchEngineId;
		}

		private SearchEngineProxyWrapper _originalSearchEngineProxyWrapper;
		private final List<InvokerMessageListener>
			_originalSearchReaderMessageListeners = new ArrayList<>();
		private final List<InvokerMessageListener>
			_originalSearchWriterMessageListeners = new ArrayList<>();
		private boolean _override;
		private final String _searchEngineId;
		private String _searchReaderDestinationName;
		private String _searchWriterDestinationName;

	}

}
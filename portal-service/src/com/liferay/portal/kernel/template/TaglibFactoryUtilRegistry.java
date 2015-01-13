package com.liferay.portal.kernel.template;

import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

public class TaglibFactoryUtilRegistry {
	
	private TaglibFactoryUtilRegistry() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(TaglibFactoryUtil.class);

		_serviceTracker.open();
	}

	private static final TaglibFactoryUtilRegistry _instance =
		new TaglibFactoryUtilRegistry();

	private final ServiceTracker<TaglibFactoryUtil, TaglibFactoryUtil>
		_serviceTracker;

	public static TaglibFactoryUtil getTaglibFactoryUtil() {
		return _instance._serviceTracker.getService();
	}
}

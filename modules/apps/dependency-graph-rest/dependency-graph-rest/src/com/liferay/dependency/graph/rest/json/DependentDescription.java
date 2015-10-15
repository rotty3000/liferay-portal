package com.liferay.dependency.graph.rest.json;

import java.util.ArrayList;
import java.util.List;

public class DependentDescription {

	public DependentDescription(String name) {
		_name = name;
	}

	public void addDependency(String dependency) {
		if (_dependencies.contains(dependency)) {
			return;
		}

		_dependencies.add(dependency);
	}

	public String getName() {
		return _name;
	}

	public List<String> getDependencies() {
		return _dependencies;
	}

	private final List<String> _dependencies = new ArrayList<>();
	private final String _name;

}
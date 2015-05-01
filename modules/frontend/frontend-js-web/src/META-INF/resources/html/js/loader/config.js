(function() {
	var LiferayAUI = Liferay.AUI;

	window.__CONFIG__ = {
		basePath: '',
		combine: LiferayAUI.getCombine(),
		url: LiferayAUI.getComboPath()
	};

	__CONFIG__.modules = {
		'liferay/html/js/liferay/address.es': {
			dependencies: ['exports']
		},
		'liferay/html/js/liferay/nate.es': {
			dependencies: ['exports', 'liferay/html/js/liferay/eduardo.es', 'liferay/html/js/liferay/iliyan.es']
		},
		'liferay/html/js/liferay/eduardo.es': {
			dependencies: ['exports', 'liferay/html/js/liferay/iliyan.es']
		},
		'liferay/html/js/liferay/iliyan.es': {
			dependencies: ['exports']
		}
	};

	__CONFIG__.paths = Liferay.PATHS;
}());

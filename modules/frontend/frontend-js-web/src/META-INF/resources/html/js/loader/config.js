(function() {
	var LiferayAUI = Liferay.AUI;

	window.__CONFIG__ = {
		basePath: '',
		combine: LiferayAUI.getCombine(),
		url: LiferayAUI.getComboPath()
	};

	__CONFIG__.modules = Liferay.MODULES;

	__CONFIG__.paths = Liferay.PATHS;
}());